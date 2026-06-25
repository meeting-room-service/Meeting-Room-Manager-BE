package com.meetingroom.manager.domain.notice.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.meetingroom.manager.domain.notice.dto.NoticeListSearchRequestDto;
import com.meetingroom.manager.domain.notice.entity.Notice;
import com.meetingroom.manager.domain.notice.entity.QNotice;
import com.meetingroom.manager.domain.notice.enums.NoticeSortBy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QNotice notice = QNotice.notice;

    /*
     * 공지 목록과 전체 건수를 페이징 조회합니다.
     *
     *        SELECT COUNT(n.id)
     *          FROM notice n (공지)
     *         WHERE (:categoryId IS NULL OR n.category_id = :categoryId)
     *           AND (:title IS NULL OR n.title LIKE CONCAT('%', :title, '%'))
     *
     *        SELECT n.*
     *          FROM notice n (공지)
     *     LEFT JOIN category c (카테고리)  -- JPQL: FETCH JOIN noticeCategory
     *            ON n.category_id = c.id
     *         WHERE (:categoryId IS NULL OR n.category_id = :categoryId)
     *           AND (:title IS NULL OR n.title LIKE CONCAT('%', :title, '%'))
     * -- 정렬 조건이 지원하는 경우
     *      ORDER BY n.created_at ASC|DESC
     *             , n.id ASC|DESC
     *  또는 ORDER BY n.view_count ASC|DESC
     *             , n.id ASC|DESC
     * -- 정렬 조건이 없거나 지원하지 않는 경우
     *      ORDER BY n.id DESC
     *         LIMIT ? OFFSET ?
     */
    @Override
    public Page<Notice> getNotices(NoticeListSearchRequestDto noticeListSearchRequestDto, Pageable pageable) {
        Long total = queryFactory
                .select(notice.id.count())
                .from(notice)
                .where(
                        eqCategoryId(noticeListSearchRequestDto),
                        titleContains(noticeListSearchRequestDto)
                )
                .fetchOne();

        List<Notice> content = queryFactory
                .selectFrom(notice)
                .leftJoin(notice.noticeCategory).fetchJoin()
                .where(
                        eqCategoryId(noticeListSearchRequestDto),
                        titleContains(noticeListSearchRequestDto)
                )
                .orderBy(resolveSortOrders(pageable).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total == null ? 0L : total);
    }

    /*
     * 카테고리 필터 조건을 생성합니다.
     *
     * WHERE n.category_id = ?
     * -- categoryId가 NULL이면 조건 생략
     */
    private BooleanExpression eqCategoryId(NoticeListSearchRequestDto noticeListSearchRequestDto) {
        if (noticeListSearchRequestDto == null || noticeListSearchRequestDto.getCategoryId() == null) {
            return null;
        }
        return notice.noticeCategory.id.eq(noticeListSearchRequestDto.getCategoryId());
    }

    /*
     * 제목 검색 필터 조건을 생성합니다.
     *
     * WHERE n.title LIKE CONCAT('%', ?, '%')
     * -- title이 NULL 또는 공백이면 조건 생략
     */
    private BooleanExpression titleContains(NoticeListSearchRequestDto noticeListSearchRequestDto) {
        if (noticeListSearchRequestDto == null || !StringUtils.hasText(noticeListSearchRequestDto.getTitle())) {
            return null;
        }
        return notice.title.contains(noticeListSearchRequestDto.getTitle());
    }

    /*
     * 정렬 요청을 ORDER BY 절로 변환합니다.
     *
     * -- 정렬 조건이 지원하는 경우
     *      ORDER BY n.created_at ASC|DESC
     *             , n.id ASC|DESC
     *  또는 ORDER BY n.view_count ASC|DESC
     *             , n.id ASC|DESC
     * -- 정렬 조건이 없거나 지원하지 않는 경우
     *      ORDER BY n.id DESC
     */
    private List<OrderSpecifier<?>> resolveSortOrders(Pageable pageable) {
        Sort sort = pageable.getSort();
        if (!sort.isSorted()) {
            return List.of(notice.id.desc());
        }
        for (Sort.Order order : sort) {
            NoticeSortBy noticeSortBy = NoticeSortBy.fromRequest(order.getProperty()).orElse(null);
            if (noticeSortBy == NoticeSortBy.CREATED_AT) {
                return order.isAscending()
                        ? List.of(notice.createdAt.asc(), notice.id.asc())
                        : List.of(notice.createdAt.desc(), notice.id.desc());
            }
            if (noticeSortBy == NoticeSortBy.VIEW_COUNT) {
                return order.isAscending()
                        ? List.of(notice.viewCount.asc(), notice.id.asc())
                        : List.of(notice.viewCount.desc(), notice.id.desc());
            }
        }

        return List.of(notice.id.desc());
    }
}
