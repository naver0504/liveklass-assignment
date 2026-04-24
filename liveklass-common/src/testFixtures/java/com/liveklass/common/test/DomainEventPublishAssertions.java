package com.liveklass.common.test;

import com.liveklass.common.event.DomainEvent;
import com.liveklass.common.event.DomainEventPublisher;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

public final class DomainEventPublishAssertions {

    private DomainEventPublishAssertions() {
    }

    public static <T extends DomainEvent> T assertPublishedEvent(
            final DomainEventPublisher publisher,
            final Class<T> expectedType
    ) {
        final ArgumentCaptor<DomainEvent> captor = ArgumentCaptor.forClass(DomainEvent.class);
        verify(publisher).publish(captor.capture());

        final DomainEvent publishedEvent = captor.getValue();
        assertThat(publishedEvent).isInstanceOf(expectedType);
        return expectedType.cast(publishedEvent);
    }
}
