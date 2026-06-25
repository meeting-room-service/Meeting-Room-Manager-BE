package com.meetingroom.manager.domain.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.meetingroom.manager.domain.reservation.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DashboardReservationQueryDto {
    private Long id;
    private Long roomId;
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endAt;
    private ReservationStatus status;
}
