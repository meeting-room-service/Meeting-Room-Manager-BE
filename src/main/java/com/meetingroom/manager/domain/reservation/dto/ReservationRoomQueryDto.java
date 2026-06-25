package com.meetingroom.manager.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationRoomQueryDto {
    private Long id;
    private String name;
}
