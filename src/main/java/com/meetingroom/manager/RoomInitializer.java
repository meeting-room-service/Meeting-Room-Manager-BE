package com.meetingroom.manager;

import com.meetingroom.manager.domain.company.repository.CompanyRepository;
import com.meetingroom.manager.domain.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(3)
@RequiredArgsConstructor
public class RoomInitializer implements ApplicationRunner {
    private final CompanyRepository companyRepository;
    private final RoomRepository roomRepository;

    @Transactional
    @Override
    public void run(ApplicationArguments args) {
    }
}
