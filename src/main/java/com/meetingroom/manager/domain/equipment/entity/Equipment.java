package com.meetingroom.manager.domain.equipment.entity;

import jakarta.persistence.*;
import com.meetingroom.manager.domain.company.entity.Company;
import com.meetingroom.manager.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(
        name = "uk_equipment_name_company",
        columnNames = {"name", "company_id"}
))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Equipment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 아이디

    @Column(nullable = false, length = 20)
    private String name; // 이름

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company; // 회사 아이디

    @Builder(access = AccessLevel.PRIVATE)
    private Equipment(String name, Company company) {
        this.name = name;
        this.company = company;
    }

    public static Equipment createEquipment(String name, Company company) {
        return new Equipment(name, company);
    }

    public void update(String name) {
        this.name = name;
    }
}
