package com.meetingroom.manager.domain.reservation.mapper;

import com.meetingroom.manager.domain.reservation.dto.ReservationRoomDto;
import com.meetingroom.manager.domain.reservation.dto.ReservationRoomQueryDto;
import com.meetingroom.manager.domain.room.entity.Room;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservationRoomMapper {

    default ReservationRoomDto toReservationRoomDto(ReservationRoomQueryDto reservationRoomQueryDto) {
        if (reservationRoomQueryDto == null) {
            return null;
        }
        return ReservationRoomDto.builder()
                .id(reservationRoomQueryDto.getId())
                .name(reservationRoomQueryDto.getName())
                .build();
    }

    default ReservationRoomDto toReservationRoomDto(Room room) {
        if (room == null) {
            return null;
        }
        return ReservationRoomDto.builder()
                .id(room.getId())
                .name(room.getName())
                .build();
    }
}
