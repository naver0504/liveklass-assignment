package com.liveklass.notification.worker.consumer;

public sealed interface PublishResult
        permits PublishResult.Success, PublishResult.RetryableFailure, PublishResult.PermanentFailure {

    record Success() implements PublishResult {}

    record RetryableFailure(String errorMessage) implements PublishResult {}

    record PermanentFailure(String errorMessage) implements PublishResult {}
}
