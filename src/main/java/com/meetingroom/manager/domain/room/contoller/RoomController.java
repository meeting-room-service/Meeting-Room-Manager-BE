package com.meetingroom.manager.domain.room.contoller;

import jakarta.validation.Valid;
import com.meetingroom.manager.domain.room.dto.RoomCreateRequestDto;
import com.meetingroom.manager.domain.room.dto.RoomDto;
import com.meetingroom.manager.domain.room.dto.RoomUpdateRequestDto;
import com.meetingroom.manager.domain.room.dto.RoomListDto;
import com.meetingroom.manager.domain.room.dto.RoomSearchRequestDto;
import com.meetingroom.manager.domain.room.service.RoomService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/company/{companyId}")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    // 회의실 등록
    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<RoomDto>> createRoom(
            @LoginUser User currentUser,
            @PathVariable Long companyId,
            @Valid @RequestBody RoomCreateRequestDto roomCreateRequestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(roomService.createRoom(currentUser, companyId, roomCreateRequestDto)));
    }
    // 회의실 목록 조회
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<OffsetPageResponseDto<RoomListDto>>> getRooms(
            @LoginUser User currentUser,
            @PathVariable Long companyId,
            @PageableDefault(size = 10, sort = "maxCapacity", direction = Sort.Direction.DESC) Pageable pageable,
            @Valid @ModelAttribute RoomSearchRequestDto roomSearchRequestDto
    ) {

        return ResponseEntity.ok(ApiResponse.success(roomService.getRooms(currentUser, companyId, pageable, roomSearchRequestDto)));
    }

    // 관리자 회의실 목록 조회
    @GetMapping("/admin/rooms")
    public ResponseEntity<ApiResponse<OffsetPageResponseDto<RoomListDto>>> getAdminRooms(
            @LoginUser User currentUser,
            @PathVariable Long companyId,
            @PageableDefault(size = 10, sort = "maxCapacity", direction = Sort.Direction.DESC) Pageable pageable,
            @Valid @ModelAttribute RoomSearchRequestDto roomSearchRequestDto
    ) {
        return ResponseEntity.ok(ApiResponse.success(roomService.getAdminRooms(currentUser, companyId, pageable, roomSearchRequestDto)));
    }

    // 회의실 전체 목록 조회
    @GetMapping("/rooms/all")
    public ResponseEntity<ApiResponse<List<RoomListDto>>> getAllRooms(
            @LoginUser User currentUser,
            @PathVariable Long companyId
    ) {
        return ResponseEntity.ok(ApiResponse.success(roomService.getAllRooms(currentUser, companyId)));
    }

    // 회의실 수정
    @PatchMapping("/rooms/{roomId}")
    public ResponseEntity<ApiResponse<RoomDto>> updateRoom(
            @LoginUser User currentUser,
            @PathVariable Long companyId,
            @PathVariable Long roomId,
            @Valid @RequestBody RoomUpdateRequestDto roomUpdateRequestDto
    ) {
        return ResponseEntity.ok(ApiResponse.success(roomService.updateRoom(currentUser, companyId, roomId, roomUpdateRequestDto)));
    }


    // 회의실 삭제
    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<Void> deleteRoom(
            @LoginUser User currentUser,
            @PathVariable Long companyId,
            @PathVariable Long roomId
    ) {
        roomService.deleteRoom(currentUser, companyId, roomId);
        return ResponseEntity.noContent().build();
    }
}
