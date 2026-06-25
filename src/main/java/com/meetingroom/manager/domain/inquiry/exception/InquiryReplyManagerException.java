package com.meetingroom.manager.domain.inquiry.exception;

import com.meetingroom.manager.domain.inquiry.enums.InquiryReplyErrorCode;
import com.meetingroom.manager.global.error.exception.BusinessManagerException;

public class InquiryReplyManagerException extends BusinessManagerException {
    public InquiryReplyManagerException(InquiryReplyErrorCode errorCode) {
        super(errorCode);
    }
}
