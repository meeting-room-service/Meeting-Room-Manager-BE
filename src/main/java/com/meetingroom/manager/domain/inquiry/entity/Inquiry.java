package com.meetingroom.manager.domain.inquiry.entity;

import jakarta.persistence.*;
import com.meetingroom.manager.domain.category.entity.Category;
import com.meetingroom.manager.domain.user.entity.User;
import com.meetingroom.manager.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 아이디

    @Column(nullable = false, length = 50)
    private String title; // 제목

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 내용

    @Column(nullable = false, name = "is_secret", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean secret; // 비공개 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 사용자 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category inquiryCategory; // 카테고리 아이디

    @Builder(access = AccessLevel.PRIVATE)
    private Inquiry(
            User user,
            Category inquiryCategory,
            String title,
            String content,
            boolean secret
    ) {
        this.user = user;
        this.inquiryCategory = inquiryCategory;
        this.title = title;
        this.content = content;
        this.secret = secret;
    }

    public static Inquiry createInquiry(
            User user,
            Category inquiryCategory,
            String title,
            String content,
            boolean secret
    ) {

        return Inquiry.builder()
                .user(user)
                .inquiryCategory(inquiryCategory)
                .title(title)
                .content(content)
                .secret(secret)
                .build();
    }

    public void update(Category inquiryCategory, String title, String content, boolean secret) {
        this.inquiryCategory = inquiryCategory;
        this.title = title;
        this.content = content;
        this.secret = secret;
    }
}
