package com.meetingroom.manager.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationApplicantQueryDto {
    private Long companyMemberId;
    private Long userId;
    private String nickname;
}
