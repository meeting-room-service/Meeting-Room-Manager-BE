package com.meetingroom.manager.domain.room.dto;

import com.meetingroom.manager.domain.equipment.enums.RoomEquipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomEquipmentQueryDto {
    private Long roomId;
    private Long equipmentId;
    private String name;
    private int quantity;
    private RoomEquipmentStatus status;
}
