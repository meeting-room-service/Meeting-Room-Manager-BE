package com.meetingroom.manager.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.ConstraintViolationException;
import com.meetingroom.manager.global.error.ErrorResponse;
import com.meetingroom.manager.global.error.code.CommonErrorCode;
import com.meetingroom.manager.global.error.code.ErrorCode;
import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드를 JSON에 포함하지 않음
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private ErrorResponse error;

    private ApiResponse(boolean success, T data, ErrorResponse error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(false, null, ErrorResponse.of(errorCode));
    }

    public static <T> ApiResponse<T> validationError(BindingResult bindingResult) {
        return new ApiResponse<>(false, null, ErrorResponse.of(CommonErrorCode.INVALID_REQUEST, bindingResult));
    }

    public static <T> ApiResponse<T> validationError(ConstraintViolationException ex) {
        return new ApiResponse<>(false, null, ErrorResponse.of(CommonErrorCode.INVALID_REQUEST, ex));
    }
}