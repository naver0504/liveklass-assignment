# liveklass-common

`liveklass-common` is the shared contract module for the notification flow.
It owns common event types and the minimum verification commands for downstream modules.

## Compile Verification

Run these commands when the common event contract changes.

```powershell
.\gradlew.bat :liveklass-common:compileJava
.\gradlew.bat :lecture-core:compileJava :notification-core:compileJava :liveklass-api:compileJava :notification-worker:compileJava
```

Checks:

- `liveklass-common` public API compiles after enum, interface, or payload contract changes
- `lecture-core` and `notification-core` keep common as an internal `implementation` dependency
- edge modules add a direct common dependency only when they compile against common types themselves

## Test Verification

Run these commands before merging common contract changes.

```powershell
.\gradlew.bat :liveklass-common:test
.\gradlew.bat :liveklass-common:test --tests "*CommonEventContractTest"
.\gradlew.bat :liveklass-common:test --tests "*EmailPayloadTest" --tests "*InAppPayloadTest"
```

Checks:

- `Topic`, `ChannelType`, `DomainEvent`, and `DomainEventPublisher` keep their expected contracts
- channel payload builders satisfy the minimum `IN_APP` and `EMAIL` payload shape
- null validation on payload builders stays intact

## Contract Checklist

Review these points whenever the common contract changes.

- Adding or renaming `Topic` or `ChannelType` values can affect downstream config keys and persisted values
- Changing the `DomainEvent` signature affects `lecture-core`, `notification-core`, `liveklass-api`, and `notification-worker`
- Changing `DomainEventPublisher` affects adapter implementations in edge modules
- Changing payload builder fields affects event payload shape used by notification registration and dispatch
- If an edge module starts importing common types directly, add `project(':liveklass-common')` there instead of relying on transitive exposure
