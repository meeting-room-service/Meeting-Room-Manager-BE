package com.meetingroom.manager.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationParticipantQueryDto {
    private Long reservationId;
    private Long id;
    private ReservationParticipantUserCompanyMemberQueryDto companyMember;
}
