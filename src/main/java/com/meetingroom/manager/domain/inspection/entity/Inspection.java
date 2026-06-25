package com.meetingroom.manager.domain.inspection.entity;

import jakarta.persistence.*;
import com.meetingroom.manager.domain.company.entity.Company;
import com.meetingroom.manager.domain.room.entity.Room;
import com.meetingroom.manager.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inspection extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 아이디

    @Column(nullable = false, length = 40)
    private String name; // 이름

    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime startAt; // 시작 일시

    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime endAt; // 종료 일시

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company; // 회사 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room; // 회의실 아이디

    @Builder(access = AccessLevel.PRIVATE)
    private Inspection(String name, LocalDateTime startAt, LocalDateTime endAt, Company company, Room room) {
        this.name = name;
        this.startAt = startAt;
        this.endAt = endAt;
        this.company = company;
        this.room = room;
    }

    public static Inspection createInspection(String name, LocalDateTime startAt, LocalDateTime endAt, Room room) {
        return new Inspection(name, startAt, endAt, room.getCompany(), room);
    }

    public void update(String name, LocalDateTime startAt, LocalDateTime endAt, Room room) {
        this.name = name;
        this.startAt = startAt;
        this.endAt = endAt;
        this.room = room;
    }
}
