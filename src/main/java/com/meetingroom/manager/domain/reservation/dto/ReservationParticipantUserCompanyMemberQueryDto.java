package com.meetingroom.manager.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationParticipantUserCompanyMemberQueryDto {
    private Long id;
    private ReservationParticipantUserCompanyMemberUserQueryDto user;
}
