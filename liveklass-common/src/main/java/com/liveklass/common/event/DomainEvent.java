package com.liveklass.common.event;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

/**
 * 도메인 이벤트 계약.
 *
 * <p>수강신청 완료, 결제 확정 등 도메인 사실을 표현하는 모든 이벤트가 구현한다.
 * 이벤트 record는 {@link #payload()}를 통해 채널별 최소 계약을 만족하는 JSON을 반환한다.
 *
 * <ul>
 *   <li>IN_APP: {@code title}, {@code body} 필수
 *   <li>EMAIL: {@code subject}, {@code body}, {@code metadata.recipientEmail} 필수
 * </ul>
 *
 * <p>{@code channelType}은 이 인터페이스에 노출하지 않는다.
 * 채널 결정은 {@code liveklass-api}의 {@code TopicChannelPolicy}가 담당한다.
 *
 * <pre>{@code
 * public record EnrollmentCompletedEvent(...) implements DomainEvent {
 *
 *     @Override public Topic topic() { return Topic.LECTURE_ENROLLMENT_COMPLETED; }
 *
 *     @Override
 *     public JsonNode payload() {
 *         return InAppPayload.builder("수강 신청이 완료되었습니다", lectureTitle + " 수강 신청이 완료되었습니다.")
 *             .metadata("screen", "LECTURE_DETAIL")
 *             .metadata("enrollmentId", enrollmentId)
 *             .build();
 *     }
 * }
 * }</pre>
 */
public interface DomainEvent {

    Topic topic();

    Long recipientId();

    String referenceId();

    LocalDateTime publishedAt();

    JsonNode payload();
}
