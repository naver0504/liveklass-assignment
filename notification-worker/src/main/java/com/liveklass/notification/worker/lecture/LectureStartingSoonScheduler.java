package com.liveklass.notification.worker.lecture;

import com.liveklass.common.event.DomainEventPublisher;
import com.liveklass.lecture.application.service.LectureStartingSoonQueryService;
import com.liveklass.lecture.domain.event.LectureStartingSoonEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LectureStartingSoonScheduler {

    private final LectureStartingSoonQueryService queryService;
    private final DomainEventPublisher eventPublisher;

    @Scheduled(cron = "0 0 0 * * *")
    public void publishDayBeforeNotifications() {
        final LocalDate tomorrow = LocalDate.now().plusDays(1);
        final LocalDateTime now = LocalDateTime.now();

        final List<LectureStartingSoonEvent> events = queryService.findEventsFor(tomorrow, now);
        if (events.isEmpty()) {
            return;
        }

        events.forEach(eventPublisher::publish);
        log.info("[LectureStartingSoonScheduler] D-1 알림 {} 건 발행 (target={})", events.size(), tomorrow);
    }
}
