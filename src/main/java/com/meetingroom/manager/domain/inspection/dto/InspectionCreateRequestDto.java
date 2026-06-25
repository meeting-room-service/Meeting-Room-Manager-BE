package com.meetingroom.manager.domain.inspection.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class InspectionCreateRequestDto {

    @NotBlank(message = "점검 이름을 입력해 주세요.")
    @Size(max = 40, message = "점검 이름은 최대 40자까지 입력할 수 있습니다.")
    private String name;

    @NotNull(message = "시작 일시를 입력해 주세요.")
    @FutureOrPresent(message = "시작 일시는 현재 이후여야 합니다.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startAt;

    @NotNull(message = "종료 일시를 입력해 주세요.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endAt;

    @NotNull(message = "회의실을 선택해 주세요.")
    private Long roomId;

    @Builder
    private InspectionCreateRequestDto(
            String name,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Long roomId
    ) {
        this.name = name;
        this.startAt = startAt;
        this.endAt = endAt;
        this.roomId = roomId;
    }

    @JsonIgnore
    @AssertTrue(message = "종료 일시는 시작 일시보다 이후여야 합니다.")
    public boolean isEndAfterStart() {
        if (startAt == null || endAt == null) {
            return true;
        }
        return endAt.isAfter(startAt);
    }
}
