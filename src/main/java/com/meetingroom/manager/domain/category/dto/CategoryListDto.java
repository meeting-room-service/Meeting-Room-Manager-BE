package com.meetingroom.manager.domain.category.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CategoryListDto {
    private Long id;
    private String name;

    @Builder
    private CategoryListDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
