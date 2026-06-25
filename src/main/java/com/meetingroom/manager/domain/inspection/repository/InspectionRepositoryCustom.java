package com.meetingroom.manager.domain.inspection.repository;

import com.meetingroom.manager.domain.inspection.dto.InspectionListSearchRequestDto;
import com.meetingroom.manager.domain.inspection.dto.InspectionQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface InspectionRepositoryCustom {
    Optional<InspectionQueryDto> getInspectionById(Long inspectionId);
    Page<InspectionQueryDto> getInspections(Long companyId, Pageable pageable, InspectionListSearchRequestDto inspectionListSearchRequestDto);
}
