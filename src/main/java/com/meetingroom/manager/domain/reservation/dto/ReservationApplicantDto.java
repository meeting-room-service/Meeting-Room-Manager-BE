package com.meetingroom.manager.domain.reservation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReservationApplicantDto {
    private Long id;
    private ReservationApplicantUserDto user;

    @Builder
    private ReservationApplicantDto(Long id, ReservationApplicantUserDto user) {
        this.id = id;
        this.user = user;
    }
}
