package com.meetingroom.manager.domain.reservation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReservationApplicantUserDto {
    private Long id;
    private String nickname;

    @Builder
    private ReservationApplicantUserDto(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }
}
