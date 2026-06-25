package com.meetingroom.manager.domain.auth.service;

import com.meetingroom.manager.domain.auth.dto.EmailVerificationSendResponseDto;
import com.meetingroom.manager.domain.auth.dto.EmailVerificationSendRequestDto;
import com.meetingroom.manager.domain.auth.dto.EmailVerificationVerifyResponseDto;
import com.meetingroom.manager.domain.auth.dto.EmailVerificationVerifyRequestDto;
import com.meetingroom.manager.domain.auth.enums.EmailVerificationPurpose;
import com.meetingroom.manager.domain.auth.exception.EmailVerificationManagerException;
import com.meetingroom.manager.global.error.code.AuthErrorCode;
import com.meetingroom.manager.global.error.exception.ExternalServiceManagerException;
import com.meetingroom.manager.infra.mail.dto.MailTxtSendDto;
import com.meetingroom.manager.infra.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private static final Duration CODE_TTL = Duration.ofMinutes(5);
    private static final Duration VERIFIED_TTL = Duration.ofMinutes(30);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final StringRedisTemplate stringRedisTemplate;
    private final MailService mailService;

    // 이메일 인증 코드 발송
    public EmailVerificationSendResponseDto sendCode(EmailVerificationSendRequestDto emailVerificationSendRequestDto) {
        EmailVerificationPurpose purpose = EmailVerificationPurpose.defaultIfNull(emailVerificationSendRequestDto.getPurpose());
        String email = emailVerificationSendRequestDto.getEmail();

        String key = purpose.codeKey(email);

        String code = generate6Digits();
        stringRedisTemplate.opsForValue().set(key, code, CODE_TTL);

        try {
            mailService.sendSimpleMail(MailTxtSendDto.builder()
                    .emailAddress(email)
                    .subject(mailSubject(purpose))
                    .content(mailBody(purpose, code))
                    .build());
        } catch (MailException e) {
            stringRedisTemplate.delete(key);
            throw new ExternalServiceManagerException(AuthErrorCode.EMAIL_SEND_FAILED);
        }

        return EmailVerificationSendResponseDto.builder()
                .expiresInSeconds(CODE_TTL.toSeconds())
                .build();
    }

    // 이메일 인증 코드 검증
    public EmailVerificationVerifyResponseDto verifyCode(EmailVerificationVerifyRequestDto emailVerificationVerifyRequestDto) {
        EmailVerificationPurpose purpose = EmailVerificationPurpose.defaultIfNull(emailVerificationVerifyRequestDto.getPurpose());
        String email = emailVerificationVerifyRequestDto.getEmail();
        String codeKey = purpose.codeKey(email);
        String saved = stringRedisTemplate.opsForValue().get(codeKey);

        if (saved == null) {
            throw new EmailVerificationManagerException(AuthErrorCode.EMAIL_VERIFICATION_CODE_EXPIRED);
        }

        if (!saved.equals(emailVerificationVerifyRequestDto.getCode())) {
            throw new EmailVerificationManagerException(AuthErrorCode.EMAIL_VERIFICATION_CODE_MISMATCH);
        }

        stringRedisTemplate.delete(codeKey);

        String verificationId = UUID.randomUUID().toString();
        String verifiedKey = purpose.verifiedKey(verificationId);
        stringRedisTemplate.opsForValue().set(verifiedKey, email, VERIFIED_TTL);

        return EmailVerificationVerifyResponseDto.builder()
                .verificationId(verificationId)
                .build();
    }

    // 이메일 제목 생성
    private static String mailSubject(EmailVerificationPurpose purpose) {
        return switch (purpose) {
            case SIGNUP -> "[MeetingRoom] 회원가입 이메일 인증번호";
            case FIND_LOGIN_ID -> "[MeetingRoom] 아이디 찾기 인증번호";
            case FIND_PASSWORD -> "[MeetingRoom] 비밀번호 찾기 인증번호";
        };
    }

    // 이메일 본문 생성
    private static String mailBody(EmailVerificationPurpose purpose, String code) {
        String label = switch (purpose) {
            case SIGNUP -> "회원가입";
            case FIND_LOGIN_ID -> "아이디 찾기";
            case FIND_PASSWORD -> "비밀번호 찾기";
        };
        return label + " 인증번호는 [" + code + "] 입니다.\n"
                + "유효시간은 " + CODE_TTL.toMinutes() + "분 입니다.";
    }

    // 인증 코드 생성
    private static String generate6Digits() {
        int n = RANDOM.nextInt(1_000_000);
        return String.format("%06d", n);
    }
}
