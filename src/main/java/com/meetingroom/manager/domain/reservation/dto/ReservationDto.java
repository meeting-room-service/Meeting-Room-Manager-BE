package com.meetingroom.manager.domain.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.meetingroom.manager.domain.reservation.enums.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ReservationDto {
    private Long id;
    private String title;
    private ReservationRoomDto room;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endAt;
    private ReservationStatus status;
    private List<ReservationParticipantDto> participants;

    @Builder
    private ReservationDto(Long id, String title, ReservationRoomDto room, LocalDateTime startAt, LocalDateTime endAt, ReservationStatus status, List<ReservationParticipantDto> participants) {
        this.id = id;
        this.title = title;
        this.room = room;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = status;
        this.participants = participants;
    }
}
