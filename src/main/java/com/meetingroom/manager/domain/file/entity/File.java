package com.meetingroom.manager.domain.file.entity;

import jakarta.persistence.*;
import com.meetingroom.manager.domain.file.enums.FileType;
import com.meetingroom.manager.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id; // 아이디

    @Column(nullable = false, length = 255)
    private String name; // 이름

    @Column(nullable = false)
    private long size; // 파일 사이즈

    @Column(nullable = false, length = 10)
    private String extension; // 확장자

    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String url; // URL

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FileType type; // 유형

    @Column(nullable = false)
    private Long targetId; // 유형별 ID

    @Builder(access = AccessLevel.PRIVATE)
    private File(String name, long size, String extension, String url, FileType type, Long targetId) {
        this.name = name;
        this.size = size;
        this.extension = extension;
        this.url = url;
        this.type = type;
        this.targetId = targetId;
    }

    public static File createFile(String name, long size, String extension, String url, FileType type, Long targetId) {
        return File.builder()
                .name(name)
                .size(size)
                .extension(extension)
                .url(url)
                .type(type)
                .targetId(targetId)
                .build();
    }

    public void updateImage(String name, long size, String extension, String url) {
        this.name = name;
        this.size = size;
        this.extension = extension;
        this.url = url;
    }
}
