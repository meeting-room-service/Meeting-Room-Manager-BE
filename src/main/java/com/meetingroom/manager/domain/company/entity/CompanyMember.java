package com.meetingroom.manager.domain.company.entity;

import jakarta.persistence.*;
import com.meetingroom.manager.domain.company.enums.Role;
import com.meetingroom.manager.domain.company.enums.CompanyMemberStatus;
import com.meetingroom.manager.domain.user.entity.User;
import com.meetingroom.manager.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyMember extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 아이디

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // 역할

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'ACTIVE'")
    private CompanyMemberStatus status = CompanyMemberStatus.ACTIVE; // 상태

    @Column(nullable = false)
    private LocalDate joinedDate; // 입사일

    @Column(nullable = true)
    private LocalDate resignedDate; // 퇴사일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company; // 회사 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 사용자 아이디

    @Builder(access = AccessLevel.PRIVATE)
    private CompanyMember(Role role, User user, Company company, LocalDate joinedDate, CompanyMemberStatus status) {
        this.role = role;
        this.user = user;
        this.company = company;
        this.joinedDate = joinedDate;
        this.status = status;
    }

    public static CompanyMember createCompanyMember(Role role, User user, Company company) {
        return CompanyMember.builder()
                .role(role)
                .user(user)
                .company(company)
                .joinedDate(LocalDate.now())
                .status(CompanyMemberStatus.ACTIVE)
                .build();
    }
}
