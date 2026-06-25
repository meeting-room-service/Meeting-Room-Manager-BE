package com.meetingroom.manager.domain.room.service;

import com.meetingroom.manager.domain.company.entity.Company;
import com.meetingroom.manager.domain.company.entity.CompanyMember;
import com.meetingroom.manager.domain.company.enums.Role;
import com.meetingroom.manager.domain.company.exception.CompanyManagerException;
import com.meetingroom.manager.domain.company.exception.CompanyMemberManagerException;
import com.meetingroom.manager.domain.company.repository.CompanyMemberRepository;
import com.meetingroom.manager.domain.company.repository.CompanyRepository;
import com.meetingroom.manager.domain.equipment.dto.RoomEquipmentItemDto;
import com.meetingroom.manager.domain.equipment.entity.Equipment;
import com.meetingroom.manager.domain.equipment.entity.RoomEquipment;
import com.meetingroom.manager.domain.equipment.exception.EquipmentManagerException;
import com.meetingroom.manager.domain.equipment.repository.EquipmentRepository;
import com.meetingroom.manager.domain.equipment.repository.RoomEquipmentRepository;
import com.meetingroom.manager.domain.inspection.repository.InspectionRepository;
import com.meetingroom.manager.domain.room.dto.*;
import com.meetingroom.manager.global.error.code.*;
import com.meetingroom.manager.domain.room.entity.Room;
import com.meetingroom.manager.domain.room.exception.RoomManagerException;
import com.meetingroom.manager.domain.room.mapper.RoomMapper;
import com.meetingroom.manager.domain.room.repository.RoomRepository;
import com.meetingroom.manager.domain.user.entity.User;
import com.meetingroom.manager.domain.user.exception.UserManagerException;
import com.meetingroom.manager.global.common.response.OffsetPageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final RoomEquipmentRepository roomEquipmentRepository;
    private final EquipmentRepository equipmentRepository;
    private final CompanyRepository companyRepository;
    private final CompanyMemberRepository companyMemberRepository;
    private final InspectionRepository inspectionRepository;
    private final RoomMapper roomMapper;

    // 회의실 등록
    @Transactional
    public RoomDto createRoom(User currentUser, Long companyId, RoomCreateRequestDto roomCreateRequestDto) {
        // 인증 여부 확인
        if (!currentUser.isCertificated()) {
            throw new UserManagerException(UserErrorCode.NOT_CERTIFICATED_USER);
        }

        // 현재 사용자 회사 소속 확인
        CompanyMember companyMember = validateCurrentUserBelongsToCompany(currentUser, companyId);

        // 관리자 권한 확인
        validateAdminRole(companyMember);

        // 회의실 이름 중복 확인
        if (roomRepository.existsByNameAndCompany_Id(roomCreateRequestDto.getName(), companyId)) {
            throw new RoomManagerException(RoomErrorCode.ROOM_NAME_DUPLICATE);
        }

        // 회사 조회
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyManagerException(CompanyErrorCode.COMPANY_NOT_FOUND));

        // 회의실 생성
        Room room = Room.createRoom(roomCreateRequestDto.getName(), roomCreateRequestDto.getMaxCapacity(), company);

        // 회의실 저장
        roomRepository.save(room);

        // 회의실 비품 연결
        attachRoomEquipments(room, roomCreateRequestDto.getItems());

        // 회의실 조회
        RoomQueryDto roomQueryDto = roomRepository.getRoomById(room.getId(), LocalDateTime.now())
                .orElseThrow(() -> new RoomManagerException(RoomErrorCode.ROOM_NOT_FOUND));

        List<Long> roomIds = List.of(room.getId());

        // 회의실 비품 목록 조회
        List<RoomEquipmentQueryDto> equipmentRows = roomEquipmentRepository.findAllRoomIdIn(roomIds);

        Map<Long, List<RoomEquipmentQueryDto>> equipmentByRoom = equipmentRows.stream()
                .collect(Collectors.groupingBy(RoomEquipmentQueryDto::getRoomId));

        return roomMapper.toDto(roomQueryDto, equipmentByRoom.getOrDefault(room.getId(), List.of()));
    }

    // 회의실 목록 조회
    public OffsetPageResponseDto<RoomListDto> getRooms(User currentUser, Long companyId, Pageable pageable, RoomSearchRequestDto roomSearchRequestDto
    ) {
        // 인증 여부 확인
        if (!currentUser.isCertificated()) {
            throw new UserManagerException(UserErrorCode.NOT_CERTIFICATED_USER);
        }

        // 현재 사용자 회사 소속 확인
        validateCurrentUserBelongsToCompany(currentUser, companyId);

        LocalDateTime at = LocalDateTime.now();

        // 회의실 목록 조회
        Page<RoomQueryDto> roomPage = roomRepository.getRooms(
                companyId,
                roomSearchRequestDto,
                at,
                pageable
        );

        List<Long> roomIds = roomPage.getContent().stream().map(RoomQueryDto::getId).toList();

        // 회의실 비품 목록 조회
        List<RoomEquipmentQueryDto> equipmentRows = roomIds.isEmpty()
                ? List.of()
                : roomEquipmentRepository.findAllRoomIdIn(roomIds);

        Map<Long, List<RoomEquipmentQueryDto>> equipmentByRoom = equipmentRows.stream()
                .collect(Collectors.groupingBy(RoomEquipmentQueryDto::getRoomId));

        List<RoomListDto> roomDtos = roomPage.getContent().stream()
                .map(roomQueryDto -> roomMapper.toListDto(roomQueryDto, equipmentByRoom.getOrDefault(roomQueryDto.getId(), List.of())))
                .toList();

        return OffsetPageResponseDto.<RoomListDto>builder()
                .totalCount(roomPage.getTotalElements())
                .page(roomPage.getNumber())
                .size(roomPage.getSize())
                .totalPages(roomPage.getTotalPages())
                .first(roomPage.isFirst())
                .last(roomPage.isLast())
                .hasPrevious(roomPage.hasPrevious())
                .hasNext(roomPage.hasNext())
                .content(roomDtos)
                .build();
    }

    // 관리자 회의실 목록 조회
    public OffsetPageResponseDto<RoomListDto> getAdminRooms(
            User currentUser,
            Long companyId,
            Pageable pageable,
            RoomSearchRequestDto roomSearchRequestDto
    ) {
        // 인증 여부 확인
        if (!currentUser.isCertificated()) {
            throw new UserManagerException(UserErrorCode.NOT_CERTIFICATED_USER);
        }

        // 현재 사용자 회사 소속 확인
        CompanyMember companyMember = validateCurrentUserBelongsToCompany(currentUser, companyId);

        // 관리자 권한 확인
        validateAdminRole(companyMember);

        return getRooms(currentUser, companyId, pageable, roomSearchRequestDto);
    }

    // 회의실 전체 목록 조회
    public List<RoomListDto> getAllRooms(User currentUser, Long companyId) {
        // 인증 여부 확인
        if (!currentUser.isCertificated()) {
            throw new UserManagerException(UserErrorCode.NOT_CERTIFICATED_USER);
        }

        // 현재 사용자 회사 소속 확인
        validateCurrentUserBelongsToCompany(currentUser, companyId);

        // 회의실 전체 목록 조회
        List<RoomQueryDto> rooms = roomRepository.getAllRooms(companyId, LocalDateTime.now());

        List<Long> roomIds = rooms.stream().map(RoomQueryDto::getId).toList();

        // 회의실 비품 전체 목록 조회
        List<RoomEquipmentQueryDto> equipmentRows = roomIds.isEmpty()
                ? List.of()
                : roomEquipmentRepository.findAllRoomIdIn(roomIds);

        Map<Long, List<RoomEquipmentQueryDto>> equipmentByRoom = equipmentRows.stream()
                .collect(Collectors.groupingBy(RoomEquipmentQueryDto::getRoomId));

        return rooms.stream()
                .map(roomQueryDto -> roomMapper.toListDto(roomQueryDto, equipmentByRoom.getOrDefault(roomQueryDto.getId(), List.of())))
                .toList();
    }

    // 회의실 수정
    @Transactional
    public RoomDto updateRoom(User currentUser, Long companyId, Long roomId, RoomUpdateRequestDto roomUpdateRequestDto) {
        // 인증 여부 확인
        if (!currentUser.isCertificated()) {
            throw new UserManagerException(UserErrorCode.NOT_CERTIFICATED_USER);
        }

        // 현재 사용자 회사 소속 확인
        CompanyMember companyMember = validateCurrentUserBelongsToCompany(currentUser, companyId);

        // 관리자 권한 확인
        validateAdminRole(companyMember);

        // 회의실 조회
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomManagerException(RoomErrorCode.ROOM_NOT_FOUND));

        // 회의실 이름 중복 확인
        if (roomRepository.existsByNameAndCompany_IdAndIdNot(roomUpdateRequestDto.getName(), companyId, roomId)) {
            throw new RoomManagerException(RoomErrorCode.ROOM_NAME_DUPLICATE);
        }

        // 회의실 업데이트
        room.update(roomUpdateRequestDto.getName(), roomUpdateRequestDto.getMaxCapacity());

        // 기존 회의실 비품 연결 삭제
        roomEquipmentRepository.deleteByRoom_Id(room.getId());

        // 회의실 비품 연결
        attachRoomEquipments(room, roomUpdateRequestDto.getItems());

        // 회의실 조회
        RoomQueryDto roomQueryDto = roomRepository.getRoomById(room.getId(), LocalDateTime.now())
                .orElseThrow(() -> new RoomManagerException(RoomErrorCode.ROOM_NOT_FOUND));

        List<Long> roomIds = List.of(room.getId());

        // 회의실 비품 목록 조회
        List<RoomEquipmentQueryDto> equipmentRows = roomEquipmentRepository.findAllRoomIdIn(roomIds);

        Map<Long, List<RoomEquipmentQueryDto>> equipmentByRoom = equipmentRows.stream()
                .collect(Collectors.groupingBy(RoomEquipmentQueryDto::getRoomId));

        return roomMapper.toDto(roomQueryDto, equipmentByRoom.getOrDefault(room.getId(), List.of()));
    }

    // 회의실 삭제
    @Transactional
    public void deleteRoom(User currentUser, Long companyId, Long roomId) {
        // 인증 여부 확인
        if (!currentUser.isCertificated()) {
            throw new UserManagerException(UserErrorCode.NOT_CERTIFICATED_USER);
        }

        // 현재 사용자 회사 소속 확인
        CompanyMember companyMember = validateCurrentUserBelongsToCompany(currentUser, companyId);

        // 관리자 권한 확인
        validateAdminRole(companyMember);

        // 회의실 조회
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomManagerException(RoomErrorCode.ROOM_NOT_FOUND));

        // 회의실 비품 삭제
        roomEquipmentRepository.deleteByRoom_Id(roomId);

        // 회의실 관련 점검 삭제
        inspectionRepository.deleteByRoom_Id(roomId);

        // 회의실 삭제
        room.softDelete();
    }

    // 현재 사용자 회사 소속 확인
    private CompanyMember validateCurrentUserBelongsToCompany(User currentUser, Long companyId) {
        Optional<CompanyMember> companyMember = companyMemberRepository.findByUser_IdAndCompany_Id(currentUser.getId(), companyId);

        if (companyMember.isEmpty()) {
            throw new CompanyMemberManagerException(CompanyMemberErrorCode.COMPANY_MEMBER_NOT_AUTHORIZED);
        }

        return companyMember.get();
    }

    // 관리자 권한 확인
    private void validateAdminRole(CompanyMember companyMember) {
        if (companyMember.getRole() != Role.ADMIN) {
            throw new RoomManagerException(RoomErrorCode.ROOM_NOT_AUTHORIZED);
        }
    }

    // 회의실 비품 연결
    private void attachRoomEquipments(Room room, List<RoomEquipmentItemDto> items) {
        for (RoomEquipmentItemDto roomEquipmentItemDto : items) {
            Equipment equipment = equipmentRepository.findById(roomEquipmentItemDto.getEquipmentId())
                    .orElseThrow(() -> new EquipmentManagerException(EquipmentErrorCode.EQUIPMENT_NOT_FOUND));

            if (roomEquipmentRepository.existsByRoom_IdAndEquipment_Id(room.getId(), equipment.getId())) {
                throw new EquipmentManagerException(EquipmentErrorCode.EQUIPMENT_DUPLICATE_IN_ROOM);
            }

            roomEquipmentRepository.save(
                    RoomEquipment.createRoomEquipment(equipment, room, roomEquipmentItemDto.getQuantity(), roomEquipmentItemDto.getStatus())
            );
        }
    }
}
