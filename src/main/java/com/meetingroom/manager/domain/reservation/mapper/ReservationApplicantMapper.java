package com.meetingroom.manager.domain.reservation.mapper;

import com.meetingroom.manager.domain.reservation.dto.ReservationApplicantDto;
import com.meetingroom.manager.domain.reservation.dto.ReservationApplicantQueryDto;
import com.meetingroom.manager.domain.reservation.dto.ReservationApplicantUserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservationApplicantMapper {

    default ReservationApplicantDto toReservationApplicantDto(ReservationApplicantQueryDto reservationApplicantQueryDto) {
        if (reservationApplicantQueryDto == null) {
            return null;
        }
        return ReservationApplicantDto.builder()
                .id(reservationApplicantQueryDto.getCompanyMemberId())
                .user(ReservationApplicantUserDto.builder()
                        .id(reservationApplicantQueryDto.getUserId())
                        .nickname(reservationApplicantQueryDto.getNickname())
                        .build())
                .build();
    }
}
