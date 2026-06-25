package com.meetingroom.manager.global.error.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReservationErrorCode implements ErrorCode {
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION-01", "예약을 찾을 수 없습니다."),
    RESERVATION_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "RESERVATION-02", "해당 시간에 이미 예약이 존재합니다."),
    RESERVATION_ROOM_UNDER_INSPECTION(HttpStatus.BAD_REQUEST, "RESERVATION-03", "선택한 회의실은 해당 시간에 점검 중입니다."),
    RESERVATION_CAPACITY_EXCEEDED(HttpStatus.BAD_REQUEST, "RESERVATION-04", "회의 참가 인원이 회의실 최대 수용 인원을 초과합니다."),
    RESERVATION_PARTICIPANT_UNAVAILABLE(HttpStatus.BAD_REQUEST, "RESERVATION-05", "선택한 참가자 중 해당 시간에 다른 예약이 있는 사용자가 있습니다."),
    RESERVATION_PARTICIPANT_NOT_IN_COMPANY(HttpStatus.BAD_REQUEST, "RESERVATION-06", "같은 회사 소속이 아닌 사용자는 참가자로 선택할 수 없습니다."),
    RESERVATION_NOT_MODIFIABLE_CANCELED(HttpStatus.BAD_REQUEST, "RESERVATION-07", "취소된 예약은 수정할 수 없습니다."),
    RESERVATION_NOT_MODIFIABLE_STARTED(HttpStatus.BAD_REQUEST, "RESERVATION-08", "시작된 예약은 수정할 수 없습니다."),
    RESERVATION_NOT_CANCELLABLE_ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "RESERVATION-09", "이미 취소된 예약은 다시 취소할 수 없습니다."),
    RESERVATION_NOT_CANCELLABLE_STARTED(HttpStatus.BAD_REQUEST, "RESERVATION-10", "시작된 예약은 취소할 수 없습니다."),
    RESERVATION_NOT_APPLICANT(HttpStatus.FORBIDDEN, "RESERVATION-11", "해당 예약의 예약 신청자가 아닙니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

