package com.meetingroom.manager.domain.category.controller;

import com.meetingroom.manager.domain.category.dto.CategoryListDto;
import com.meetingroom.manager.domain.category.enums.CategoryType;
import com.meetingroom.manager.domain.category.service.CategoryService;
import com.meetingroom.manager.domain.user.entity.User;
import com.meetingroom.manager.global.common.annotation.LoginUser;
import com.meetingroom.manager.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    // 카테고리 목록 전체 조회
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CategoryListDto>>> getAllCategories(
            @LoginUser User currentUser,
            @RequestParam("type") CategoryType categoryType
    ) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAllCategories(currentUser, categoryType)));
    }
}
