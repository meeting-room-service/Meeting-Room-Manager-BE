package com.meetingroom.manager.domain.inquiry.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class InquiryCategoryDto {
    private Long id;
    private String name;

    @Builder
    private InquiryCategoryDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
