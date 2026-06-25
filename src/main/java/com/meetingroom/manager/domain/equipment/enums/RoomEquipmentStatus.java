package com.meetingroom.manager.domain.equipment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomEquipmentStatus {
    AVAILABLE, // 정상
    BROKEN, // 고장
    LOST, // 분실
    IN_REPAIR; // 수리 중
}
