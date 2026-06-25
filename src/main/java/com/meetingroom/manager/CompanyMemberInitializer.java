package com.meetingroom.manager;

import com.meetingroom.manager.domain.company.entity.Company;
import com.meetingroom.manager.domain.company.entity.CompanyMember;
import com.meetingroom.manager.domain.company.enums.Role;
import com.meetingroom.manager.domain.company.exception.CompanyManagerException;
import com.meetingroom.manager.domain.company.repository.CompanyMemberRepository;
import com.meetingroom.manager.domain.company.repository.CompanyRepository;
import com.meetingroom.manager.domain.user.entity.User;
import com.meetingroom.manager.domain.user.exception.UserManagerException;
import com.meetingroom.manager.domain.user.repository.UserRepository;
import com.meetingroom.manager.global.error.code.CompanyErrorCode;
import com.meetingroom.manager.global.error.code.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(3)
@RequiredArgsConstructor
public class CompanyMemberInitializer implements ApplicationRunner {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CompanyMemberRepository companyMemberRepository;

    @Transactional
    @Override
    public void run(ApplicationArguments args) {
        User user1 = userRepository.findByLoginId("testuser01")
                .orElseThrow(() -> new UserManagerException(UserErrorCode.USER_NOT_FOUND));

        User user2 = userRepository.findByLoginId("testuser02")
                .orElseThrow(() -> new UserManagerException(UserErrorCode.USER_NOT_FOUND));

        User user3 = userRepository.findByLoginId("testuser03")
                .orElseThrow(() -> new UserManagerException(UserErrorCode.USER_NOT_FOUND));

        Company company = companyRepository.findById(1L)
                .orElseThrow(() -> new CompanyManagerException(CompanyErrorCode.COMPANY_NOT_FOUND));

        companyMemberRepository.save(CompanyMember.createCompanyMember(Role.ADMIN, user1, company));
        companyMemberRepository.save(CompanyMember.createCompanyMember(Role.EMPLOYEE, user2, company));
        companyMemberRepository.save(CompanyMember.createCompanyMember(Role.EMPLOYEE, user3, company));
    }
}
