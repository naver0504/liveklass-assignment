package com.liveklass.lecture.controller;

import com.liveklass.lecture.api.LectureApi;
import com.liveklass.lecture.application.service.LectureService;
import com.liveklass.lecture.request.LectureCreateRequest;
import com.liveklass.lecture.response.LectureResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LectureController implements LectureApi {

    private final LectureService lectureService;

    @Override
    public LectureResponse create(final LectureCreateRequest request) {
        return LectureResponse.from(
                lectureService.create(request.title(), request.startAt())
        );
    }
}
