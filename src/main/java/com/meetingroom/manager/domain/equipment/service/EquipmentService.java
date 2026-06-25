package com.meetingroom.manager.domain.equipment.service;

import com.meetingroom.manager.domain.company.entity.Company;
import com.meetingroom.manager.domain.company.entity.CompanyMember;
import com.meetingroom.manager.domain.company.enums.Role;
import com.meetingroom.manager.domain.company.exception.CompanyManagerException;
import com.meetingroom.manager.domain.company.exception.CompanyMemberManagerException;
import com.meetingroom.manager.domain.company.repository.CompanyMemberRepository;
import com.meetingroom.manager.domain.company.repository.CompanyRepository;
import com.meetingroom.manager.domain.equipment.dto.EquipmentCreateRequestDto;
import com.meetingroom.manager.domain.equipment.dto.EquipmentDto;
import com.meetingroom.manager.domain.equipment.dto.EquipmentListDto;
import com.meetingroom.manager.domain.equipment.dto.EquipmentQueryDto;
import com.meetingroom.manager.domain.equipment.dto.EquipmentUpdateRequestDto;
import com.meetingroom.manager.domain.equipment.entity.Equipment;
import com.meetingroom.manager.domain.equipment.exception.EquipmentManagerException;
import com.meetingroom.manager.domain.equipment.mapper.EquipmentMapper;
import com.meetingroom.manager.domain.equipment.repository.EquipmentRepository;
import com.meetingroom.manager.domain.equipment.repository.RoomEquipmentRepository;
import com.meetingroom.manager.domain.user.entity.User;
import com.meetingroom.manager.domain.user.exception.UserManagerException;
import com.meetingroom.manager.global.common.response.OffsetPageResponseDto;
import com.meetingroom.manager.global.error.code.CompanyErrorCode;
import com.meetingroom.manager.global.error.code.CompanyMemberErrorCode;
import com.meetingroom.manager.global.error.code.EquipmentErrorCode;
import com.meetingroom.manager.global.error.code.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;
    private final RoomEquipmentRepository roomEquipmentRepository;
    private final CompanyRepository companyRepository;
    private final CompanyMemberRepository companyMemberRepository;
    private final EquipmentMapper equipmentMapper;

    // 비품 등록
    @Transactional
    public EquipmentDto createEquipment(
            User currentUser,
            Long companyId,
            EquipmentCreateRequestDto equipmentCreateRequestDto
    ) {
        // 인증 여부 확인
        if (!currentUser.isCertificated()) {
            throw new UserManagerException(UserErrorCode.NOT_CERTIFICATED_USER);
        }

        // 현재 사용자 회사 소속 확인
        CompanyMember companyMember = validateCurrentUserBelongsToCompany(currentUser, companyId);

        // 관리자 권한 확인
        validateAdminRole(companyMember);

        // 비품 이름 중복 확인
        if (equipmentRepository.existsByNameAndCompanyId(equipmentCreateRequestDto.getName(), companyId)) {
            throw new EquipmentManagerException(EquipmentErrorCode.EQUIPMENT_ALREADY_EXISTS);
        }

        // 회사 조회
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyManagerException(CompanyErrorCode.COMPANY_NOT_FOUND));

        // 비품 저장
        Equipment equipment = equipmentRepository.save(
                Equipment.createEquipment(equipmentCreateRequestDto.getName(), company));

        return equipmentMapper.toDto(equipment);
    }

    // 비품 목록 조회
    public OffsetPageResponseDto<EquipmentListDto> getEquipments(User currentUser, Long companyId, String name, Pageable pageable) {
        // 인증 여부 확인
        if (!currentUser.isCertificated()) {
            throw new UserManagerException(UserErrorCode.NOT_CERTIFICATED_USER);
        }

        // 현재 사용자 회사 소속 확인
        CompanyMember companyMember = validateCurrentUserBelongsToCompany(currentUser, companyId);

        // 관리자 권한 확인
        validateAdminRole(companyMember);

        // 비품 목록 조회
        Page<EquipmentQueryDto> page = equipmentRepository.getEquipments(companyId, name, pageable);

        List<EquipmentQueryDto> rows = page.getContent();

        List<EquipmentListDto> equipmentDtos = rows.stream()
                .map(equipmentMapper::toListDto)
                .toList();

        return OffsetPageResponseDto.<EquipmentListDto>builder()
                .totalCount(page.getTotalElements())
                .page(page.getNumber())
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                    .hasPrevious(page.hasPrevious())
                .hasNext(page.hasNext())
                .content(equipmentDtos)
                .build();
    }

    // 비품 목록 전체 조회
    public List<EquipmentListDto> getAllEquipments(User currentUser, Long companyId) {
        // 인증 여부 확인
        if (!currentUser.isCertificated()) {
            throw new UserManagerException(UserErrorCode.NOT_CERTIFICATED_USER);
        }

        // 현재 사용자 회사 소속 확인
        validateCurrentUserBelongsToCompany(currentUser, companyId);

        // 비품 목록 전체 조회
        return equipmentRepository.getAllEquipments(companyId).stream()
                .map(equipmentMapper::toListDto)
                .toList();
    }

    // 비품 수정
    @Transactional
    public EquipmentDto updateEquipment(
            User currentUser,
            Long companyId,
            Long equipmentId,
            EquipmentUpdateRequestDto equipmentUpdateRequestDto
    ) {
        // 인증 여부 확인
        if (!currentUser.isCertificated()) {
            throw new UserManagerException(UserErrorCode.NOT_CERTIFICATED_USER);
        }

        // 현재 사용자 회사 소속 확인
        CompanyMember companyMember = validateCurrentUserBelongsToCompany(currentUser, companyId);

        // 관리자 권한 확인
        validateAdminRole(companyMember);

        // 비품 조회
        Equipment equipment = equipmentRepository.findById(equipmentId)
                        .orElseThrow(() -> new EquipmentManagerException(EquipmentErrorCode.EQUIPMENT_NOT_FOUND));

        // 비품 이름 중복 확인
        if (equipmentRepository.existsByNameAndCompanyIdAndIdNot(
                equipmentUpdateRequestDto.getName(), companyId, equipmentId)) {
            throw new EquipmentManagerException(EquipmentErrorCode.EQUIPMENT_ALREADY_EXISTS);
        }

        // 비품 수정
        equipment.update(equipmentUpdateRequestDto.getName());

        // 비품 조회
        EquipmentQueryDto equipmentQueryDto = equipmentRepository.getByEquipmentId(equipmentId)
                .orElseThrow(() -> new EquipmentManagerException(EquipmentErrorCode.EQUIPMENT_NOT_FOUND));

        return equipmentMapper.toDto(equipmentQueryDto);
    }

    // 비품 삭제
    @Transactional
    public void deleteEquipment(User currentUser, Long companyId, Long equipmentId) {
        // 인증 여부 확인
        if (!currentUser.isCertificated()) {
            throw new UserManagerException(UserErrorCode.NOT_CERTIFICATED_USER);
        }

        // 현재 사용자 회사 소속 확인
        CompanyMember companyMember = validateCurrentUserBelongsToCompany(currentUser, companyId);

        // 관리자 권한 확인
        validateAdminRole(companyMember);

        // 비품 조회
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new EquipmentManagerException(EquipmentErrorCode.EQUIPMENT_NOT_FOUND));

        // 비품 연결 정보 삭제
        roomEquipmentRepository.deleteByEquipment_Id(equipment.getId());

        // 비품 삭제
        equipmentRepository.delete(equipment);
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
            throw new CompanyMemberManagerException(CompanyMemberErrorCode.COMPANY_MEMBER_NOT_ADMIN);
        }
    }
}
