package com.liveklass.notification.infrastructure.persistence.entity;

import com.liveklass.common.event.ChannelType;
import com.liveklass.common.event.Topic;
import com.liveklass.notification.domain.enums.OutboxStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "domain_event_outbox")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OutboxJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @Column(name = "requester_id", nullable = false)
    private Long requesterId;

    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    @Column(name = "topic", nullable = false)
    @Enumerated(EnumType.STRING)
    private Topic topic;

    @Column(name = "channel_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ChannelType channelType;

    @Column(name = "reference_id")
    private String referenceId;

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "max_attempts", nullable = false)
    private int maxAttempts;

    @Column(name = "next_attempt_at", nullable = false)
    private LocalDateTime nextAttemptAt;

    @Column(name = "last_error")
    private String lastError;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
