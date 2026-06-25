package com.meetingroom.manager.domain.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.meetingroom.manager.domain.reservation.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReservationQueryDto {
    private Long id;
    private String title;
    private ReservationRoomQueryDto room;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endAt;
    private ReservationStatus status;
    private ReservationApplicantQueryDto applicant;
}
