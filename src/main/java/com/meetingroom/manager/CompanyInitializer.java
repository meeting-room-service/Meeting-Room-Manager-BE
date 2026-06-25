package com.meetingroom.manager;

import com.meetingroom.manager.domain.company.entity.Company;
import com.meetingroom.manager.domain.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@Order(1)
@RequiredArgsConstructor
public class CompanyInitializer implements ApplicationRunner {
    private final CompanyRepository companyRepository;

    @Transactional
    @Override
    public void run(ApplicationArguments args) {
        companyRepository.save(Company.createCompany(
                "테스트 회사",
                "IT",
                LocalDate.of(2020, 1, 1),
                "",
                "123-45-67890"
        ));
    }
}
