package com.meetingroom.manager.domain.equipment.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.meetingroom.manager.domain.equipment.dto.EquipmentQueryDto;
import com.meetingroom.manager.domain.equipment.entity.QEquipment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class EquipmentRepositoryImpl implements EquipmentRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QEquipment equipment = QEquipment.equipment;

    /*
     * 회사별 비품 단건 정보를 조회합니다.
     *
     * SELECT e.id
     *      , e.name
     *      , e.created_at
     *   FROM equipment e (비품)
     *  WHERE e.id = ?
     *  LIMIT 1
     */

    @Override
    public Optional<EquipmentQueryDto> getByEquipmentId(Long equipmentId) {
        EquipmentQueryDto equipmentQueryDto = queryFactory
                .select(Projections.constructor(EquipmentQueryDto.class,
                        equipment.id,
                        equipment.name,
                        equipment.createdAt
                ))
                .from(equipment)
                .where(
                        equipment.id.eq(equipmentId)
                )
                .fetchOne();

        return Optional.ofNullable(equipmentQueryDto);
    }

    /*
     * 회사별 비품 목록과 전체 건수를 페이징 조회합니다.
     *
     *       SELECT COUNT(e.id)
     *         FROM equipment e (비품)
     *        WHERE (:companyId IS NULL OR e.company_id = :companyId)
     *          AND (:name IS NULL OR e.name LIKE CONCAT('%', :name, '%'))
     *
     *       SELECT e.id
     *            , e.name
     *            , e.created_at
     *         FROM equipment e (비품)
     *        WHERE (:companyId IS NULL OR e.company_id = :companyId)
     *          AND (:name IS NULL OR e.name LIKE CONCAT('%', :name, '%'))
     * -- 정렬 조건이 지원하는 경우
     *      ORDER BY e.created_at ASC|DESC
     *             , e.id ASC|DESC
     * -- 정렬 조건이 없거나 지원하지 않는 경우
     *      ORDER BY e.id DESC
     *         LIMIT ? OFFSET ?
     *
     */
    @Override
    public Page<EquipmentQueryDto> getEquipments(Long companyId, String name, Pageable pageable) {
        Long total = queryFactory
                .select(equipment.count())
                .from(equipment)
                .where(
                        companyIdEq(companyId),
                        nameEq(name)
                )
                .fetchOne();

        List<EquipmentQueryDto> content = queryFactory
                .select(Projections.constructor(EquipmentQueryDto.class,
                        equipment.id,
                        equipment.name,
                        equipment.createdAt
                ))
                .from(equipment)
                .where(
                        companyIdEq(companyId),
                        nameEq(name)
                )
                .orderBy(resolveSortOrders(pageable).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total == null ? 0L : total);
    }

    /*
     * 회사별 비품 전체 목록을 조회합니다.
     *
     * SELECT e.id
     *      , e.name
     *      , e.created_at
     *   FROM equipment e (비품)
     *  WHERE (:companyId IS NULL OR e.company_id = :companyId)
     */
    @Override
    public List<EquipmentQueryDto> getAllEquipments(Long companyId) {
        return queryFactory
                .select(Projections.constructor(EquipmentQueryDto.class,
                        equipment.id,
                        equipment.name,
                        equipment.createdAt
                ))
                .from(equipment)
                .where(
                        companyIdEq(companyId)
                )
                .fetch();
    }

    /*
     * 회사 ID 필터 조건을 생성합니다.
     *
     * WHERE (:companyId IS NULL OR e.company_id = :companyId)
     * -- companyId가 NULL이면 조건 생략
     */
    private BooleanExpression companyIdEq(Long companyId) {
        if (companyId == null) {
            return null;
        }
        return equipment.company.id.eq(companyId);
    }

    /*
     * 비품 이름 검색 필터 조건을 생성합니다.
     * WHERE e.name LIKE CONCAT('%', ?, '%')
     * -- name이 NULL 또는 공백이면 조건 생략
     */
    private BooleanExpression nameEq(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        return equipment.name.contains(name);
    }

    /*
     * 정렬 요청을 ORDER BY 절로 변환합니다.
     *
     * -- 정렬 조건이 지원하는 경우
     *      ORDER BY e.created_at ASC|DESC
     *             , e.id ASC|DESC
     * -- 정렬 조건이 없거나 지원하지 않는 경우
     *      ORDER BY e.id DESC
     */
    private List<OrderSpecifier<?>> resolveSortOrders(Pageable pageable) {
        Sort sort = pageable.getSort();
        if (!sort.isSorted()) {
            return List.of(equipment.id.desc());
        }
        for (Sort.Order order : sort) {
            String property = order.getProperty();
            if ("createdAt".equals(property)) {
                return order.isAscending()
                        ? List.of(equipment.createdAt.asc(), equipment.id.asc())
                        : List.of(equipment.createdAt.desc(), equipment.id.desc());
            }
        }
        return List.of(equipment.id.desc());
    }

}
