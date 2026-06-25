package com.meetingroom.manager.domain.user.mapper;

import com.meetingroom.manager.domain.user.dto.UserProfileDto;
import com.meetingroom.manager.domain.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    default UserProfileDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return UserProfileDto.builder()
                .nickname(user.getNickname())
                .profileImageUrl(null)
                .build();
    }
}
