package com.meetingroom.manager.domain.reservation.repository;

import com.meetingroom.manager.domain.reservation.dto.ReservationParticipantQueryDto;

import java.util.Collection;
import java.util.List;
public interface ReservationParticipantRepositoryCustom {
    List<ReservationParticipantQueryDto> getReservationParticipantsByReservationIds(
            Collection<Long> reservationIds
    );
}
