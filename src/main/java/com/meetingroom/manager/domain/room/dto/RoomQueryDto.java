package com.meetingroom.manager.domain.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomQueryDto {
    private Long id;
    private String name;
    private int maxCapacity;
    private Long companyId;
    private String status;
}
