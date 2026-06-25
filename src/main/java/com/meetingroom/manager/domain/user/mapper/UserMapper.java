package com.meetingroom.manager.domain.user.mapper;

import com.meetingroom.manager.domain.company.entity.Company;
import com.meetingroom.manager.domain.company.enums.Role;
import com.meetingroom.manager.domain.user.dto.UserDto;
import com.meetingroom.manager.domain.user.dto.UserCompanyDto;
import com.meetingroom.manager.domain.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    default UserDto toDto(User user, Company company, Role role) {
        if (user == null) {
            return null;
        }
        UserCompanyDto userCompanyDto = null;
        if (company != null) {
            userCompanyDto = UserCompanyDto.builder()
                    .id(company.getId())
                    .name(company.getName())
                    .build();
        }
        return UserDto.builder()
                .id(user.getId())
                .loginId(user.getLoginId())
                .name(user.getName())
                .email(user.getEmail())
                .company(userCompanyDto)
                .role(role)
                .certificated(user.isCertificated())
                .build();
    }
}
