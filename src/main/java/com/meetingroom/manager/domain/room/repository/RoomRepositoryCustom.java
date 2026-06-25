package com.meetingroom.manager.domain.room.repository;

import com.meetingroom.manager.domain.room.dto.RoomQueryDto;
import com.meetingroom.manager.domain.room.dto.RoomSearchRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RoomRepositoryCustom {
    Optional<RoomQueryDto> getRoomById(Long roomId, LocalDateTime at);
    Page<RoomQueryDto> getRooms(Long companyId, RoomSearchRequestDto roomSearchRequestDto, LocalDateTime at, Pageable pageable);
    List<RoomQueryDto> getAllRooms(Long companyId, LocalDateTime at);
}
