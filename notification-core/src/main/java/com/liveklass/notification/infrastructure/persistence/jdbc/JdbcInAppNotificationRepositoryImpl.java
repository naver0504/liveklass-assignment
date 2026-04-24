package com.liveklass.notification.infrastructure.persistence.jdbc;

import com.liveklass.notification.domain.InAppNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcInAppNotificationRepositoryImpl implements JdbcInAppNotificationRepository {

    private static final String UPSERT_SQL =
            "INSERT INTO in_app_notification " +
            "(outbox_id, recipient_id, title, body, is_read, published_at, created_at, updated_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, now(), now()) " +
            "ON DUPLICATE KEY UPDATE outbox_id = outbox_id";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void upsert(final InAppNotification notification) {
        jdbcTemplate.update(con -> {
            final PreparedStatement ps = con.prepareStatement(UPSERT_SQL);
            ps.setLong(1, notification.outboxId());
            ps.setLong(2, notification.recipientId());
            ps.setString(3, notification.title());
            ps.setString(4, notification.body());
            ps.setBoolean(5, notification.isRead());
            ps.setTimestamp(6, Timestamp.valueOf(notification.publishedAt()));
            return ps;
        });
    }

    @Override
    public void batchInsert(final List<InAppNotification> notifications) {
        jdbcTemplate.batchUpdate(UPSERT_SQL, notifications, notifications.size(), (ps, n) -> {
            ps.setLong(1, n.outboxId());
            ps.setLong(2, n.recipientId());
            ps.setString(3, n.title());
            ps.setString(4, n.body());
            ps.setBoolean(5, n.isRead());
            ps.setTimestamp(6, Timestamp.valueOf(n.publishedAt()));
        });
    }
}
