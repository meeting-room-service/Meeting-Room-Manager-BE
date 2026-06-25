package com.meetingroom.manager.domain.room.repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.meetingroom.manager.domain.equipment.entity.QRoomEquipment;
import com.meetingroom.manager.domain.reservation.enums.ReservationStatus;
import com.meetingroom.manager.domain.room.dto.EquipmentSearchRequestDto;
import com.meetingroom.manager.domain.room.dto.RoomSearchRequestDto;
import com.meetingroom.manager.domain.inspection.entity.QInspection;
import com.meetingroom.manager.domain.reservation.entity.QReservation;
import com.meetingroom.manager.domain.room.dto.RoomQueryDto;
import com.meetingroom.manager.domain.room.entity.QRoom;
import com.meetingroom.manager.domain.room.enums.RoomSortBy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class RoomRepositoryImpl implements RoomRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    private final QRoom room = QRoom.room;

    private final QInspection inspection = QInspection.inspection;

    private final QReservation reservation = QReservation.reservation;

    private final QRoomEquipment roomEquipment = QRoomEquipment.roomEquipment;

    /*
     * 특정 시점 기준 회의실 단건 정보를 조회합니다.
     *
     * SELECT rm.id
     *      , rm.name
     *      , rm.max_capacity
     *      , rm.company_id
     *      , CASE
     *            WHEN EXISTS (
     *                 SELECT 1
     *                   FROM inspection i (점검)
     *                  WHERE i.room_id = rm.id
     *                    AND i.start_at <= :at
     *                    AND i.end_at >= :at
     *            ) THEN '점검 중'
     *            WHEN EXISTS (
     *                 SELECT 1
     *                   FROM reservation r (예약)
     *                  WHERE r.room_id = rm.id
     *                    AND r.status = 'CONFIRMED'
     *                    AND r.start_at <= :at
     *                    AND r.end_at >= :at
     *            ) THEN '사용 중'
     *            ELSE '사용 가능'
     *        END
     *   FROM room rm (회의실)
     *  WHERE rm.id = ?
     *    AND rm.is_deleted = FALSE
     */
    @Override
    public Optional<RoomQueryDto> getRoomById(Long roomId, LocalDateTime at) {
        RoomQueryDto roomQueryDto = queryFactory
                .select(Projections.constructor(
                        RoomQueryDto.class,
                        room.id,
                        room.name,
                        room.maxCapacity,
                        room.company.id,
                        resolveRoomStatusExpression(at)
                ))
                .from(room)
                .where(
                        room.id.eq(roomId),
                        isNotDeleted()
                )
                .fetchOne();

        return Optional.ofNullable(roomQueryDto);
    }

    /*
     * 필터 조건으로 회의실 목록을 페이징 조회합니다.
     *
     *       SELECT COUNT(rm.id)
     *          FROM room rm (회의실)
     *         WHERE rm.is_deleted = FALSE
     *           AND (:companyId IS NULL OR rm.company_id = :companyId)
     *           AND (:maxCapacity IS NULL OR rm.max_capacity >= :maxCapacity)
     *           AND EXISTS (
     *                    SELECT 1
     *                      FROM room_equipment re (회의실-비품)
     *                     WHERE re.room_id = rm.id
     *                       AND re.equipment_id = :equipmentId1
     *                       AND (:minQuantity1 IS NULL OR re.quantity >= :minQuantity1)
     *                       AND (:status1 IS NULL OR re.status = :status1)
     *                 )
     *    -- 비품 조건 개수만큼 AND EXISTS 반복; 비품 목록이 비어 있거나 적용할 조건이 없으면 비품 관련 AND 절 전체 생략
     *
     *        SELECT rm.id
     *             , rm.name
     *             , rm.max_capacity
     *             , rm.company_id
     *             , CASE
     *                   WHEN EXISTS (
     *                        SELECT 1
     *                          FROM inspection i (점검)
     *                         WHERE i.room_id = rm.id
     *                           AND i.start_at <= :at
     *                           AND i.end_at >= :at
     *                   ) THEN '점검 중'
     *                   WHEN EXISTS (
     *                        SELECT 1
     *                          FROM reservation r (예약)
     *                         WHERE r.room_id = rm.id
     *                           AND r.status = 'CONFIRMED'
     *                           AND r.start_at <= :at
     *                           AND r.end_at >= :at
     *                   ) THEN '사용 중'
     *                   ELSE '사용 가능'
     *               END
     *          FROM room rm (회의실)
     *         WHERE rm.is_deleted = FALSE
     *           AND (:companyId IS NULL OR rm.company_id = :companyId)
     *           AND (:maxCapacity IS NULL OR rm.max_capacity >= :maxCapacity)
     *           AND EXISTS (
     *                    SELECT 1
     *                      FROM room_equipment re (회의실-비품)
     *                     WHERE re.room_id = rm.id
     *                       AND re.equipment_id = :equipmentId1
     *                       AND (:minQuantity1 IS NULL OR re.quantity >= :minQuantity1)
     *                       AND (:status1 IS NULL OR re.status = :status1)
     *                 )
     *    -- 비품 조건 개수만큼 AND EXISTS 반복; 비품 목록이 비어 있거나 적용할 조건이 없으면 비품 관련 AND 절 전체 생략
     * -- 정렬 조건이 지원하는 경우
     *      ORDER BY rm.max_capacity ASC|DESC
     *             , rm.id ASC|DESC
     * -- 정렬 조건이 없거나 지원하지 않는 경우
     *      ORDER BY rm.id DESC
     *         LIMIT ? OFFSET ?
     */
    @Override
    public Page<RoomQueryDto> getRooms(
            Long companyId,
            RoomSearchRequestDto roomSearchRequestDto,
            LocalDateTime at,
            Pageable pageable
    ) {
        Long total = queryFactory
                .select(room.id.count())
                .from(room)
                .where(
                        isNotDeleted(),
                        eqCompanyId(companyId),
                        maxCapacityGoe(roomSearchRequestDto),
                        equipmentConditionsAll(roomSearchRequestDto)
                )
                .fetchOne();

        List<RoomQueryDto> content = queryFactory
                .select(Projections.constructor(
                        RoomQueryDto.class,
                        room.id,
                        room.name,
                        room.maxCapacity,
                        room.company.id,
                        resolveRoomStatusExpression(at)
                ))
                .from(room)
                .where(
                        isNotDeleted(),
                        eqCompanyId(companyId),
                        maxCapacityGoe(roomSearchRequestDto),
                        equipmentConditionsAll(roomSearchRequestDto)
                )
                .orderBy(resolveSortOrders(pageable).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total == null ? 0L : total);
    }

    /*
     * 특정 회사의 회의실 목록을 조회합니다.
     *
     * SELECT rm.id
     *      , rm.name
     *      , rm.max_capacity
     *      , rm.company_id
     *      , CASE
     *            WHEN EXISTS (
     *                 SELECT 1
     *                   FROM inspection i (점검)
     *                  WHERE i.room_id = rm.id
     *                    AND i.start_at <= :at
     *                    AND i.end_at >= :at
     *            ) THEN '점검 중'
     *            WHEN EXISTS (
     *                 SELECT 1
     *                   FROM reservation r (예약)
     *                  WHERE r.room_id = rm.id
     *                    AND r.status = 'CONFIRMED'
     *                    AND r.start_at <= :at
     *                    AND r.end_at >= :at
     *            ) THEN '사용 중'
     *            ELSE '사용 가능'
     *        END
     *   FROM room rm (회의실)
     *  WHERE (:companyId IS NULL OR rm.company_id = :companyId)
     *    AND rm.is_deleted = FALSE
     */
    @Override
    public List<RoomQueryDto> getAllRooms(Long companyId, LocalDateTime at) {
        return queryFactory
                .select(Projections.constructor(
                        RoomQueryDto.class,
                        room.id,
                        room.name,
                        room.maxCapacity,
                        room.company.id,
                        resolveRoomStatusExpression(at)
                ))
                .from(room)
                .where(
                        isNotDeleted(),
                        eqCompanyId(companyId)
                )
                .fetch();
    }


    /*
     * 회사 ID 필터 조건을 생성합니다.
     *
     * WHERE (:companyId IS NULL OR rm.company_id = :companyId)
     * -- companyId가 NULL이면 조건 생략
     */
    private BooleanExpression eqCompanyId(Long companyId) {
        if (companyId == null) {
            return null;
        }
        return room.company.id.eq(companyId);
    }

    /*
     * 소프트 삭제 제외 조건을 생성합니다.
     * WHERE rm.is_deleted = FALSE
     * -- (조건 생략 없음, 항상 적용)
     */
    private BooleanExpression isNotDeleted() {
        return room.deleted.isFalse();
    }

    /*
     * 최소 수용 인원 필터 조건을 생성합니다.
     * WHERE rm.max_capacity >= ?
     * -- maxCapacity가 NULL이면 조건 생략
     */
    private BooleanExpression maxCapacityGoe(RoomSearchRequestDto roomSearchRequestDto) {
        if (roomSearchRequestDto == null || roomSearchRequestDto.getMaxCapacity() == null) {
            return null;
        }
        return room.maxCapacity.goe(roomSearchRequestDto.getMaxCapacity());
    }

    /*
     * 비품 조건 목록을 AND EXISTS 조건으로 결합합니다.
     *

     * AND EXISTS (
     *     SELECT 1
     *       FROM room_equipment re (회의실-비품)
     *      WHERE re.room_id = rm.id
     *        AND re.equipment_id = ?
     *        AND ...
     * )
     * -- 비품 조건 개수만큼 AND EXISTS 반복; 비품 목록이 비어 있거나 적용할 조건이 없으면 비품 관련 AND 절 전체 생략
     */
    private BooleanExpression equipmentConditionsAll(RoomSearchRequestDto roomSearchRequestDto) {
        if (roomSearchRequestDto == null) {
            return null;
        }

        List<EquipmentSearchRequestDto> equipmentSearchRequestDtos = roomSearchRequestDto.getEquipmentSearchRequestDtos() == null
                ? List.of()
                : roomSearchRequestDto.getEquipmentSearchRequestDtos();
        if (equipmentSearchRequestDtos.isEmpty()) {
            return null;
        }

        BooleanExpression combined = null;
        for (EquipmentSearchRequestDto equipmentSearchRequestDto : equipmentSearchRequestDtos) {
            BooleanExpression exists = equipmentExists(equipmentSearchRequestDto);
            if (exists == null) {
                continue;
            }
            combined = combined == null ? exists : combined.and(exists);
        }
        return combined;
    }

    /*
     * 단일 비품 조건에 대한 EXISTS 서브쿼리를 생성합니다.
     *
     * SELECT 1
     *   FROM room_equipment re (회의실-비품)
     *  WHERE re.room_id = rm.id
     *    AND re.equipment_id = ?
     *
     * -- equipmentId가 NULL이면 조건 생략
     * -- minQuantity가 NULL이면 수량 하한 조건 생략
     * -- status가 NULL이면 비품 상태 조건 생략
     */
    private BooleanExpression equipmentExists(EquipmentSearchRequestDto equipmentSearchRequestDto) {
        if (equipmentSearchRequestDto == null || equipmentSearchRequestDto.getEquipmentId() == null) {
            return null;
        }

        BooleanExpression subQueryWhere = roomEquipment.room.id.eq(room.id)
                .and(roomEquipment.equipment.id.eq(equipmentSearchRequestDto.getEquipmentId()));

        if (equipmentSearchRequestDto.getMinQuantity() != null) {
            subQueryWhere = subQueryWhere.and(roomEquipment.quantity.goe(equipmentSearchRequestDto.getMinQuantity()));
        }
        if (equipmentSearchRequestDto.getStatus() != null) {
            subQueryWhere = subQueryWhere.and(roomEquipment.status.eq(equipmentSearchRequestDto.getStatus()));
        }

        return JPAExpressions.selectOne()
                .from(roomEquipment)
                .where(subQueryWhere)
                .exists();
    }

    /*
     *  정렬 요청을 ORDER BY 절로 변환합니다.
     *
     * -- 정렬 조건이 지원하는 경우
     *      ORDER BY rm.max_capacity ASC|DESC
     *             , rm.id ASC|DESC
     * -- 정렬 조건이 없거나 지원하지 않는 경우
     *      ORDER BY rm.id DESC
     */
    private List<OrderSpecifier<?>> resolveSortOrders(Pageable pageable) {
        Sort sort = pageable.getSort();
        if (!sort.isSorted()) {
            return List.of(room.id.desc());
        }

        for (Sort.Order order : sort) {
            RoomSortBy by = RoomSortBy.fromRequest(order.getProperty()).orElse(null);
            if (by == RoomSortBy.MAX_CAPACITY) {
                return order.isAscending()
                        ? List.of(room.maxCapacity.asc(), room.id.asc())
                        : List.of(room.maxCapacity.desc(), room.id.desc());
            }
        }

        return List.of(room.id.desc());
    }

    /*
     * 특정 시점 기준 회의실 상태 CASE 식을 생성합니다.
     *
     * CASE
     *     WHEN EXISTS (
     *          SELECT 1
     *            FROM inspection i (점검)
     *           WHERE i.room_id = rm.id
     *             AND i.start_at <= :at
     *             AND i.end_at >= :at
     *     ) THEN '점검 중'
     *     WHEN EXISTS (
     *          SELECT 1
     *            FROM reservation r (예약)
     *           WHERE r.room_id = rm.id
     *             AND r.status = 'CONFIRMED'
     *             AND r.start_at <= :at
     *             AND r.end_at >= :at
     *     ) THEN '사용 중'
     *     ELSE '사용 가능'
     * END
     */
    private Expression<String> resolveRoomStatusExpression(LocalDateTime at) {
        BooleanExpression underInspection = JPAExpressions.selectOne()
                .from(inspection)
                .where(
                        inspection.room.id.eq(room.id),
                        inspection.startAt.loe(at),
                        inspection.endAt.goe(at)
                )
                .exists();

        BooleanExpression inUse = JPAExpressions.selectOne()
                .from(reservation)
                .where(
                        reservation.room.id.eq(room.id),
                        reservation.status.eq(ReservationStatus.CONFIRMED),
                        reservation.startAt.loe(at),
                        reservation.endAt.goe(at)
                )
                .exists();

        return new CaseBuilder()
                .when(underInspection)
                .then("점검 중")
                .when(inUse)
                .then("사용 중")
                .otherwise("사용 가능");
    }

}
