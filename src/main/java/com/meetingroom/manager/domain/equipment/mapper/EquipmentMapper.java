package com.meetingroom.manager.domain.equipment.mapper;

import com.meetingroom.manager.domain.equipment.dto.EquipmentDto;
import com.meetingroom.manager.domain.equipment.dto.EquipmentListDto;
import com.meetingroom.manager.domain.equipment.dto.EquipmentQueryDto;
import com.meetingroom.manager.domain.equipment.entity.Equipment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {
    default EquipmentDto toDto(Equipment equipment) {
        if (equipment == null) {
            return null;
        }
        return EquipmentDto.builder()
                .id(equipment.getId())
                .name(equipment.getName())
                .createdAt(equipment.getCreatedAt())
                .build();
    }

    default EquipmentDto toDto(EquipmentQueryDto equipmentQueryDto) {
        if (equipmentQueryDto == null) {
            return null;
        }
        return EquipmentDto.builder()
                .id(equipmentQueryDto.getId())
                .name(equipmentQueryDto.getName())
                .createdAt(equipmentQueryDto.getCreatedAt())
                .build();
    }

    default EquipmentListDto toListDto(EquipmentQueryDto equipmentQueryDto) {
        if (equipmentQueryDto == null) {
            return null;
        }
        return EquipmentListDto.builder()
                .id(equipmentQueryDto.getId())
                .name(equipmentQueryDto.getName())
                .createdAt(equipmentQueryDto.getCreatedAt())
                .build();
    }
}

