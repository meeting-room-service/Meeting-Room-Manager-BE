package com.meetingroom.manager.domain.equipment.repository;

import com.meetingroom.manager.domain.equipment.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EquipmentRepository extends JpaRepository<Equipment, Long>, EquipmentRepositoryCustom {

    /*
     * 회사 내 비품 이름 중복 여부를 확인합니다.
     *
     * SELECT COUNT(1) > 0
     *   FROM equipment e (비품)
     *  WHERE e.name = ?
     *    AND e.company_id = ?
     */
    boolean existsByNameAndCompanyId(String name, Long companyId);

    /*
     * 수정 대상 제외 후 회사 내 비품 이름 중복 여부를 확인합니다.
     *
     * SELECT COUNT(1) > 0
     *   FROM equipment e (비품)
     *  WHERE e.name = ?
     *    AND e.company_id = ?
     *    AND e.id <> ?
     */
    @Query("""
           SELECT COUNT(e) > 0
             FROM Equipment e
            WHERE e.name = :name
              AND e.company.id = :companyId
              AND e.id <> :id
           """)
    boolean existsByNameAndCompanyIdAndIdNot(
            @Param("name") String name,
            @Param("companyId") Long companyId,
            @Param("id") Long id
    );
}

