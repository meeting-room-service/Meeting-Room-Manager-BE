package com.meetingroom.manager.domain.room.dto;

import com.meetingroom.manager.domain.equipment.enums.RoomEquipmentStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RoomEquipmentDto {
    private Long roomId;
    private Long equipmentId;
    private String name;
    private int quantity;
    private RoomEquipmentStatus status;

    @Builder
    private RoomEquipmentDto(Long roomId, Long equipmentId, String name, int quantity, RoomEquipmentStatus status) {
        this.roomId = roomId;
        this.equipmentId = equipmentId;
        this.name = name;
        this.quantity = quantity;
        this.status = status;
    }
}
