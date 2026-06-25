package com.meetingroom.manager;

import com.meetingroom.manager.domain.category.entity.Category;
import com.meetingroom.manager.domain.category.exception.CategoryManagerException;
import com.meetingroom.manager.domain.category.repository.CategoryRepository;
import com.meetingroom.manager.domain.notice.entity.Notice;
import com.meetingroom.manager.domain.notice.repository.NoticeRepository;
import com.meetingroom.manager.global.error.code.CategoryErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Component
@Order(9)
@RequiredArgsConstructor
public class NoticeInitializer implements ApplicationRunner {
    private final CategoryRepository categoryRepository;
    private final NoticeRepository noticeRepository;

    @Transactional
    @Override
    public void run(ApplicationArguments args) {
        Category category = categoryRepository.findById(1L)
                .orElseThrow(() -> new CategoryManagerException(CategoryErrorCode.CATEGORY_NOT_FOUND));

        ArrayList<Notice> notices = new ArrayList<>();

        for(int i = 1; i <= 100; i++) {
            Notice notice = Notice.createNotice(
                    category,
                    "공지사항 제목 " + i,
                    "공지사항 내용 " + i,
                    0
            );
            notices.add(notice);
        }

        noticeRepository.saveAll(notices);
    }
}
