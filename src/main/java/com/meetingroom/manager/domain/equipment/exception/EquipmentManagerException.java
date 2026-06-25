package com.meetingroom.manager.domain.equipment.exception;

import com.meetingroom.manager.global.error.code.EquipmentErrorCode;
import com.meetingroom.manager.global.error.exception.BusinessManagerException;

public class EquipmentManagerException extends BusinessManagerException {
    public EquipmentManagerException(EquipmentErrorCode errorCode) {
        super(errorCode);
    }
}

