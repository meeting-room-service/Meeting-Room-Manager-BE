package com.meetingroom.manager.domain.inspection.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.meetingroom.manager.domain.inspection.dto.InspectionListSearchRequestDto;
import com.meetingroom.manager.domain.inspection.dto.InspectionQueryDto;
import com.meetingroom.manager.domain.inspection.dto.InspectionRoomQueryDto;
import com.meetingroom.manager.domain.inspection.entity.QInspection;
import com.meetingroom.manager.domain.room.entity.QRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class InspectionRepositoryImpl implements InspectionRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QInspection inspection = QInspection.inspection;
    private final QRoom room = QRoom.room;

    /*
     * 점검 ID 기준 단건 정보를 조회합니다.
     *
     *    SELECT i.id
     *         , i.name
     *         , i.start_at
     *         , i.end_at
     *         , i.created_at
     *         , rm.id
     *         , rm.name
     *      FROM inspection i (점검)
     * LEFT JOIN room rm (회의실)
     *        ON i.room_id = rm.id
     *     WHERE i.id = ?
     */
    @Override
    public Optional<InspectionQueryDto> getInspectionById(Long inspectionId) {
        InspectionQueryDto inspectionQueryDto = queryFactory
                .select(Projections.constructor(
                        InspectionQueryDto.class,
                        inspection.id,
                        inspection.name,
                        inspection.startAt,
                        inspection.endAt,
                        inspection.createdAt,
                        Projections.constructor(
                                InspectionRoomQueryDto.class,
                                room.id,
                                room.name
                        )
                ))
                .from(inspection)
                .leftJoin(inspection.room, room)
                .where(
                        inspection.id.eq(inspectionId)
                )
                .fetchOne();

        return Optional.ofNullable(inspectionQueryDto);
    }

    /*
     * 점검 목록과 전체 건수를 페이징 조회합니다.
     *
     *        SELECT COUNT(i.id)
     *          FROM inspection i (점검)
     *         WHERE (:companyId IS NULL OR i.company_id = :companyId)
     *           AND (:roomId IS NULL OR i.room.id = :roomId)
     *           AND (:name IS NULL OR i.name LIKE CONCAT('%', :name, '%'))
     *           AND (:fromDate IS NULL OR i.start_at >= :fromDate)
     *           AND (:toDate IS NULL OR i.end_at <= :toDate)
     *
     *        SELECT i.id
     *             , i.name
     *             , i.start_at
     *             , i.end_at
     *             , i.created_at
     *             , rm.id
     *             , rm.name
     *          FROM inspection i (점검)
     *     LEFT JOIN room rm (회의실)
     *            ON i.room_id = rm.id
     *         WHERE (:companyId IS NULL OR i.company_id = :companyId)
     *           AND (:roomId IS NULL OR i.room_id = :roomId)
     *           AND (:name IS NULL OR i.name LIKE CONCAT('%', :name, '%'))
     *           AND (:fromDate IS NULL OR i.start_at >= :fromDate)
     *           AND (:toDate IS NULL OR i.end_at <= :toDate)
     * -- 정렬 조건이 지원하는 경우
     *      ORDER BY i.created_at ASC|DESC
     *             , i.id ASC|DESC
     * -- 정렬 조건이 없거나 지원하지 않는 경우
     *      ORDER BY i.id DESC
     *         LIMIT ? OFFSET ?
     */
    @Override
    public Page<InspectionQueryDto> getInspections(Long companyId, Pageable pageable, InspectionListSearchRequestDto inspectionListSearchRequestDto) {
        Long total = queryFactory
                .select(inspection.count())
                .from(inspection)
                .where(
                        companyIdEq(companyId),
                        roomIdEq(inspectionListSearchRequestDto),
                        nameContains(inspectionListSearchRequestDto),
                        fromDateGoe(inspectionListSearchRequestDto),
                        toDateLoe(inspectionListSearchRequestDto)
                )
                .fetchOne();

        List<InspectionQueryDto> content = queryFactory
                .select(Projections.constructor(
                        InspectionQueryDto.class,
                        inspection.id,
                        inspection.name,
                        inspection.startAt,
                        inspection.endAt,
                        inspection.createdAt,
                        Projections.constructor(
                                InspectionRoomQueryDto.class,
                                room.id,
                                room.name
                        )
                ))
                .from(inspection)
                .leftJoin(inspection.room, room)
                .where(companyIdEq(companyId),
                        roomIdEq(inspectionListSearchRequestDto),
                        nameContains(inspectionListSearchRequestDto),
                        fromDateGoe(inspectionListSearchRequestDto),
                        toDateLoe(inspectionListSearchRequestDto)
                )
                .orderBy(resolveSortOrders(pageable).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total == null ? 0L : total);

    }

    /*
     * 회사 ID 일치 조건을 생성합니다.
     * WHERE (:companyId IS NULL OR i.company_id = :companyId)
     * -- companyId가 NULL이면 조건 생략
     */
    private BooleanExpression companyIdEq(Long companyId) {
        if (companyId == null) {
            return null;
        }
        return inspection.company.id.eq(companyId);
    }

    /*
     * 회의실 ID 필터 조건을 생성합니다.
     * WHERE rm.id = ?
     * -- roomId가 NULL이면 조건 생략
     */
    private BooleanExpression roomIdEq(InspectionListSearchRequestDto inspectionListSearchRequestDto) {
        if (inspectionListSearchRequestDto == null || inspectionListSearchRequestDto.getRoomId() == null) {
            return null;
        }
        return inspection.room.id.eq(inspectionListSearchRequestDto.getRoomId());
    }

    /*
     * 정렬 요청을 ORDER BY 절로 변환합니다.
     *
     * -- 정렬 조건이 지원하는 경우
     *      ORDER BY i.created_at ASC|DESC
     *             , i.id ASC|DESC
     * -- 정렬 조건이 없거나 지원하지 않는 경우
     *      ORDER BY i.id DESC
     */
    private List<OrderSpecifier<?>> resolveSortOrders(Pageable pageable) {
        Sort sort = pageable.getSort();
        if (!sort.isSorted()) {
            return List.of(inspection.id.desc());
        }
        for (Sort.Order order : sort) {
            String property = order.getProperty();
            if ("createdAt".equals(property)) {
                return order.isAscending()
                        ? List.of(inspection.createdAt.asc(), inspection.id.asc())
                        : List.of(inspection.createdAt.desc(), inspection.id.desc());
            }
        }
        return List.of(inspection.id.desc());
    }

    /*
     * 점검명 검색 필터 조건을 생성합니다.
     *
     * WHERE i.name LIKE CONCAT('%', ?, '%')
     * -- name이 NULL 또는 공백이면 조건 생략
     */
    private BooleanExpression nameContains(InspectionListSearchRequestDto inspectionListSearchRequestDto) {
        if (inspectionListSearchRequestDto == null) {
            return null;
        }
        String title = inspectionListSearchRequestDto.getName();
        if (!StringUtils.hasText(title)) {
            return null;
        }
        return inspection.name.contains(title);
    }

    /*
     * 시작일 하한 필터 조건을 생성합니다.
     *
     * WHERE i.start_at >= :fromDate
     * -- fromDate가 NULL이면 조건 생략
     */
    private BooleanExpression fromDateGoe(InspectionListSearchRequestDto inspectionListSearchRequestDto) {
        if (inspectionListSearchRequestDto == null || inspectionListSearchRequestDto.getFromDate() == null) {
            return null;
        }
        LocalDate fromDate = inspectionListSearchRequestDto.getFromDate();
        return inspection.startAt.goe(fromDate.atStartOfDay());
    }

    /*
     * 종료일 상한 필터 조건을 생성합니다.
     *
     * WHERE i.end_at <= :toDate
     * -- toDate가 NULL이면 조건 생략
     */
    private BooleanExpression toDateLoe(InspectionListSearchRequestDto inspectionListSearchRequestDto) {
        if (inspectionListSearchRequestDto == null || inspectionListSearchRequestDto.getToDate() == null) {
            return null;
        }
        LocalDate toDate = inspectionListSearchRequestDto.getToDate();
        return inspection.endAt.loe(toDate.atTime(LocalTime.MAX));
    }
}
