package com.meetingroom.manager;

import com.meetingroom.manager.domain.file.entity.File;
import com.meetingroom.manager.domain.file.enums.FileType;
import com.meetingroom.manager.domain.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(12)
@RequiredArgsConstructor
public class FileInitializer implements ApplicationRunner {
    private final FileRepository fileRepository;

    @Transactional
    @Override
    public void run(ApplicationArguments args) {
        File defaultProfile = File.createFile(
                "default.png",
                20602L,
                "png",
                "https://tanineojt.s3.ap-northeast-2.amazonaws.com/default.png",
                FileType.PROFILE,
                1L
        );

        fileRepository.save(defaultProfile);
    }
}
