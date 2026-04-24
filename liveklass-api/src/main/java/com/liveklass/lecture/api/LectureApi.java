package com.liveklass.lecture.api;

import com.liveklass.common.dto.ErrorResponse;
import com.liveklass.lecture.request.LectureCreateRequest;
import com.liveklass.lecture.response.LectureResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Lecture", description = "강의 API")
@RequestMapping("/api/lectures")
public interface LectureApi {

    @Operation(summary = "강의 생성", description = "새 강의를 등록합니다.")
    @ApiResponse(responseCode = "201", description = "강의 생성 완료")
    @ApiResponse(responseCode = "400", description = "요청 값 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    LectureResponse create(@Valid @RequestBody LectureCreateRequest request);
}
