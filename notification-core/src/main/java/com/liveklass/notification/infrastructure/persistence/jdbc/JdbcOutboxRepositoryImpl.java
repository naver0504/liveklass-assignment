package com.liveklass.notification.infrastructure.persistence.jdbc;

import com.liveklass.common.event.ChannelType;
import com.liveklass.common.event.Topic;
import com.liveklass.notification.domain.DomainEventOutbox;
import com.liveklass.notification.domain.enums.OutboxStatus;
import com.liveklass.notification.domain.id.OutboxId;
import com.liveklass.notification.domain.vo.EventRef;
import com.liveklass.notification.domain.vo.ProcessingLock;
import com.liveklass.notification.domain.vo.RetryState;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcOutboxRepositoryImpl implements JdbcOutboxRepository {

    private static final String UPSERT_SQL =
            "INSERT INTO domain_event_outbox " +
            "(idempotency_key, requester_id, recipient_id, topic, channel_type, reference_id, payload, " +
            " status, locked_at, attempt_count, max_attempts, next_attempt_at, last_error, created_at, updated_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), now()) " +
            "ON DUPLICATE KEY UPDATE idempotency_key = idempotency_key";

    private static final String BATCH_UPDATE_SQL =
            "UPDATE domain_event_outbox " +
            "SET status=?, locked_at=?, attempt_count=?, max_attempts=?, next_attempt_at=?, last_error=?, updated_at=now() " +
            "WHERE id=?";

    private static final String BATCH_UPDATE_PROCESSING_RESULTS_SQL =
            "UPDATE domain_event_outbox " +
            "SET status=?, locked_at=?, attempt_count=?, max_attempts=?, next_attempt_at=?, last_error=?, updated_at=now() " +
            "WHERE id=? AND status='PROCESSING'";

    private static final String SELECT_PENDING_SQL =
            "SELECT * FROM domain_event_outbox " +
            "WHERE status = 'PENDING' AND next_attempt_at <= ? " +
            "ORDER BY next_attempt_at, id LIMIT ? " +
            "FOR UPDATE SKIP LOCKED";

    static final RowMapper<DomainEventOutbox> ROW_MAPPER = (rs, rowNum) -> {
        final OutboxStatus status = OutboxStatus.valueOf(rs.getString("status"));
        final LocalDateTime lockedAt = rs.getTimestamp("locked_at") != null
                ? rs.getTimestamp("locked_at").toLocalDateTime() : null;
        return DomainEventOutbox.builder()
                .id(new OutboxId(rs.getLong("id")))
                .idempotencyKey(rs.getString("idempotency_key"))
                .requesterId(rs.getLong("requester_id"))
                .recipientId(rs.getLong("recipient_id"))
                .eventRef(new EventRef(
                        Topic.valueOf(rs.getString("topic")),
                        ChannelType.valueOf(rs.getString("channel_type")),
                        rs.getString("reference_id")
                ))
                .payload(rs.getString("payload"))
                .lock(ProcessingLock.of(status, lockedAt))
                .retryState(new RetryState(
                        rs.getInt("attempt_count"),
                        rs.getInt("max_attempts"),
                        rs.getTimestamp("next_attempt_at").toLocalDateTime(),
                        rs.getString("last_error")
                ))
                .build();
    };

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void upsert(final DomainEventOutbox outbox) {
        jdbcTemplate.update(con -> {
            final PreparedStatement ps = con.prepareStatement(UPSERT_SQL);
            ps.setString(1, outbox.idempotencyKey());
            ps.setLong(2, outbox.requesterId());
            ps.setLong(3, outbox.recipientId());
            ps.setString(4, outbox.eventRef().topic().name());
            ps.setString(5, outbox.eventRef().channelType().name());
            ps.setString(6, outbox.eventRef().referenceId());
            ps.setString(7, outbox.payload());
            ps.setString(8, outbox.status().name());
            setNullableTimestamp(ps, 9, outbox.lock().lockedAt());
            ps.setInt(10, outbox.retryState().attemptCount());
            ps.setInt(11, outbox.retryState().maxAttempts());
            ps.setTimestamp(12, Timestamp.valueOf(outbox.retryState().nextAttemptAt()));
            setNullableString(ps, 13, outbox.retryState().lastError());
            return ps;
        });
    }

    @Override
    public void batchUpdate(final List<DomainEventOutbox> outboxes) {
        jdbcTemplate.batchUpdate(BATCH_UPDATE_SQL, outboxes, outboxes.size(), (ps, outbox) -> {
            ps.setString(1, outbox.status().name());
            setNullableTimestamp(ps, 2, outbox.lock().lockedAt());
            ps.setInt(3, outbox.retryState().attemptCount());
            ps.setInt(4, outbox.retryState().maxAttempts());
            ps.setTimestamp(5, Timestamp.valueOf(outbox.retryState().nextAttemptAt()));
            setNullableString(ps, 6, outbox.retryState().lastError());
            ps.setLong(7, outbox.id().id());
        });
    }

    @Override
    public void batchUpdateProcessingResults(final List<DomainEventOutbox> outboxes) {
        jdbcTemplate.batchUpdate(BATCH_UPDATE_PROCESSING_RESULTS_SQL, outboxes, outboxes.size(), (ps, outbox) -> {
            ps.setString(1, outbox.status().name());
            setNullableTimestamp(ps, 2, outbox.lock().lockedAt());
            ps.setInt(3, outbox.retryState().attemptCount());
            ps.setInt(4, outbox.retryState().maxAttempts());
            ps.setTimestamp(5, Timestamp.valueOf(outbox.retryState().nextAttemptAt()));
            setNullableString(ps, 6, outbox.retryState().lastError());
            ps.setLong(7, outbox.id().id());
        });
    }

    @Override
    public List<DomainEventOutbox> findPendingBefore(final LocalDateTime scheduledAt, final int limit) {
        return jdbcTemplate.query(SELECT_PENDING_SQL, ROW_MAPPER, Timestamp.valueOf(scheduledAt), limit);
    }

    private static void setNullableTimestamp(final PreparedStatement ps, final int idx, final LocalDateTime value)
            throws java.sql.SQLException {
        if (value != null) {
            ps.setTimestamp(idx, Timestamp.valueOf(value));
        } else {
            ps.setNull(idx, Types.TIMESTAMP);
        }
    }

    private static void setNullableString(final PreparedStatement ps, final int idx, final String value)
            throws java.sql.SQLException {
        if (value != null) {
            ps.setString(idx, value);
        } else {
            ps.setNull(idx, Types.VARCHAR);
        }
    }
}
