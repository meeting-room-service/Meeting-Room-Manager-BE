package com.meetingroom.manager.domain.notice.entity;

import jakarta.persistence.*;
import com.meetingroom.manager.domain.category.entity.Category;
import com.meetingroom.manager.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 아이디

    @Column(nullable = false, length = 50)
    private String title; // 제목

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 내용

    @ColumnDefault("0")
    @Column(nullable = false)
    private int viewCount; // 조회 수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category noticeCategory; // 카테고리 아이디

    public void increaseViewCount() {
        this.viewCount++;
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Notice(String title, String content, Category noticeCategory, int viewCount) {
        this.title = title;
        this.content = content;
        this.noticeCategory = noticeCategory;
        this.viewCount = viewCount;
    }

    public static Notice createNotice(
            Category noticeCategory,
            String title,
            String content,
            int viewCount
    ) {
        return Notice.builder()
                .noticeCategory(noticeCategory)
                .title(title)
                .content(content)
                .viewCount(viewCount)
                .build();
    }
}
