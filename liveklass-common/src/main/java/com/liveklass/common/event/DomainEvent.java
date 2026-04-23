package com.liveklass.common.event;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;

public interface DomainEvent {

    Topic topic();

    Long recipientId();

    String referenceId();

    LocalDateTime publishedAt();

    JsonNode payload();
}
