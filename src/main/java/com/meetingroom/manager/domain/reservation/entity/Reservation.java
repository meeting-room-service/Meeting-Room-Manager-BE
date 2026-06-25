package com.meetingroom.manager.domain.reservation.entity;

import jakarta.persistence.*;
import com.meetingroom.manager.domain.company.entity.Company;
import com.meetingroom.manager.domain.company.entity.CompanyMember;
import com.meetingroom.manager.domain.room.entity.Room;
import com.meetingroom.manager.domain.reservation.enums.ReservationStatus;
import com.meetingroom.manager.global.common.entity.BaseEntity;
import lombok.Builder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 아이디

    @Column(nullable = false, length = 50)
    private String title; // 제목

    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime startAt; // 시작 일시

    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime endAt; // 종료 일시

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'CONFIRMED'")
    private ReservationStatus status = ReservationStatus.CONFIRMED; // 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company; // 회사 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_member_id", nullable = false)
    private CompanyMember companyMember; // 회사 멤버 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room; // 회의실 아이디

    @Builder(access = AccessLevel.PRIVATE)
    private Reservation(Company company, CompanyMember companyMember, Room room, String title, LocalDateTime startAt, LocalDateTime endAt, ReservationStatus status) {
        this.company = company;
        this.companyMember = companyMember;
        this.room = room;
        this.title = title;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = status == null ? ReservationStatus.CONFIRMED : status;
    }

    public static Reservation createReservation(CompanyMember companyMember, Room room, String title, LocalDateTime startAt, LocalDateTime endAt) {
        return Reservation.builder()
                .company(room.getCompany())
                .companyMember(companyMember)
                .room(room)
                .title(title)
                .startAt(startAt)
                .endAt(endAt)
                .status(ReservationStatus.CONFIRMED)
                .build();
    }

    public void update(Room room, String title, LocalDateTime startAt, LocalDateTime endAt) {
        this.room = room;
        this.title = title;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELED;
    }
}
