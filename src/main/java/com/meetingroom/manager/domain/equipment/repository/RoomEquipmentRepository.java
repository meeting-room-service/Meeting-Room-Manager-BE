package com.meetingroom.manager.domain.equipment.repository;

import com.meetingroom.manager.domain.equipment.entity.RoomEquipment;
import com.meetingroom.manager.domain.room.dto.RoomEquipmentQueryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomEquipmentRepository extends JpaRepository<RoomEquipment, Long> {

    /*
     * 회의실에 연결된 비품 정보를 삭제합니다.
     *
     * DELETE
     *   FROM room_equipment re (회의실-비품)
     *  WHERE re.room_id = ?
     */
    void deleteByRoom_Id(Long roomId);

    /*
     * 비품에 연결된 회의실-비품 정보를 삭제합니다.
     *
     * DELETE
     *   FROM room_equipment re (회의실-비품)
     *  WHERE re.equipment_id = ?
     */
    void deleteByEquipment_Id(Long equipmentId);

    /*
     * 회의실-비품 연결 존재 여부를 확인합니다.
     *
     * SELECT COUNT(1) > 0
     *   FROM room_equipment re (회의실-비품)
     *  WHERE re.room_id = ?
     *    AND re.equipment_id = ?
     */
    boolean existsByRoom_IdAndEquipment_Id(Long roomId, Long equipmentId);

    /*
     * 회의실별 비품 정보를 조회합니다.
     *
     * SELECT re.room_id, re.equipment_id, eq.name, re.quantity, re.status
     *   FROM room_equipment re (회의실-비품)
     *   JOIN equipment eq (비품)
     *     ON re.equipment_id = eq.id
     *  WHERE re.room_id IN (...)
     */
    @Query("""
            SELECT new com.meetingroom.manager.domain.room.dto.RoomEquipmentQueryDto(
                re.room.id, eq.id, eq.name, re.quantity, re.status)
              FROM RoomEquipment re
              JOIN re.equipment eq
             WHERE re.room.id IN :roomIds
            """)
    List<RoomEquipmentQueryDto> findAllRoomIdIn(@Param("roomIds") List<Long> roomIds);
}

