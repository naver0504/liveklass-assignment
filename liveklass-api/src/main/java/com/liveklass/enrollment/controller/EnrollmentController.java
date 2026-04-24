package com.liveklass.enrollment.controller;

import com.liveklass.enrollment.api.EnrollmentApi;
import com.liveklass.enrollment.request.EnrollmentCancelRequest;
import com.liveklass.enrollment.request.EnrollmentRequest;
import com.liveklass.lecture.application.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class EnrollmentController implements EnrollmentApi {

    private final EnrollmentService enrollmentService;

    @Override
    public void enroll(final Long userId, final EnrollmentRequest request) {
        enrollmentService.enroll(request.lectureId(), userId, LocalDateTime.now());
    }

    @Override
    public void cancel(final Long enrollmentId, final Long userId, final EnrollmentCancelRequest request) {
        final String cancelReason = request != null ? request.cancelReason() : null;
        enrollmentService.cancel(enrollmentId, userId, cancelReason, LocalDateTime.now());
    }
}
