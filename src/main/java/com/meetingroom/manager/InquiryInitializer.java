package com.meetingroom.manager;

import com.meetingroom.manager.domain.category.entity.Category;
import com.meetingroom.manager.domain.category.exception.CategoryManagerException;
import com.meetingroom.manager.domain.category.repository.CategoryRepository;
import com.meetingroom.manager.domain.inquiry.entity.Inquiry;
import com.meetingroom.manager.domain.inquiry.repository.InquiryRepository;
import com.meetingroom.manager.domain.user.entity.User;
import com.meetingroom.manager.domain.user.exception.UserManagerException;
import com.meetingroom.manager.domain.user.repository.UserRepository;
import com.meetingroom.manager.global.error.code.CategoryErrorCode;
import com.meetingroom.manager.global.error.code.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(10)
@RequiredArgsConstructor
public class InquiryInitializer implements ApplicationRunner {
    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public void run(ApplicationArguments args) {
        User user = userRepository.findByLoginId("testuser01")
                .orElseThrow(() -> new UserManagerException(UserErrorCode.USER_NOT_FOUND));

        Category category = categoryRepository.findById(3L)
                .orElseThrow(() -> new CategoryManagerException(CategoryErrorCode.CATEGORY_NOT_FOUND));

        inquiryRepository.save(Inquiry.createInquiry(
                user,
                category,
                "테스트 문의 샘플",
                "문의 내용입니다.",
                true
        ));
    }
}
