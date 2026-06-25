package com.meetingroom.manager.domain.reservation.repository;

import com.meetingroom.manager.domain.reservation.dto.ReservationListSearchRequestDto;
import com.meetingroom.manager.domain.reservation.dto.ReservationQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ReservationRepositoryCustom {
    Optional<ReservationQueryDto> getReservationById(Long currentCompanyMemberId, Long reservationId);
    Page<ReservationQueryDto> getReservations(
            Long currentCompanyMemberId,
            Long companyId,
            ReservationListSearchRequestDto reservationListSearchRequestDto,
            Pageable pageable
    );
}
