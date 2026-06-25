package com.meetingroom.manager.domain.inspection.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class InspectionRoomDto {
    private Long id;
    private String name;

    @Builder
    private InspectionRoomDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
