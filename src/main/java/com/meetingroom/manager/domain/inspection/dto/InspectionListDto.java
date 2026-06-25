package com.meetingroom.manager.domain.inspection.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class InspectionListDto {
    private Long id;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endAt;
    private LocalDateTime createdAt;
    private InspectionRoomDto room;

    @Builder
    private InspectionListDto(Long id, String name, LocalDateTime startAt, LocalDateTime endAt, LocalDateTime createdAt, InspectionRoomDto room) {
        this.id = id;
        this.name = name;
        this.startAt = startAt;
        this.endAt = endAt;
        this.createdAt = createdAt;
        this.room = room;
    }
}
