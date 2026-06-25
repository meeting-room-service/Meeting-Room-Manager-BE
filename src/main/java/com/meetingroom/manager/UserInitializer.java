package com.meetingroom.manager;

import com.meetingroom.manager.domain.user.entity.User;
import com.meetingroom.manager.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Order(2)
@RequiredArgsConstructor
public class UserInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void run(ApplicationArguments args) {
        User user1 = User.createUser(
                "testuser01",
                passwordEncoder.encode("Test#pwd01"),
                "테스트유저",
                "testnick01",
                "test.user01@example.org"
        );

        User user2 = User.createUser(
                "testuser02",
                passwordEncoder.encode("Test#pwd02"),
                "테스트유저",
                "testnick02",
                "test.user02@example.org"
        );

        User user3 = User.createUser(
                "testuser03",
                passwordEncoder.encode("Test#pwd03"),
                "테스트유저",
                "testnick03",
                "test.user03@example.org"
        );

        User user4 = User.createUser(
                "testuser04",
                passwordEncoder.encode("Test#pwd04"),
                "테스트유저",
                "testnick04",
                "test.user04@example.org"
        );

        userRepository.saveAll(List.of(user1, user2, user3, user4));
    }
}
