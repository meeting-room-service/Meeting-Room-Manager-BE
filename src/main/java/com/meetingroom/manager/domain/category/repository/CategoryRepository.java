package com.meetingroom.manager.domain.category.repository;

import com.meetingroom.manager.domain.category.entity.Category;
import com.meetingroom.manager.domain.category.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    /*
     * 타입에 맞는 카테고리 목록을 조회합니다.
     *
     * SELECT c.*
     *   FROM category c (카테고리)
     *  WHERE c.type = ?
     */
    List<Category> findByType(CategoryType type);
}
