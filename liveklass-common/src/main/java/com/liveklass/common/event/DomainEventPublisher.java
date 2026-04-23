package com.liveklass.common.event;

/**
 * 도메인 이벤트 발행 포트.
 *
 * <p>lecture-core의 도메인 서비스가 이 포트를 통해 이벤트를 발행한다.
 * 실제 구현(adapter)은 실행 컨텍스트(liveklass-api, notification-worker)에서 주입된다.
 *
 * <pre>{@code
 * // lecture-core의 EnrollmentService — 포트만 알고 구현체는 모른다
 * eventPublisher.publish(new EnrollmentCompletedEvent(...));
 *
 * // 단위 테스트 — 람다로 간단히 교체
 * DomainEventPublisher publisher = capturedEvents::add;
 * }</pre>
 */
@FunctionalInterface
public interface DomainEventPublisher {

    void publish(DomainEvent event);
}
