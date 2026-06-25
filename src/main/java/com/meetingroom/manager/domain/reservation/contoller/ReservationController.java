package com.meetingroom.manager.domain.reservation.contoller;

import jakarta.validation.Valid;
import com.meetingroom.manager.domain.reservation.dto.ReservationCreateRequestDto;
import com.meetingroom.manager.domain.reservation.dto.ReservationDto;
import com.meetingroom.manager.domain.reservation.dto.ReservationListDto;
import com.meetingroom.manager.domain.reservation.dto.ReservationListSearchRequestDto;
import com.meetingroom.manager.domain.reservation.dto.ReservationUpdateRequestDto;
import com.meetingroom.manager.domain.reservation.service.ReservationService;
import com.meetingroom.manager.domain.user.entity.User;
import com.meetingroom.manager.global.common.annotation.LoginUser;
import com.meetingroom.manager.global.common.response.ApiResponse;
import com.meetingroom.manager.global.common.response.OffsetPageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/company/{companyId}/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    // 예약 등록
    @PostMapping
    public ResponseEntity<ApiResponse<ReservationDto>> createReservation(
            @LoginUser User currentUser,
            @PathVariable Long companyId,
            @Valid @RequestBody ReservationCreateRequestDto reservationCreateRequestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(reservationService.createReservation(currentUser, companyId, reservationCreateRequestDto)));
    }

    // 예약 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<OffsetPageResponseDto<ReservationListDto>>> getReservations(
            @LoginUser User currentUser,
            @PathVariable Long companyId,
            @PageableDefault(size = 10, sort = "startAt", direction = Sort.Direction.DESC) Pageable pageable,
            @Valid @ModelAttribute ReservationListSearchRequestDto reservationListSearchRequestDto
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                reservationService.getReservations(currentUser, companyId, pageable, reservationListSearchRequestDto)));
    }

    // 예약 수정
    @PutMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationDto>> updateReservation(
            @LoginUser User currentUser,
            @PathVariable Long companyId,
            @PathVariable Long reservationId,
            @Valid @RequestBody ReservationUpdateRequestDto reservationUpdateRequestDto
    ) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.updateReservation(currentUser, companyId, reservationId, reservationUpdateRequestDto)));
    }

    // 예약 취소
    @PostMapping("/{reservationId}/cancel")
    public ResponseEntity<ApiResponse<ReservationDto>> cancelReservation(
            @LoginUser User currentUser,
            @PathVariable Long companyId,
            @PathVariable Long reservationId
    ) {
        return ResponseEntity.ok(ApiResponse.success(reservationService.cancelReservation(currentUser, companyId, reservationId)));
    }

}
