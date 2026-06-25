package com.meetingroom.manager.domain.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.meetingroom.manager.domain.reservation.enums.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DashboardReservationDto {
    private Long id;
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endAt;
    private ReservationStatus status;

    @Builder
    private DashboardReservationDto(
            Long id,
            String title,
            LocalDateTime startAt,
            LocalDateTime endAt,
            ReservationStatus status
    ) {
        this.id = id;
        this.title = title;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = status;
    }
}
