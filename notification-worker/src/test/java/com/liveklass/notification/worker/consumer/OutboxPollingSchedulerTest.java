package com.liveklass.notification.worker.consumer;

import com.liveklass.notification.application.service.OutboxService;
import com.liveklass.notification.domain.DomainEventOutbox;
import com.liveklass.notification.fixture.DomainEventOutboxFixture;
import com.liveklass.notification.worker.config.WorkerProperties;
import com.liveklass.notification.worker.consumer.dispatcher.OutboxDispatcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@Tag("UNIT_TEST")
@ExtendWith(MockitoExtension.class)
@DisplayName("OutboxPollingScheduler는")
class OutboxPollingSchedulerTest {

    @Mock
    private OutboxService outboxService;

    @Mock
    private OutboxDispatcher outboxDispatcher;

    @Test
    @DisplayName("설정된 batch size로 outbox를 claim하고 처리 결과 저장 메서드를 호출한다")
    void it_claims_with_configured_batch_size_and_saves_processing_results() {
        // given
        final WorkerProperties workerProperties = new WorkerProperties(true, 1000L, 25, 5L);
        final OutboxPollingScheduler scheduler =
                new OutboxPollingScheduler(outboxService, outboxDispatcher, workerProperties);
        final List<DomainEventOutbox> claimed = List.of(DomainEventOutboxFixture.processing());
        given(outboxService.claimPendingOutboxes(any(LocalDateTime.class), eq(25))).willReturn(claimed);
        given(outboxDispatcher.dispatchAll(eq(claimed), any(LocalDateTime.class))).willReturn(claimed);

        // when
        scheduler.poll();

        // then
        verify(outboxService).claimPendingOutboxes(any(LocalDateTime.class), eq(25));
        verify(outboxService).saveAllProcessingResults(claimed);
    }
}
