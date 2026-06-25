package com.meetingroom.manager.domain.inquiry.exception;

import com.meetingroom.manager.global.error.code.InquiryErrorCode;
import com.meetingroom.manager.global.error.exception.BusinessManagerException;

public class InquiryManagerException extends BusinessManagerException {
    public InquiryManagerException(InquiryErrorCode errorCode) {
        super(errorCode);
    }
}

