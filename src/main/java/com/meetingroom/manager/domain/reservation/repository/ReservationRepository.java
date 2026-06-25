package com.meetingroom.manager.domain.reservation.repository;

import com.meetingroom.manager.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepositoryCustom {
    /*
     * 신규 예약 생성 시 회의실 시간 중복 여부를 확인합니다.
     *
     * SELECT COUNT(r) > 0
     *   FROM reservation r (예약)
     *  WHERE r.room_id = :roomId
     *    AND r.status = 'CONFIRMED'
     *    AND r.start_at < :endAt
     *    AND r.end_at > :startAt
     */
    @Query("""
           SELECT COUNT(r) > 0
             FROM Reservation r
            WHERE r.room.id = :roomId
              AND r.status = com.meetingroom.manager.domain.reservation.enums.ReservationStatus.CONFIRMED
              AND r.startAt < :endAt
              AND r.endAt > :startAt
           """)
    boolean existsConfirmedReservationOverlappingOnRoom(
            @Param("roomId") Long roomId,
            @Param("endAt") LocalDateTime endAt,
            @Param("startAt") LocalDateTime startAt
    );

    /*
     * 예약 수정 시 자기 자신을 제외한 회의실 시간 중복 여부를 확인합니다.
     *
     * SELECT COUNT(r) > 0
     *   FROM reservation r (예약)
     *  WHERE r.room_id = :roomId
     *    AND r.status = 'CONFIRMED'
     *    AND r.start_at < :endAt
     *    AND r.end_at > :startAt
     *    AND r.id <> :excludeReservationId
     */
    @Query("""
           SELECT COUNT(r) > 0
             FROM Reservation r
            WHERE r.room.id = :roomId
              AND r.status = com.meetingroom.manager.domain.reservation.enums.ReservationStatus.CONFIRMED
              AND r.startAt < :endAt
              AND r.endAt > :startAt
              AND r.id <> :excludeReservationId
           """)
    boolean existsConfirmedReservationOverlappingOnRoomExcluding(
            @Param("roomId") Long roomId,
            @Param("endAt") LocalDateTime endAt,
            @Param("startAt") LocalDateTime startAt,
            @Param("excludeReservationId") Long excludeReservationId
    );

    /*
     * 예약 시간이 겹치는 회사 멤버 ID 목록을 조회합니다.
     *
     * SELECT DISTINCT cm.id
     *   FROM reservation_participant rp (예약 참가자)
     *   JOIN reservation r (예약)
     *     ON rp.reservation_id = r.id
     *   JOIN company_member cm (회사 멤버)
     *     ON rp.company_member_id = cm.id
     *  WHERE r.status = 'CONFIRMED'
     *    AND r.start_at < ?
     *    AND r.end_at > ?
     *    AND cm.id IN (...)
     *    AND (? IS NULL OR r.id <> ?)
     */
    @Query("""
           SELECT DISTINCT cm.id
             FROM ReservationParticipant p
             JOIN p.reservation r
             JOIN p.companyMember cm
            WHERE r.status = com.meetingroom.manager.domain.reservation.enums.ReservationStatus.CONFIRMED
              AND r.startAt < :endAt
              AND r.endAt > :startAt
              AND cm.id IN :companyMemberIds
              AND (:excludeReservationId IS NULL OR r.id <> :excludeReservationId)
           """)
    Set<Long> findCompanyMemberIdsWithOverlappingReservationAsParticipant(
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt,
            @Param("companyMemberIds") List<Long> companyMemberIds,
            @Param("excludeReservationId") Long excludeReservationId
    );
}
