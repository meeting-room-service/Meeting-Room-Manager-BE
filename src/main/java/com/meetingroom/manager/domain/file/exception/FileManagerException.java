package com.meetingroom.manager.domain.file.exception;

import com.meetingroom.manager.global.error.code.FileErrorCode;
import com.meetingroom.manager.global.error.exception.BusinessManagerException;

public class FileManagerException extends BusinessManagerException {
    public FileManagerException(FileErrorCode errorCode) {
        super(errorCode);
    }
}
