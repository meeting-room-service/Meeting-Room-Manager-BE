package com.meetingroom.manager.domain.room.entity;

import jakarta.persistence.*;
import com.meetingroom.manager.domain.company.entity.Company;
import com.meetingroom.manager.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(
        name = "uk_room_name_company",
        columnNames = {"name", "company_id"}
))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 아이디

    @Column(nullable = false, length = 59)
    private String name; // 이름

    @Column(nullable = false)
    private int maxCapacity; // 최대 수용 인원

    @Column(nullable = false , name = "is_deleted", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean deleted; // 삭제 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company; // 회사 아이디

    @Builder(access = AccessLevel.PRIVATE)
    private Room(String name, int maxCapacity, Company company) {
        this.name = name;
        this.maxCapacity = maxCapacity;
        this.company = company;
    }

    public static Room createRoom(String name, int maxCapacity, Company company) {
        return new Room(name, maxCapacity, company);
    }

    public void update(String name, Integer maxCapacity) {
        if (name != null) {
            this.name = name;
        }
        if (maxCapacity != null) {
            this.maxCapacity = maxCapacity;
        }
    }

    public void softDelete() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSSS");

        String timestamp = LocalDateTime.now().format(formatter);

        String suffix = "_deleted_" + timestamp;

        this.name += suffix;

        this.deleted = true;
    }
}
