package com.meetingroom.manager.domain.inspection.exception;

import com.meetingroom.manager.global.error.code.InspectionErrorCode;
import com.meetingroom.manager.global.error.exception.BusinessManagerException;

public class InspectionManagerException extends BusinessManagerException {
    public InspectionManagerException(InspectionErrorCode errorCode) {
        super(errorCode);
    }
}
