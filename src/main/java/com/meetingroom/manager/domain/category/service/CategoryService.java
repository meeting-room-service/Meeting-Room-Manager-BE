package com.meetingroom.manager.domain.category.service;

import com.meetingroom.manager.domain.category.dto.CategoryListDto;
import com.meetingroom.manager.domain.category.enums.CategoryType;
import com.meetingroom.manager.domain.category.mapper.CategoryMapper;
import com.meetingroom.manager.domain.category.repository.CategoryRepository;
import com.meetingroom.manager.domain.user.entity.User;
import com.meetingroom.manager.domain.user.exception.UserManagerException;
import com.meetingroom.manager.global.error.code.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    // 카테고리 목록 전체 조회
    public List<CategoryListDto> getAllCategories(User currentUser, CategoryType categoryType) {
        // 인증 여부 확인
        if (!currentUser.isCertificated()) {
            throw new UserManagerException(UserErrorCode.NOT_CERTIFICATED_USER);
        }

        // 카테고리 유형에 따른 목록 조회
        return categoryRepository.findByType(categoryType).stream()
                .map(categoryMapper::toListDto)
                .toList();
    }
}
