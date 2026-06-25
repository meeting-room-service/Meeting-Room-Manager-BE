package com.meetingroom.manager.domain.company.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import com.meetingroom.manager.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Company extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 아이디

    @Column(nullable = false, length = 30)
    private String name; // 회사명

    @Column(nullable = false, length = 50)
    private String industry; // 회사 업계

    @Column(nullable = false)
    private LocalDate foundedDate; // 설립 일자

    @Column(length = 200)
    private String introduction; // 회사 소개

    @Column(nullable = false, unique = true, columnDefinition = "CHAR(12)")
    private String businessRegistrationNumber; // 사업자 등록 번호

    @Builder(access = AccessLevel.PRIVATE)
    private Company(
            String name,
            String industry,
            LocalDate foundedDate,
            String introduction,
            int employeeCount,
            String businessRegistrationNumber
    ) {
        this.name = name;
        this.industry = industry;
        this.foundedDate = foundedDate;
        this.introduction = introduction;
        this.businessRegistrationNumber = businessRegistrationNumber;
    }

    public static Company createCompany(
            String name,
            String industry,
            LocalDate foundedDate,
            String introduction,
            String businessRegistrationNumber
    ) {
        return Company.builder()
                .name(name)
                .industry(industry)
                .foundedDate(foundedDate)
                .introduction(introduction)
                .businessRegistrationNumber(businessRegistrationNumber)
                .build();
    }
}
