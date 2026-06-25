package com.meetingroom.manager.domain.reservation.exception;

import com.meetingroom.manager.global.error.code.ReservationErrorCode;
import com.meetingroom.manager.global.error.exception.BusinessManagerException;

public class ReservationManagerException extends BusinessManagerException {
    public ReservationManagerException(ReservationErrorCode errorCode) {
        super(errorCode);
    }
}

