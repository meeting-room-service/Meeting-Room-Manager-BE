package com.meetingroom.manager.domain.category.mapper;

import com.meetingroom.manager.domain.category.dto.CategoryListDto;
import com.meetingroom.manager.domain.category.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    default CategoryListDto toListDto(Category category) {
        if (category == null) {
            return null;
        }
        return CategoryListDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
