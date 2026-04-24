# 알림 발송 시스템 (Notification Worker)

## 프로젝트 개요

수강 신청 완료·결제 확정·강의 시작 D-1 등 다양한 도메인 이벤트 발생 시 사용자에게 이메일(EMAIL) 또는 인앱(IN_APP) 알림을 비동기로 발송하는 시스템입니다.

**Transactional Outbox 패턴**을 채택해 알림 처리 실패가 비즈니스 트랜잭션에 영향을 주지 않도록 설계했으며, 실제 메시지 브로커 없이 DB 폴링 방식으로 구현하면서도 Kafka 등 외부 브로커로의 전환이 가능한 추상화 구조를 유지했습니다.

```
assignment/
├── liveklass-common        # 공통 예외·이벤트 계약 (DomainEvent, ChannelType, PayloadValidator …)
├── notification-core       # 도메인, 애플리케이션 서비스, 인프라 어댑터
│   ├── domain/             # DomainEventOutbox, InAppNotification, OutboxStatus …
│   ├── application/        # OutboxService, InAppNotificationService, Port 인터페이스
│   └── infrastructure/     # JPA Entity, JdbcRepository 구현체, JpaRepository 인터페이스
├── notification-worker     # 스케줄러·디스패처·퍼블리셔 (발송 워커)
│   ├── consumer/           # OutboxPollingScheduler, OutboxDispatcher, Publisher 구현체
│   ├── producer/           # DomainEventPublisherAdapter (Outbox 저장 진입점)
│   ├── recovery/           # StuckOutboxRecoveryScheduler
│   └── config/             # WorkerProperties, SchedulerConfig
├── lecture-core            # 강의 도메인 참조용
└── liveklass-api           # REST API 레이어
```

---

## 기술 스택

| 영역 | 기술 |
|------|------|
| 언어 / 런타임 | Java 21, Spring Boot 4.0.5 |
| 영속성 | Spring Data JPA + Spring JDBC (MySQL) |
| 비동기 처리 | Spring `@Scheduled` + `CompletableFuture` |
| 빌드 | Gradle Multi-Module |
| 테스트 | JUnit 5 |

---

## 실행 방법

### 사전 준비

- JDK 21
- Docker
- MySQL 8
- 포트 `3306`, `8080`

기본 설정값은 아래와 같습니다.

| 항목 | 값 |
|------|----|
| DB URL | `jdbc:mysql://localhost:3306/assignment` |
| DB USER | `root` |
| DB PASSWORD | `SPRING_DATASOURCE_PASSWORD` 환경 변수 |
| API PORT | `8080` |

### 1. MySQL 실행

```bash
docker run -d --name mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=assignment \
  -p 3306:3306 mysql:8
```

### 2. 스키마 적용

`liveklass-api`는 `ddl-auto=validate`, `notification-worker`는 `ddl-auto=none`이라 스키마를 수동으로 넣어야 합니다.

```bash
docker cp notification-core/src/main/resources/schema.sql mysql:/schema.sql
docker exec mysql sh -c "mysql -uroot -proot assignment < /schema.sql"
```

### 3. 기본 검증

```bash
./gradlew :notification-core:test :notification-worker:test :liveklass-api:compileJava
```

### 4. API 서버 실행

PowerShell:

```powershell
$env:SPRING_DATASOURCE_PASSWORD="root"
./gradlew :liveklass-api:bootRun
```

Bash:

```bash
SPRING_DATASOURCE_PASSWORD=root ./gradlew :liveklass-api:bootRun
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

### 5. Worker 실행

API와 worker는 서로 다른 터미널에서 띄웁니다.

PowerShell:

```powershell
$env:SPRING_DATASOURCE_PASSWORD="root"
./gradlew :notification-worker:bootRun
```

Bash:

```bash
SPRING_DATASOURCE_PASSWORD=root ./gradlew :notification-worker:bootRun
```

> 이메일 실제 발송은 불필요합니다. `MockEmailNotificationSender`가 로그로 대체하고, recovery 확인용 `isSent(idempotencyKey)`는 9:1 정도로 `true`를 반환합니다.

### 6. 수동 확인 예시

수강 신청 (인앱 알림 발송까지 연결):

```bash
curl -i -X POST http://localhost:8080/api/enrollments \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1001" \
  -d '{"lectureId": 1}'
```

알림 발송 직접 요청 (EMAIL, 즉시 발송):

```bash
curl -i -X POST http://localhost:8080/api/notifications \
  -H "Content-Type: application/json" \
  -d '{
    "recipientId": 1001,
    "channelType": "EMAIL",
    "referenceId": 1,
    "body": "결제가 정상적으로 완료되었습니다.",
    "subject": "결제 완료",
    "recipientEmail": "user@example.com"
  }'
```

알림 발송 요청 (IN_APP, 예약 발송):

```bash
curl -i -X POST http://localhost:8080/api/notifications \
  -H "Content-Type: application/json" \
  -d '{
    "recipientId": 1001,
    "channelType": "IN_APP",
    "referenceId": 1,
    "title": "강의 시작 D-1 알림",
    "body": "내일 강의가 시작됩니다.",
    "scheduledAt": "2026-04-25T09:00:00"
  }'
```

상태 조회에 필요한 `outboxId`는 DB 또는 Swagger에서 확인합니다.

```bash
docker exec mysql mysql -uroot -proot -D assignment \
  -e "SELECT id, topic, channel_type, status, attempt_count FROM domain_event_outbox ORDER BY id DESC LIMIT 5;"
```

상태 조회:

```bash
curl -H "X-User-Id: 1001" http://localhost:8080/api/notifications/1
```

인앱 알림 목록 조회 (읽지 않은 것만):

```bash
curl -H "X-User-Id: 1001" "http://localhost:8080/api/in-app-notifications?isRead=false"
```

읽음 처리:

```bash
curl -i -X PATCH http://localhost:8080/api/in-app-notifications/1/read \
  -H "X-User-Id: 1001"
```

---

## API 목록 및 예시

인증/인가는 `X-User-Id` 헤더로 사용자 ID를 전달하는 방식으로 간략히 처리합니다.

Swagger UI: `http://localhost:8080/swagger-ui/index.html`

---

### 알림 발송 (`/api/notifications`)

#### 1. 알림 발송 요청

```
POST /api/notifications
```

**Request**
```json
{
  "recipientId": 1001,
  "channelType": "EMAIL",
  "referenceId": 1,
  "subject": "결제가 완료되었습니다",
  "body": "안녕하세요. 결제가 정상적으로 완료되었습니다.",
  "recipientEmail": "user@example.com",
  "scheduledAt": "2026-04-25T09:00:00"
}
```

**Response** `202 Accepted` — 응답 바디 없음

- `channelType`이 `IN_APP`이면 `title`, `body` 필수
- `channelType`이 `EMAIL`이면 `subject`, `body`, `recipientEmail` 필수
- `scheduledAt` 생략 시 즉시 발송 (`nextAttemptAt = now`)

---

#### 2. 알림 상태 조회

```
GET /api/notifications/{id}
X-User-Id: 1001
```

**Response** `200 OK`
```json
{
  "id": 42,
  "topic": "EMAIL_NOTIFICATION_REQUEST",
  "channelType": "EMAIL",
  "referenceId": "1",
  "status": "발송완료",
  "attemptCount": 1,
  "maxAttempts": 3,
  "nextAttemptAt": "2026-04-25T09:00:00",
  "lastError": null
}
```

`status` 표시값:

| 값 | 내부 상태 |
|----|-----------|
| `대기중` | `PENDING` |
| `처리중` | `PROCESSING` |
| `발송완료` | `SENT` |
| `최종실패` | `DEAD_LETTER` |

---

#### 3. DEAD_LETTER 수동 재시도

```
POST /api/notifications/{id}/retry
```

**Response** `200 OK` — 응답 바디 없음

`DEAD_LETTER` 상태인 outbox를 `PENDING`으로 되돌리고 `attemptCount`를 초기화합니다.

---

### 인앱 알림 (`/api/in-app-notifications`)

#### 4. 인앱 알림 목록 조회

```
GET /api/in-app-notifications?isRead=false
X-User-Id: 1001
```

`isRead` 파라미터를 생략하면 읽음/안읽음 구분 없이 전체 목록을 반환합니다.

**Response** `200 OK`
```json
[
  {
    "id": 1,
    "title": "수강 신청이 완료되었습니다",
    "body": "Spring Boot 실전 강의 수강이 완료되었습니다.",
    "isRead": false,
    "publishedAt": "2026-04-24T10:00:00",
    "createdAt": "2026-04-24T10:00:01"
  }
]
```

---

#### 5. 인앱 알림 읽음 처리

```
PATCH /api/in-app-notifications/{id}/read
X-User-Id: 1001
```

**Response** `200 OK` — 응답 바디 없음

본인 알림이 아닌 경우 `403 Forbidden`, 알림이 없는 경우 `404 Not Found`를 반환합니다.

---

### 수강 신청 (`/api/enrollments`)

#### 6. 수강 신청

```
POST /api/enrollments
X-User-Id: 1001
```

**Request**
```json
{
  "lectureId": 10
}
```

**Response** `201 Created` — 응답 바디 없음

수강 신청 완료 시 `EnrollmentCompletedEvent`가 발행되어 인앱 알림이 비동기로 발송됩니다.

| 상황 | 응답 코드 |
|------|-----------|
| 성공 | 201 |
| 강의 없음 | 404 |
| 이미 수강 중 | 409 |
| 취소 후 재신청 | 409 |

---

#### 7. 수강 취소

```
DELETE /api/enrollments/{enrollmentId}
X-User-Id: 1001
```

**Request Body** (선택)
```json
{
  "cancelReason": "개인 사정으로 인한 취소"
}
```

**Response** `200 OK` — 응답 바디 없음

본인 수강 신청이 아닌 경우 `403 Forbidden`, 수강 신청이 없는 경우 `404 Not Found`를 반환합니다.

---

## 데이터 모델 설명

### `domain_event_outbox`

발송 요청의 전체 생명 주기를 관리하는 핵심 테이블입니다. 비즈니스 트랜잭션과 동일한 DB에 저장되어 메시지 유실을 방지합니다.

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT | PK |
| `idempotency_key` | VARCHAR UNIQUE | `topic:recipientId:channelType:referenceId` 콜론 구분 문자열, UNIQUE 제약 |
| `requester_id` | BIGINT | 요청자 ID |
| `recipient_id` | BIGINT | 수신자 ID |
| `topic` | VARCHAR | 내부 알림 요청 토픽 (`IN_APP_NOTIFICATION_REQUEST`, `EMAIL_NOTIFICATION_REQUEST`) |
| `channel_type` | VARCHAR | `EMAIL` / `IN_APP` |
| `reference_id` | VARCHAR | 관련 도메인 식별자 (강의 ID 등) |
| `status` | VARCHAR | `PENDING` → `PROCESSING` → `SENT` / `DEAD_LETTER` |
| `payload` | TEXT | 채널별 JSON 직렬화 발송 데이터 |
| `attempt_count` | INT | 현재까지 시도 횟수 |
| `max_attempts` | INT | 채널별 최대 재시도 횟수 (WorkerProperties로 설정) |
| `next_attempt_at` | DATETIME | 다음 시도 예정 시각 (지수 백오프 계산값) |
| `locked_at` | DATETIME | PROCESSING 전환 시각 (stuck 감지 기준) |
| `last_error` | TEXT | 최근 실패 사유 |

**상태 전이 다이어그램**

```
PENDING ──[워커 폴링]──► PROCESSING ──[발송 성공]──► SENT
                              │
                         [발송 실패]
                              │
                    retryable && attempt < max_attempts
                              ▼
            PENDING (attempt_count 증가, next_attempt_at 갱신)
                              │
               permanent failure 또는 최대 재시도 초과
                              ▼
                         DEAD_LETTER
```

---

### `in_app_notification`

인앱 알림 전용 테이블입니다. `outbox_id`에 UNIQUE 제약을 걸어 Outbox 처리와 별개로 중복 저장을 차단합니다.

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT | PK |
| `outbox_id` | BIGINT UNIQUE | 멱등성 보장 — 같은 Outbox로 두 번 저장 불가 |
| `recipient_id` | BIGINT | 수신자 ID |
| `title` | VARCHAR | 알림 제목 |
| `body` | TEXT | 알림 본문 |
| `is_read` | BOOLEAN | 읽음 여부 (기본값 `false`) |
| `published_at` | DATETIME | 발송 시각 |

---

## 요구사항 해석 및 가정

### 해석한 내용

**"알림 처리 실패가 비즈니스 트랜잭션에 영향을 주어서는 안 된다. 단, 예외를 단순히 무시하는 방식은 안 된다."**

단순히 `try-catch`로 삼키는 방식이 아니라, 알림 발송 요청 자체를 DB에 기록(Outbox)한 뒤 워커가 비동기로 처리하도록 분리해야 한다는 의미로 해석했습니다. 비즈니스 트랜잭션(수강 신청, 결제 등)이 커밋되면 Outbox 레코드도 함께 커밋되므로, 이후 발송 실패는 재시도로 보완되며 비즈니스 결과에 영향을 주지 않습니다.

**"실제 메시지 브로커 없이, 전환 가능한 구조"**

`DomainEventPublisher` 인터페이스와 `NotificationPublisher` 인터페이스를 추상화 경계로 두어, 현재 구현체(`DomainEventPublisherAdapter` → Outbox 저장, `EmailNotificationPublisher` → Mock 발송)를 Kafka 기반 구현체로 교체해도 상위 레이어가 변경되지 않도록 설계했습니다.

**"동일한 이벤트에 대해 알림이 중복 발송되면 안 된다."**

두 가지 레벨에서 보장했습니다.
- **Outbox 레벨**: `idempotency_key = topic:recipientId:channelType:referenceId` UNIQUE 제약 + `ON DUPLICATE KEY UPDATE`. 동시에 같은 요청이 여러 번 들어와도 하나만 저장됩니다.
- **IN_APP 발송 레벨**: `in_app_notification.outbox_id` UNIQUE 제약. Outbox가 두 번 처리되더라도 인앱 알림이 중복 생성되지 않습니다.

**"다중 인스턴스 환경에서도 동일 알림이 중복 처리되어서는 안 된다."**

`SELECT ... FOR UPDATE SKIP LOCKED`와 `UPDATE status = PROCESSING`을 반드시 **단일 `@Transactional` 내**에서 실행해야 합니다. SELECT와 UPDATE가 별도 트랜잭션으로 분리되면 SELECT가 끝나는 순간 락이 해제되어 다른 인스턴스가 같은 row를 가져갈 수 있기 때문입니다.

### 가정한 내용

| 항목 | 가정 |
|------|------|
| 발송 채널 | EMAIL / IN_APP 두 채널만 존재 (SMS 등 미정의) |
| 예약 발송 | `NotificationSendRequest`의 `scheduledAt` 필드로 예약 가능. 생략 시 즉시 발송 |
| 이메일 실제 발송 | Mock 로그 출력으로 대체 |
| 요청자 식별 | 실제 인증 없이 `X-User-Id` 헤더로 전달된 값을 사용자 ID로 간주 |
| 읽음 처리 동시성 | 여러 기기에서 동시에 읽음 처리 요청이 오더라도 `UPDATE is_read = true WHERE id = ?` 자체가 멱등하므로 별도 락 불필요 |

---

## 설계 결정과 이유

### 비동기 처리 구조

#### Transactional Outbox 패턴 선택

알림 발송을 API 요청 스레드에서 직접 처리하면 외부 이메일 서버 장애 시 비즈니스 트랜잭션(수강 신청 등)이 함께 실패합니다. Outbox 테이블을 비즈니스 DB에 두어 트랜잭션 경계를 맞추고, 워커가 별도로 폴링해 발송하는 구조를 선택했습니다.

```
[비즈니스 트랜잭션]  ─┐
                      ├─ COMMIT ──► domain_event_outbox 행 영구 저장
[Outbox 저장]       ─┘

[OutboxPollingScheduler] ── @Scheduled(fixedDelay) ──► 독립된 스레드에서 발송 처리
```

이 구조 덕분에 이메일 서버가 다운되더라도 수강 신청 자체는 성공하고, 알림은 복구 후 재시도됩니다.

#### FOR UPDATE SKIP LOCKED 트랜잭션 경계

다중 인스턴스 환경에서 동일 Outbox를 중복 처리하지 않기 위해 `FOR UPDATE SKIP LOCKED`를 사용하지만, **SELECT와 PROCESSING 갱신이 반드시 같은 트랜잭션**이어야 합니다.

```java
// ❌ 잘못된 구조: SELECT 트랜잭션이 끝나는 순간 락 해제 → 다른 인스턴스가 동일 row 취득 가능
@Transactional(readOnly = true)
List<DomainEventOutbox> findPendingOutboxes(...) { ... SELECT FOR UPDATE SKIP LOCKED ... }

outboxRepository.saveAll(pendingList);  // 별도 트랜잭션 → 이미 락이 없는 상태

// ✅ 올바른 구조: SELECT + UPDATE를 단일 트랜잭션으로 묶어 락 보유 중에 상태 변경
@Transactional
List<DomainEventOutbox> claimPendingOutboxes(LocalDateTime now, int batchSize) {
    List<...> rows = jdbcRepository.findPendingBefore(now, batchSize); // FOR UPDATE SKIP LOCKED
    jdbcRepository.batchUpdate(rows);  // UPDATE status = PROCESSING — 같은 트랜잭션
    return rows;
}
```

#### 채널별 발송 전략 분리

`OutboxDispatcher`는 채널별로 `publisher.publishBatch(list)`를 호출할 뿐, 내부가 병렬인지 배치인지 알지 못합니다. 발송 전략은 각 `NotificationPublisher` 구현체에 캡슐화했습니다.

```
OutboxDispatcher
  └─ channelType별 publisher.publishBatch(outboxes)
       ├── InAppNotificationPublisher  → JDBC batchInsert (DB 쓰기, 배치가 효율적)
       └── EmailNotificationPublisher  → CompletableFuture.supplyAsync (외부 I/O, 병렬이 효율적)
```

#### 이벤트 수신 시점 payload 검증

다른 컨텍스트에서도 동일 이벤트를 발행할 수 있으므로, worker/API의 `DomainEventPublisher` 어댑터 진입점에서 채널별 필수 값을 먼저 검증합니다.

- `IN_APP`: `title`, `body`
- `EMAIL`: `subject`, `body`, `metadata.recipientEmail`

유효하지 않은 payload는 outbox에 저장하지 않습니다.

#### JPA + JDBC 병행 사용

| 용도 | 기술 선택 | 이유 |
|------|-----------|------|
| 단건 CRUD, 상태별 조회 | JPA (`JpaOutboxRepository`) | 타입 안전, 간결한 쿼리 |
| `ON DUPLICATE KEY UPDATE` upsert | JDBC (`JdbcOutboxRepository`) | JPA가 네이티브 upsert를 지원하지 않음 |
| `FOR UPDATE SKIP LOCKED` 폴링 | JDBC | JPQL이 해당 힌트를 직접 지원하지 않음 |
| 대량 배치 업데이트 | JDBC `batchUpdate` | JPA `saveAll`보다 네트워크 왕복 횟수 최소화 |

#### PublishResult와 상태 갱신 분리

발송 결과를 `sealed interface PublishResult { Success, RetryableFailure, PermanentFailure }`로 표현하고, Dispatcher가 패턴 매칭으로 Outbox 상태를 갱신합니다. 이때 두 가지 `batchUpdate`를 구분합니다.

- `saveAll()` — 상태 가드 없음. claim 단계에서 PENDING → PROCESSING 전환 시 사용
- `saveAllProcessingResults()` — `WHERE status = 'PROCESSING'` 조건 추가. 발송 결과 반영 시 사용

결과 반영 시 가드를 두는 이유: 복구 스케줄러가 동시에 해당 row를 PENDING으로 롤백했을 경우, 오래된 발송 결과가 덮어쓰지 않도록 보호합니다.

#### Stuck 처리 복구

서버 재시작이나 워커 크래시로 `PROCESSING` 상태가 일정 시간 이상 지속되는 경우, `StuckOutboxRecoveryScheduler`가 `locked_at` 기준으로 탐지합니다.

채널별로 `OutboxRecoveryVerifier`를 구현해 실제 발송 여부를 확인합니다.
- IN_APP: `in_app_notification.outbox_id` 존재 여부 (DB 조회)
- EMAIL: `EmailNotificationSender.isSent(idempotencyKey)` (외부 서버 조회)

실제 발송이 완료됐다면 `SENT`, 아니라면 `PENDING` 또는 `DEAD_LETTER`로 전이시킵니다.

---

## 요구사항 해석 및 개선 의견

### 선택 구현 — 발송 스케줄링

`next_attempt_at` 컬럼이 이미 존재합니다. 폴링 쿼리의 `WHERE next_attempt_at <= now` 조건 덕분에, Outbox 생성 시점을 미래로 잡으면 별도 예약 큐 없이도 지연 발송 구조를 확장할 수 있습니다. 재시도 정책(지수 백오프)도 동일한 컬럼을 재사용합니다.

예약 전용 API가 필요하다면 `NotificationController` 요청 DTO에 예약 시각을 추가하고 `createOutbox`의 `nextAttemptAt`으로 연결하면 됩니다.

### 개선 의견

**1. Hash 파티셔닝을 통한 선형 확장**

현재 구조는 `SKIP LOCKED`로 인스턴스 간 경쟁하지만, 부하가 높아지면 폴링 경합이 증가합니다. `recipient_id % N`으로 파티션을 나눠 워커 인스턴스에 고정 파티션을 할당하면 경쟁 없이 선형 확장이 가능합니다.

```sql
-- 파티션 0번 담당 워커 (N=4)
SELECT * FROM domain_event_outbox
WHERE status = 'PENDING'
  AND next_attempt_at <= NOW()
  AND recipient_id % 4 = 0
FOR UPDATE SKIP LOCKED
LIMIT 100;
```

파티션 키를 `recipient_id`로 잡으면 동일 수신자 이벤트가 항상 같은 워커로 라우팅되어 순서 보장도 부수적으로 얻을 수 있습니다.

**2. 이메일 발송 확인의 한계**

현재 구현은 recovery 시 `EmailNotificationSender.isSent(idempotencyKey)`를 호출해 실제 발송 여부를 확인합니다. 과제 구현에서는 `MockEmailNotificationSender`가 이 값을 임의로 반환하지만, 운영 환경에서는 같은 `idempotencyKey`로 조회 가능한 메일 프로바이더나 별도 delivery ledger가 필요합니다.

그 보장이 없으면 서버가 `send()` 직후 크래시한 경우, recovery가 이미 발송된 메일을 구분하지 못해 중복 발송 가능성이 남습니다.

---

## 테스트 실행 방법

```bash
# notification-core 단위 테스트
./gradlew :notification-core:test

# notification-worker 단위 테스트
./gradlew :notification-worker:test

# 전체
./gradlew test
```

---

## 미구현 / 제약사항

| 항목 | 내용 |
|------|------|
| 이메일 실제 발송 | `MockEmailNotificationSender`로 로그 대체 |
| 알림 템플릿 관리 | 선택 구현 — 미구현 |
| 알림 요청 응답 본문 | `POST /api/notifications`는 `outboxId`를 반환하지 않음 (상태 조회는 DB에서 id 확인 필요) |
| 인증/인가 | 실제 JWT 검증 없이 `X-User-Id` 헤더 직접 신뢰 |

---

## AI 활용 범위

설계는 직접 수행했습니다. Transactional Outbox 패턴 적용, 채널별 발송 전략 분리, `FOR UPDATE SKIP LOCKED`와 PROCESSING 갱신을 단일 트랜잭션으로 묶는 구조, 멱등성 키 설계, 지수 백오프 정책 등 핵심 설계 결정은 모두 직접 수립했습니다.

전체적인 구현은 Codex와 Claude Code를 활용했습니다. 설계안을 제시하고 각 요구사항을 만족하도록 수정하며, 원하는 방향으로 코드를 이끌었고, 최종 코드 리뷰까지 수행했습니다.
