package com.meetingroom.manager.domain.reservation.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.meetingroom.manager.domain.reservation.dto.ReservationApplicantQueryDto;
import com.meetingroom.manager.domain.reservation.dto.ReservationListSearchRequestDto;
import com.meetingroom.manager.domain.reservation.dto.ReservationQueryDto;
import com.meetingroom.manager.domain.reservation.dto.ReservationRoomQueryDto;
import com.meetingroom.manager.domain.company.entity.QCompanyMember;
import com.meetingroom.manager.domain.reservation.entity.QReservation;
import com.meetingroom.manager.domain.reservation.entity.QReservationParticipant;
import com.meetingroom.manager.domain.reservation.enums.ReservationSortBy;
import com.meetingroom.manager.domain.reservation.enums.TimePeriod;
import com.meetingroom.manager.domain.room.entity.QRoom;
import com.meetingroom.manager.domain.user.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QReservation reservation = QReservation.reservation;
    private final QRoom room = QRoom.room;
    private final QCompanyMember companyMember = QCompanyMember.companyMember;
    private final QUser user = QUser.user;
    private final QReservationParticipant reservationParticipant = QReservationParticipant.reservationParticipant;

    /*
     * 예약 목록과 전체 건수를 페이징 조회합니다.
     *
     *        SELECT COUNT(r.id)
     *          FROM reservation r (예약)
     *         WHERE (:companyId IS NULL OR r.company_id = :companyId)
     *           AND (:status IS NULL OR r.status = :status)
     *           AND (:fromDate IS NULL OR r.start_at >= :fromDate)
     *           AND (:toDate IS NULL OR r.end_at <= :toDate)
     *           AND (
     *                :timePeriod IS NULL
     *                OR (:timePeriod = 'MORNING' AND EXTRACT(HOUR FROM r.start_at) < 12)
     *                OR (:timePeriod = 'AFTERNOON' AND EXTRACT(HOUR FROM r.start_at) >= 12)
     *         )
     *           AND (
     *                   (:applicantOnly IS NOT TRUE AND :participatedOnly IS NOT TRUE)
     *                OR (:applicantOnly IS TRUE AND r.company_member_id = :currentCompanyMemberId)
     *                OR (:participatedOnly IS TRUE AND EXISTS (
     *                    SELECT 1
     *                      FROM reservation_participant rp (예약 참가자)
     *                     WHERE rp.reservation_id = r.id
     *                       AND rp.company_member_id = :currentCompanyMemberId
     *                 ))
     *         )
     *
     *        SELECT r.id
     *             , r.title
     *             , rm.id
     *             , CASE
     *                   WHEN rm.is_deleted = TRUE
     *                   THEN CONCAT(SUBSTRING_INDEX(rm.name, '_deleted_', 1), ' (삭제 됨)')
     *                   ELSE rm.name
     *               END AS room_name
     *             , r.start_at
     *             , r.end_at
     *             , r.status
     *             , am.id AS applicant_company_member_id
     *             , u.id AS applicant_user_id
     *             , CASE
     *                   WHEN am.status = 'RESIGNED' THEN CONCAT(u.nickname, ' (전 직원)')
     *                   ELSE u.nickname
     *               END AS applicant_nickname
     *          FROM reservation r (예약)
     *     LEFT JOIN room rm (회의실)
     *            ON r.room_id = rm.id
     *     LEFT JOIN company_member am (회사 멤버)
     *            ON r.company_member_id = am.id
     *     LEFT JOIN user u (사용자)
     *            ON am.user_id = u.id
     *         WHERE (:companyId IS NULL OR r.company_id = :companyId)
     *           AND (:status IS NULL OR r.status = :status)
     *           AND (:fromDate IS NULL OR r.start_at >= :fromDate)
     *           AND (:toDate IS NULL OR r.end_at <= :toDate)
     *           AND (
     *                :timePeriod IS NULL
     *                OR (:timePeriod = 'MORNING' AND EXTRACT(HOUR FROM r.start_at) < 12)
     *                OR (:timePeriod = 'AFTERNOON' AND EXTRACT(HOUR FROM r.start_at) >= 12)
     *           )
     *           AND (
     *                   (:applicantOnly IS NOT TRUE AND :participatedOnly IS NOT TRUE)
     *                OR (:applicantOnly IS TRUE AND r.company_member_id = :currentCompanyMemberId)
     *                OR (:participatedOnly IS TRUE AND EXISTS (
     *                    SELECT 1
     *                      FROM reservation_participant rp (예약 참가자)
     *                     WHERE rp.reservation_id = r.id
     *                       AND rp.company_member_id = :currentCompanyMemberId
     *                   )
     *                )
     *           )
     * -- 정렬 조건이 지원하는 경우
     *      ORDER BY r.created_at ASC|DESC
     *             , r.id ASC|DESC
     * -- 정렬 조건이 없거나 지원하지 않는 경우
     *      ORDER BY r.id DESC
     *         LIMIT ? OFFSET ?
     */
    @Override
    public Page<ReservationQueryDto> getReservations(
            Long currentCompanyMemberId,
            Long companyId,
            ReservationListSearchRequestDto reservationListSearchRequestDto,
            Pageable pageable
    ) {
        Long total = queryFactory
                .select(reservation.count())
                .from(reservation)
                .where(
                        eqCompanyId(companyId),
                        eqStatus(reservationListSearchRequestDto),
                        fromDateGoe(reservationListSearchRequestDto),
                        toDateLoe(reservationListSearchRequestDto),
                        timePeriodEq(reservationListSearchRequestDto),
                        companyMemberInvolvement(currentCompanyMemberId, reservationListSearchRequestDto)
                )
                .fetchOne();

        List<ReservationQueryDto> content = queryFactory
                .select(Projections.constructor(ReservationQueryDto.class,
                        reservation.id,
                        reservation.title,
                        Projections.constructor(ReservationRoomQueryDto.class,
                                room.id,
                                roomDisplayNameExpr()
                        ),
                        reservation.startAt,
                        reservation.endAt,
                        reservation.status,
                        Projections.constructor(ReservationApplicantQueryDto.class,
                                companyMember.id,
                                user.id,
                                applicantDisplayNameExpr())
                        )
                )
                .from(reservation)
                .leftJoin(reservation.room, room)
                .leftJoin(reservation.companyMember, companyMember)
                .leftJoin(companyMember.user, user)
                .where(
                        eqCompanyId(companyId),
                        eqStatus(reservationListSearchRequestDto),
                        fromDateGoe(reservationListSearchRequestDto),
                        toDateLoe(reservationListSearchRequestDto),
                        timePeriodEq(reservationListSearchRequestDto),
                        companyMemberInvolvement(currentCompanyMemberId, reservationListSearchRequestDto)
                )
                .orderBy(resolveSortOrders(pageable).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total == null ? 0L : total);
    }

    /*
     * 예약 ID 기준 상세 정보를 조회합니다.
     *
     *    SELECT r.id
     *         , r.title
     *         , rm.id
     *         , CASE
     *               WHEN rm.is_deleted = TRUE
     *               THEN CONCAT(SUBSTRING_INDEX(rm.name, '_deleted_', 1), ' (삭제 됨)')
     *               ELSE rm.name
     *           END AS room_name
     *         , r.start_at
     *         , r.end_at
     *         , r.status
     *         , am.id AS applicant_company_member_id
     *         , u.id AS applicant_user_id
     *         , CASE
     *               WHEN am.status = 'RESIGNED' THEN CONCAT(u.nickname, ' (전 직원)')
     *               ELSE u.nickname
     *           END AS applicant_nickname
     *      FROM reservation r (예약)
     * LEFT JOIN room rm (회의실)
     *        ON r.room_id = rm.id
     * LEFT JOIN company_member am (회사 멤버)
     *        ON r.company_member_id = am.id
     * LEFT JOIN user u (사용자)
     *        ON am.user_id = u.id
     *     WHERE r.id = ?
     */
    @Override
    public Optional<ReservationQueryDto> getReservationById(Long currentCompanyMemberId, Long reservationId) {
        ReservationQueryDto reservationQueryDto = queryFactory
                .select(Projections.constructor(ReservationQueryDto.class,
                                reservation.id,
                                reservation.title,
                                Projections.constructor(ReservationRoomQueryDto.class,
                                        room.id,
                                        roomDisplayNameExpr()
                                ),
                                reservation.startAt,
                                reservation.endAt,
                                reservation.status,
                                Projections.constructor(ReservationApplicantQueryDto.class,
                                        companyMember.id,
                                        user.id,
                                        applicantDisplayNameExpr())
                        )
                )
                .from(reservation)
                .leftJoin(reservation.room, room)
                .leftJoin(reservation.companyMember, companyMember)
                .leftJoin(companyMember.user, user)
                .where(reservation.id.eq(reservationId))
                .fetchOne();

        return Optional.ofNullable(reservationQueryDto);
    }

    /*
     * 회사 ID 필터 조건을 생성합니다.
     * WHERE (:companyId IS NULL OR r.company_id = :companyId)
     * -- companyId가 NULL이면 조건 생략
     */
    private BooleanExpression eqCompanyId(Long companyId) {
        if (companyId == null) {
            return null;
        }
        return reservation.company.id.eq(companyId);
    }

    /*
     * 예약 상태 필터 조건을 생성합니다.
     * WHERE r.status = ?
     * -- status가 NULL이면 조건 생략
     */
    private BooleanExpression eqStatus(ReservationListSearchRequestDto reservationListSearchRequestDto) {
        if (reservationListSearchRequestDto == null || reservationListSearchRequestDto.getStatus() == null) {
            return null;
        }
        return reservation.status.eq(reservationListSearchRequestDto.getStatus());
    }

    /*
     * 시작일 필터 조건을 생성합니다.
     *
     * WHERE r.start_at >= :fromDate
     * -- fromDate가 NULL이면 조건 생략
     */
    private BooleanExpression fromDateGoe(ReservationListSearchRequestDto reservationListSearchRequestDto) {
        if(reservationListSearchRequestDto == null || reservationListSearchRequestDto.getFromDate() == null) {
            return null;
        }
        LocalDate fromDate = reservationListSearchRequestDto.getFromDate();
        return reservation.startAt.goe(fromDate.atStartOfDay());
    }

    /*
     * 종료일 필터 조건을 생성합니다.
     *
     * WHERE r.end_at <= :toDate
     * -- toDate가 NULL이면 조건 생략
     */
    private BooleanExpression toDateLoe(ReservationListSearchRequestDto reservationListSearchRequestDto) {
        if (reservationListSearchRequestDto == null || reservationListSearchRequestDto.getToDate() == null) {
            return null;
        }
        LocalDate toDate = reservationListSearchRequestDto.getToDate();
        return reservation.endAt.loe(toDate.atTime(LocalTime.MAX));
    }

    /*
     * 시간대 필터 조건을 생성합니다.
     *
     * WHERE EXTRACT(HOUR FROM r.start_at) < 12   -- MORNING
     * WHERE EXTRACT(HOUR FROM r.start_at) >= 12  -- AFTERNOON
     * -- timePeriod가 NULL이면 조건 생략
     */
    private BooleanExpression timePeriodEq(ReservationListSearchRequestDto reservationListSearchRequestDto) {
        if (reservationListSearchRequestDto == null || reservationListSearchRequestDto.getTimePeriod() == null) {
            return null;
        }

        TimePeriod dayPart = reservationListSearchRequestDto.getTimePeriod();
        var hourExpr = Expressions.numberTemplate(Integer.class, "extract(hour from {0})", reservation.startAt);
        return switch (dayPart) {
            case MORNING -> hourExpr.lt(12);
            case AFTERNOON -> hourExpr.goe(12);
        };
    }

    /*
     * 신청자/참여자 조건을 조합한 필터를 생성합니다.
     * WHERE r.company_member_id = :currentCompanyMemberId  -- 신청자만
     *   OR EXISTS (
     *      SELECT 1
     *        FROM reservation_participant rp (예약 참가자)
     *       WHERE rp.reservation_id = r.id
     *         AND rp.company_member_id = :currentCompanyMemberId
     *     )  -- 참여만
     * -- applicantOnly, participatedOnly가 모두 FALSE이면 조건 생략
     */
    private BooleanExpression companyMemberInvolvement(Long currentCompanyMemberId, ReservationListSearchRequestDto reservationListSearchRequestDto) {
        if(reservationListSearchRequestDto == null) {
            return null;
        }

        boolean applicantOnly = Boolean.TRUE.equals(reservationListSearchRequestDto.getApplicantOnly());
        boolean participated = Boolean.TRUE.equals(reservationListSearchRequestDto.getParticipatedOnly());
        if (!applicantOnly && !participated) {
            return null;
        }
        BooleanExpression iAmApplicant = reservation.companyMember.id.eq(currentCompanyMemberId);
        BooleanExpression iAmParticipant = JPAExpressions.selectOne()
                .from(reservationParticipant)
                .where(
                        reservationParticipant.reservation.eq(reservation),
                        reservationParticipant.companyMember.id.eq(currentCompanyMemberId)
                )
                .exists();
        if (applicantOnly && participated) {
            return iAmApplicant.or(iAmParticipant);
        }
        if (applicantOnly) {
            return iAmApplicant;
        }
        return iAmParticipant;
    }

    /*
     * 회의실 표시명을 생성합니다.
     *
     * CASE
     *     WHEN rm.is_deleted = TRUE
     *     THEN CONCAT(SUBSTRING_INDEX(rm.name, '_deleted_', 1), ' (삭제 됨)')
     *     ELSE rm.name
     * END
     */
    private StringExpression roomDisplayNameExpr() {
        return Expressions.stringTemplate(
                "case when {0} = true then concat(function('substring_index', {1}, '_deleted_', 1), ' (삭제 됨)') else {1} end",
                room.deleted,
                room.name
        );
    }

    /*
     * 신청자 표시명을 생성합니다.
     *
     * CASE
     *     WHEN am.status = 'RESIGNED' THEN CONCAT(u.nickname, ' (전 직원)')
     *     ELSE u.nickname
     * END
     */
    private StringExpression applicantDisplayNameExpr() {
        return Expressions.stringTemplate(
                "case when {0} = 'RESIGNED' then concat({1}, ' (전 직원)') else {1} end",
                companyMember.status.stringValue(),
                user.nickname
        );
    }

    /*
     * 정렬 요청을 ORDER BY 절로 변환합니다.
     *
     * -- 정렬 조건이 지원하는 경우
     *      ORDER BY r.created_at ASC|DESC
     *             , r.id ASC|DESC
     * -- 정렬 조건이 없거나 지원하지 않는 경우
     *      ORDER BY r.id DESC
     */
    private List<OrderSpecifier<?>> resolveSortOrders(Pageable pageable) {
        Sort sort = pageable.getSort();
        if (!sort.isSorted()) {
            return List.of(reservation.id.desc());
        }

        for (Sort.Order order : sort) {
            ReservationSortBy by = ReservationSortBy.fromRequest(order.getProperty()).orElse(null);
            if (by == ReservationSortBy.CREATED_AT) {
                return order.isAscending()
                        ? List.of(reservation.createdAt.asc(), reservation.id.asc())
                        : List.of(reservation.createdAt.desc(), reservation.id.desc());
            }
        }

        return List.of(reservation.id.desc());
    }
}
