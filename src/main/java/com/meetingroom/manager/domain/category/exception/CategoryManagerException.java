package com.meetingroom.manager.domain.category.exception;

import com.meetingroom.manager.global.error.code.CategoryErrorCode;
import com.meetingroom.manager.global.error.exception.BusinessManagerException;

public class CategoryManagerException extends BusinessManagerException {
    public CategoryManagerException(CategoryErrorCode errorCode) {
        super(errorCode);
    }
}
