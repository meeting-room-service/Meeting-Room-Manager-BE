package com.meetingroom.manager.domain.equipment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EquipmentQueryDto {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
}
