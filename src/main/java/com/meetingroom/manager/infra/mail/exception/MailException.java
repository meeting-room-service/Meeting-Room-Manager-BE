package com.meetingroom.manager.infra.mail.exception;

import com.meetingroom.manager.global.error.code.MailErrorCode;
import com.meetingroom.manager.global.error.exception.ExternalServiceManagerException;
import lombok.Getter;

@Getter
public class MailException extends ExternalServiceManagerException {
    public MailException(MailErrorCode errorCode) {
        super(errorCode);
    }
}
