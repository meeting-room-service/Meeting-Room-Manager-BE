package com.meetingroom.manager.domain.inquiry.entity;


import jakarta.persistence.*;
import com.meetingroom.manager.global.common.entity.BaseEntity;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InquiryReply extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 아이디

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 내용

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id", nullable = false, unique = true)
    private Inquiry inquiry; // 문의 아이디

    @Builder(access = AccessLevel.PRIVATE)
    private InquiryReply(String content) {
        this.content = content;
    }

    public static InquiryReply createInquiryReply(String content) {
        return InquiryReply.builder()
                .content(content)
                .build();
    }
}
