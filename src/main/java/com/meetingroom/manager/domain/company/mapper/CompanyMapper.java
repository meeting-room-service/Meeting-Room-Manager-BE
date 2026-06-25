package com.meetingroom.manager.domain.company.mapper;

import com.meetingroom.manager.domain.company.dto.CompanyDto;
import com.meetingroom.manager.domain.company.dto.CompanyListDto;
import com.meetingroom.manager.domain.company.entity.Company;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    default CompanyDto toDto(Company company) {
        if (company == null) {
            return null;
        }
        return CompanyDto.builder()
                .id(company.getId())
                .name(company.getName())
                .logo(null)
                .industry(company.getIndustry())
                .foundedDate(company.getFoundedDate())
                .introduction(company.getIntroduction())
                .businessRegistrationNumber(company.getBusinessRegistrationNumber())
                .build();
    }

    default CompanyListDto toListDto(Company company) {
        if (company == null) {
            return null;
        }
        return CompanyListDto.builder()
                .id(company.getId())
                .name(company.getName())
                .logo(null)
                .build();
    }
}

