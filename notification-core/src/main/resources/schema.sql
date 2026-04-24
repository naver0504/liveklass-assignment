CREATE TABLE IF NOT EXISTS domain_event_outbox (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    idempotency_key VARCHAR(255)    NOT NULL,
    requester_id    BIGINT          NOT NULL,
    recipient_id    BIGINT          NOT NULL,
    topic           VARCHAR(100)    NOT NULL,
    channel_type    VARCHAR(50)     NOT NULL,
    reference_id    VARCHAR(100)    NOT NULL,
    payload         TEXT            NOT NULL,
    status          VARCHAR(50)     NOT NULL,
    locked_at       DATETIME(6),
    attempt_count   INT             NOT NULL DEFAULT 0,
    max_attempts    INT             NOT NULL DEFAULT 3,
    next_attempt_at DATETIME(6)     NOT NULL,
    last_error      TEXT,
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_idempotency_key (idempotency_key),
    KEY idx_polling (status, next_attempt_at, id),
    KEY idx_recipient (recipient_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS in_app_notification (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    outbox_id    BIGINT       NOT NULL,
    recipient_id BIGINT       NOT NULL,
    title        VARCHAR(500) NOT NULL,
    body         TEXT         NOT NULL,
    is_read      BOOLEAN      NOT NULL DEFAULT FALSE,
    published_at DATETIME(6)  NOT NULL,
    created_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_outbox_id (outbox_id),
    KEY idx_recipient_read (recipient_id, is_read, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
