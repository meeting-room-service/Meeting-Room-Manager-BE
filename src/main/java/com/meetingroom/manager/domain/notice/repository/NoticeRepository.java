package com.meetingroom.manager.domain.notice.repository;

import com.meetingroom.manager.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeRepositoryCustom {

    /*
     * 공지 단건과 카테고리 정보를 함께 조회합니다.
     *
     *    SELECT n.*
     *         , c.*
     *      FROM notice n (공지)
     * LEFT JOIN category c (카테고리)
     *        ON n.category_id = c.id
     *     WHERE n.id = ?
     */
    @Query("""
           SELECT n
             FROM Notice n
             LEFT JOIN FETCH n.noticeCategory
            WHERE n.id = :id
           """)
    Optional<Notice> findByIdWithCategory(@Param("id") Long id);
}

