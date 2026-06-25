package com.meetingroom.manager.domain.equipment.entity;

import jakarta.persistence.*;
import com.meetingroom.manager.domain.equipment.enums.RoomEquipmentStatus;
import com.meetingroom.manager.domain.room.entity.Room;
import com.meetingroom.manager.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_room_equipment_equipment_room",
                columnNames = {"equipment_id", "room_id"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomEquipment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 아이디

    @Column(nullable = false)
    private int quantity; // 수량

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomEquipmentStatus status; // 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room; // 회의실 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment; // 비품 아이디

    @Builder(access = AccessLevel.PRIVATE)
    private RoomEquipment(Equipment equipment, Room room, int quantity, RoomEquipmentStatus status) {
        this.equipment = equipment;
        this.room = room;
        this.quantity = quantity;
        this.status = status;
    }

    public static RoomEquipment createRoomEquipment(Equipment equipment, Room room, int quantity, RoomEquipmentStatus status) {
        return RoomEquipment.builder()
                .equipment(equipment)
                .room(room)
                .quantity(quantity)
                .status(status != null ? status : RoomEquipmentStatus.AVAILABLE)
                .build();
    }
}
