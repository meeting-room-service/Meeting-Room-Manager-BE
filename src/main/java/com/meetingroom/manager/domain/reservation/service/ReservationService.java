package com.meetingroom.manager.domain.reservation.service;

import com.meetingroom.manager.domain.company.entity.CompanyMember;
import com.meetingroom.manager.domain.company.exception.CompanyMemberManagerException;
import com.meetingroom.manager.domain.company.repository.CompanyMemberRepository;
import com.meetingroom.manager.domain.inspection.repository.InspectionRepository;
import com.meetingroom.manager.domain.reservation.dto.ReservationCreateRequestDto;
import com.meetingroom.manager.domain.reservation.dto.ReservationDto;
import com.meetingroom.manager.domain.reservation.dto.ReservationListDto;
import com.meetingroom.manager.domain.reservation.dto.ReservationListSearchRequestDto;
import com.meetingroom.manager.domain.reservation.dto.ReservationParticipantQueryDto;
import com.meetingroom.manager.domain.reservation.dto.ReservationQueryDto;
import com.meetingroom.manager.domain.reservation.dto.ReservationUpdateRequestDto;
import com.meetingroom.manager.domain.reservation.mapper.ReservationApplicantMapper;
import com.meetingroom.manager.domain.reservation.mapper.ReservationMapper;
import com.meetingroom.manager.domain.reservation.entity.Reservation;
import com.meetingroom.manager.domain.reservation.entity.ReservationParticipant;
import com.meetingroom.manager.domain.reservation.enums.ReservationStatus;
import com.meetingroom.manager.domain.reservation.exception.ReservationManagerException;
import com.meetingroom.manager.domain.reservation.repository.ReservationParticipantRepository;
import com.meetingroom.manager.domain.reservation.repository.ReservationRepository;
import com.meetingroom.manager.domain.room.entity.Room;
import com.meetingroom.manager.domain.room.exception.RoomManagerException;
import com.meetingroom.manager.domain.room.repository.RoomRepository;
import com.meetingroom.manager.domain.user.entity.User;
import com.meetingroom.manager.domain.user.exception.UserManagerException;
import com.meetingroom.manager.global.common.response.OffsetPageResponseDto;
import com.meetingroom.manager.global.error.code.CompanyMemberErrorCode;
import com.meetingroom.manager.global.error.code.ReservationErrorCode;
import com.meetingroom.manager.global.error.code.RoomErrorCode;
import com.meetingroom.manager.global.error.code.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationParticipantRepository reservationParticipantRepository;
    private final RoomRepository roomRepository;
    private final InspectionRepository inspectionRepository;
    private final CompanyMemberRepository companyMemberRepository;
    private final ReservationMapper reservationMapper;
    private final ReservationApplicantMapper reservationApplicantMapper;

    // 예약 등록
    @Transactional
    public ReservationDto createReservation(User currentUser, Long companyId, ReservationCreateRequestDto reservationCreateRequestDto) {
        // 인증 여부 확인
        if (!currentUser.isCertificated()) {
            throw new UserManagerException(UserErrorCode.NOT_CERTIFICATED_USER);
        }

        // 현재 사용자 회사 소속 확인
        CompanyMember applicantMember = validateCurrentUserBelongsToCompany(currentUser, companyId);

        Long roomId = reservationCreateRequestDto.getRoomId();

        // 회의실이 해당 회사 소속인지 확인
        Room room = validateRoomBelongingToCompany(roomId, companyId);

        Set<Long> participantCompanyMemberIds = reservationCreateRequestDto.getParticipantCompanyMemberIds();

        // 회의실 수용 인원 초과하는지 확인
        validateAttendeeCountWithinRoomCapacity(room, participantCompanyMemberIds.size());

        // 예약 참가자들이 같은 회사 소속인지 확인
        List<CompanyMember> participantMembers = validateParticipantCompanyMember(companyId, participantCompanyMemberIds);

        LocalDateTime startAt = reservationCreateRequestDto.getStartAt();
        LocalDateTime endAt = reservationCreateRequestDto.getEndAt();

        // 요청 일시에 회의실이 점검 중인지 확인
        validateRoomNotUnderInspection(room, startAt, endAt);

        // 요청 일시에 회의실에 이미 확정된 예약이 존재하는지 확인
        validateNoOverlappingReservationOnRoom(room, startAt, endAt, null);

        // 요청 일시에 참가자들이 다른 예약에 참여 중인지 확인
        validateParticipantsHaveNoScheduleConflict(participantCompanyMemberIds, startAt, endAt, null);

        // 예약 생성
        Reservation reservation = Reservation.createReservation(applicantMember, room, reservationCreateRequestDto.getTitle(), startAt, endAt);

        // 에약 저장
        reservationRepository.save(reservation);

        // 예약 참가자 생성
        List<ReservationParticipant> participants = participantMembers.stream()
                .map(cm -> ReservationParticipant.createReservationParticipant(reservation, cm))
                .toList();

        // 예약 참가자 저장
        reservationParticipantRepository.saveAll(participants);

        return reservationMapper.toDto(reservation, participants);
    }

    // 예약 목록 조회
    public OffsetPageResponseDto<ReservationListDto> getReservations(
            User currentUser,
            Long companyId,
            Pageable pageable,
            ReservationListSearchRequestDto reservationListSearchRequestDto
    ) {
        // 인증 여부 확인
        if (!currentUser.isCertificated()) {
            throw new UserManagerException(UserErrorCode.NOT_CERTIFICATED_USER);
        }

        // 현재 사용자 회사 소속 확인
        CompanyMember companyMember = validateCurrentUserBelongsToCompany(currentUser, companyId);

        Page<ReservationQueryDto> page = reservationRepository.getReservations(
                companyMember.getId(), companyId, reservationListSearchRequestDto, pageable);

        List<ReservationQueryDto> rows = page.getContent();
        List<Long> reservationIds = rows.stream().map(ReservationQueryDto::getId).toList();

        List<ReservationParticipantQueryDto> allParticipantRows =
                reservationParticipantRepository.getReservationParticipantsByReservationIds(reservationIds);

        Map<Long, List<ReservationParticipantQueryDto>> participantsByReservationId = allParticipantRows.stream()
                .collect(Collectors.groupingBy(ReservationParticipantQueryDto::getReservationId));

        List<ReservationListDto> reservationDtos = rows.stream()
                .map(q -> reservationMapper.toListDto(
                        q,
                        participantsByReservationId.getOrDefault(q.getId(), List.of()),
                        reservationApplicantMapper))
                .toList();

        return OffsetPageResponseDto.<ReservationListDto>builder()
                .totalCount(page.getTotalElements())
                .page(page.getNumber())
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasPrevious(page.hasPrevious())
                .hasNext(page.hasNext())
                .content(reservationDtos)
                .build();
    }
    // 예약 수정
    @Transactional
    public ReservationDto updateReservation(
            User currentUser,
            Long companyId,
            Long reservationId,
            ReservationUpdateRequestDto reservationUpdateRequestDto
    ) {
        // 인증 여부 확인
        if (!currentUser.isCertificated()) {
            throw new UserManagerException(UserErrorCode.NOT_CERTIFICATED_USER);
        }

        // 예약 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationManagerException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        // 예약 신청자와 현재 사용자가 같은지 확인
        if (!reservation.getCompanyMember().getUser().getId().equals(currentUser.getId())) {
            throw new ReservationManagerException(ReservationErrorCode.RESERVATION_NOT_APPLICANT);
        }

        // 이미 취소된 예약인지 확인
        if (reservation.getStatus() == ReservationStatus.CANCELED) {
            throw new ReservationManagerException(ReservationErrorCode.RESERVATION_NOT_MODIFIABLE_CANCELED);
        }

        // 예약이 이미 시작되지 않았는지 확인
        validateReservationNotStarted(reservation.getStartAt(), ReservationErrorCode.RESERVATION_NOT_MODIFIABLE_STARTED);

        // 현재 사용자 회사 소속 확인
        validateCurrentUserBelongsToCompany(currentUser, companyId);

        Long roomId = reservationUpdateRequestDto.getRoomId();

        // 회의실이 해당 회사 소속인지 확인
        Room room = validateRoomBelongingToCompany(roomId, companyId);

        Set<Long> participantCompanyMemberIds = reservationUpdateRequestDto.getParticipantCompanyMemberIds();

        // 회의실 수용 인원 초과하는지 확인
        validateAttendeeCountWithinRoomCapacity(room, participantCompanyMemberIds.size());

        // 예약 참가자들이 같은 회사 소속인지 확인
        List<CompanyMember> participantMembers = validateParticipantCompanyMember(companyId, participantCompanyMemberIds);

        LocalDateTime startAt = reservationUpdateRequestDto.getStartAt();
        LocalDateTime endAt = reservationUpdateRequestDto.getEndAt();

        // 요청 일시에 회의실이 점검 중인지 확인
        validateRoomNotUnderInspection(room, startAt, endAt);

        // 요청 일시에 회의실에 이미 확정된 예약이 존재하는지 확인
        validateNoOverlappingReservationOnRoom(room, startAt, endAt, reservationId);

        // 요청 일시에 참가자들이 다른 예약에 참여 중인지 확인
        validateParticipantsHaveNoScheduleConflict(participantCompanyMemberIds, startAt, endAt, reservationId);

        // 예약 업데이트
        reservation.update(room, reservationUpdateRequestDto.getTitle(), startAt, endAt);

        // 기존 예약 참가자 삭제
        List<Long> participantRowIds = reservationParticipantRepository.findIdsByReservationId(reservationId);
        if (!participantRowIds.isEmpty()) {
            reservationParticipantRepository.deleteByIds(participantRowIds);
        }

        // 예약 참가자 생성
        List<ReservationParticipant> participants = participantMembers.stream()
                .map(cm -> ReservationParticipant.createReservationParticipant(reservation, cm))
                .toList();

        // 예약 참가자 저장
        reservationParticipantRepository.saveAll(participants);

        return reservationMapper.toDto(reservation, participants);
    }

    // 예약 취소
    @Transactional
    public ReservationDto cancelReservation(User currentUser, Long companyId, Long reservationId) {
        // 인증 여부 확인
        if (!currentUser.isCertificated()) {
            throw new UserManagerException(UserErrorCode.NOT_CERTIFICATED_USER);
        }

        // 예약 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationManagerException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        // 예약 신청자와 현재 사용자가 같은지 확인
        if (!reservation.getCompanyMember().getUser().getId().equals(currentUser.getId())) {
            throw new ReservationManagerException(ReservationErrorCode.RESERVATION_NOT_APPLICANT);
        }

        // 현재 사용자 회사 소속 확인
        CompanyMember companyMember = validateCurrentUserBelongsToCompany(currentUser, companyId);

        // 이미 취소된 예약인지 확인
        if (reservation.getStatus() == ReservationStatus.CANCELED) {
            throw new ReservationManagerException(ReservationErrorCode.RESERVATION_NOT_CANCELLABLE_ALREADY_CANCELED);
        }

        // 예약이 이미 시작되지 않았는지 확인
        validateReservationNotStarted(reservation.getStartAt(), ReservationErrorCode.RESERVATION_NOT_CANCELLABLE_STARTED);

        // 예약 취소
        reservation.cancel();

        // 예약 조회
        ReservationQueryDto reservationQueryDto
                = reservationRepository.getReservationById(companyMember.getId(), reservationId)
                .orElseThrow(() -> new ReservationManagerException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        // 예약 참가자 조회
        List<ReservationParticipantQueryDto> participantQueryDtos =
                reservationParticipantRepository.getReservationParticipantsByReservationIds(List.of(reservation.getId()));

        return reservationMapper.toDto(reservationQueryDto, participantQueryDtos);
    }

    // 현재 사용자 회사 소속 확인
    private CompanyMember validateCurrentUserBelongsToCompany(User currentUser, Long companyId) {
        Optional<CompanyMember> companyMember = companyMemberRepository.findByUser_IdAndCompany_Id(currentUser.getId(), companyId);

        if (companyMember.isEmpty()) {
            throw new CompanyMemberManagerException(CompanyMemberErrorCode.COMPANY_MEMBER_NOT_AUTHORIZED);
        }

        return companyMember.get();
    }

    // 회사 소속 회의실 조회
    private Room validateRoomBelongingToCompany(Long roomId, Long companyId) {
        Optional<Room> room = roomRepository.findByIdAndCompany_IdAndDeletedFalse(roomId, companyId);

        if(room.isEmpty()) {
            throw new RoomManagerException(RoomErrorCode.ROOM_NOT_IN_COMPANY);
        }

        return room.get();
    }

    // 회의실 수용 인원 확인
    private void validateAttendeeCountWithinRoomCapacity(Room room, int participantCount) {
        if (participantCount > room.getMaxCapacity()) {
            throw new ReservationManagerException(ReservationErrorCode.RESERVATION_CAPACITY_EXCEEDED);
        }
    }

    // 참가자 회사 소속 확인
    private List<CompanyMember> validateParticipantCompanyMember(Long companyId, Set<Long> participantComapnyIds) {
        List<CompanyMember> participantMembers = companyMemberRepository.findAllByIdInAndCompany_Id(new ArrayList<>(participantComapnyIds), companyId);

        if (participantMembers.size() != participantComapnyIds.size()) {
            throw new ReservationManagerException(ReservationErrorCode.RESERVATION_PARTICIPANT_NOT_IN_COMPANY);
        }

        return participantMembers;
    }

    // 회의실 점검 시간 확인
    private void validateRoomNotUnderInspection(Room room, LocalDateTime startAt, LocalDateTime endAt) {
        if (inspectionRepository.existsByRoom_IdAndStartAtLessThanAndEndAtGreaterThan(room.getId(), endAt, startAt)) {
            throw new ReservationManagerException(ReservationErrorCode.RESERVATION_ROOM_UNDER_INSPECTION);
        }
    }

    // 회의실 예약 중복 확인
    private void validateNoOverlappingReservationOnRoom(
            Room room,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Long excludeReservationId
    ) {
        boolean hasOverlappingReservation = excludeReservationId == null
                ? reservationRepository.existsConfirmedReservationOverlappingOnRoom(
                        room.getId(), endAt, startAt)
                : reservationRepository.existsConfirmedReservationOverlappingOnRoomExcluding(
                        room.getId(), endAt, startAt, excludeReservationId);
        if (hasOverlappingReservation) {
            throw new ReservationManagerException(ReservationErrorCode.RESERVATION_ALREADY_EXISTS);
        }
    }

    // 참가자 일정 충돌 확인
    private void validateParticipantsHaveNoScheduleConflict(
            Set<Long> participantCompanyIds,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Long excludeReservationId
    ) {
        if (participantCompanyIds == null || participantCompanyIds.isEmpty()) {
            return;
        }

        Set<Long> busy = new HashSet<>(reservationRepository.findCompanyMemberIdsWithOverlappingReservationAsParticipant(
                startAt, endAt, new ArrayList<>(participantCompanyIds), excludeReservationId));

        if (!busy.isEmpty()) {
            throw new ReservationManagerException(ReservationErrorCode.RESERVATION_PARTICIPANT_UNAVAILABLE);
        }
    }

    // 예약이 시작되지 않았는지 확인
    private void validateReservationNotStarted(LocalDateTime startAt, ReservationErrorCode reservationErrorCode) {
        if (!LocalDateTime.now().isBefore(startAt)) {
            throw new ReservationManagerException(reservationErrorCode);
        }
    }

}
