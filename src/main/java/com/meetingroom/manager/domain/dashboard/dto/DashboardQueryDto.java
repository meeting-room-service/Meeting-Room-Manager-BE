package com.meetingroom.manager.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardQueryDto {
    private DashboardRoomQueryDto room;
    private DashboardReservationQueryDto reservation;
    private DashboardInspectionQueryDto inspection;
}
