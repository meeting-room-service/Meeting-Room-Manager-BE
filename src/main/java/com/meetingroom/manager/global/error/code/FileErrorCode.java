package com.meetingroom.manager.global.error.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FileErrorCode implements ErrorCode {
    File_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE-01", "파일을 찾을 수 없습니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, "FILE-02", "파일 크기는 최대 10MB까지 업로드할 수 있습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
