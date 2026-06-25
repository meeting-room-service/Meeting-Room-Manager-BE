package com.meetingroom.manager.domain.reservation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReservationParticipantCompanyMemberDto {
    private Long id;
    private ReservationParticipantCompanyMemberUserDto user;

    @Builder
    private ReservationParticipantCompanyMemberDto(Long id, ReservationParticipantCompanyMemberUserDto user) {
        this.id = id;
        this.user = user;
    }
}
