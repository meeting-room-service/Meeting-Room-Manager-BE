package com.meetingroom.manager.domain.reservation.entity;

import jakarta.persistence.*;
import com.meetingroom.manager.domain.company.entity.CompanyMember;
import com.meetingroom.manager.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_reservation_participant_reservation_company_member",
                columnNames = {"reservation_id", "company_member_id"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationParticipant extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation; // 예약 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_member_id", nullable = false)
    private CompanyMember companyMember; // 회사 멤버 아이디

    @Builder(access = AccessLevel.PRIVATE)
    private ReservationParticipant(Reservation reservation, CompanyMember companyMember) {
        this.reservation = reservation;
        this.companyMember = companyMember;
    }

    public static ReservationParticipant createReservationParticipant(Reservation reservation, CompanyMember companyMember) {
        return ReservationParticipant.builder()
                .reservation(reservation)
                .companyMember(companyMember)
                .build();
    }
}
