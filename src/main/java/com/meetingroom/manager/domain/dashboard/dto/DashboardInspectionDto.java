package com.meetingroom.manager.domain.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DashboardInspectionDto {
    private Long id;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endAt;

    @Builder
    private DashboardInspectionDto(
            Long id,
            String name,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        this.id = id;
        this.name = name;
        this.startAt = startAt;
        this.endAt = endAt;
    }
}
