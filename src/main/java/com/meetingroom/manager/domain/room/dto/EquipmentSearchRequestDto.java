package com.meetingroom.manager.domain.room.dto;

import jakarta.validation.constraints.Min;
import com.meetingroom.manager.domain.equipment.enums.RoomEquipmentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EquipmentSearchRequestDto {

    private Long equipmentId;

    @Min(value = 1, message = "최소 수량은 1 이상이어야 합니다.")
    private Integer minQuantity;

    private RoomEquipmentStatus status;

    @Builder
    private EquipmentSearchRequestDto(Long equipmentId, Integer minQuantity, RoomEquipmentStatus status) {
        this.equipmentId = equipmentId;
        this.minQuantity = minQuantity;
        this.status = status;
    }
}
