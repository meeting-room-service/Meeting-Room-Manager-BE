package com.meetingroom.manager.infra.mail.service;

import com.meetingroom.manager.global.error.code.MailErrorCode;
import com.meetingroom.manager.infra.mail.exception.MailException;
import com.meetingroom.manager.infra.mail.dto.MailTxtSendDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    // 이메일 발송
    public void sendSimpleMail(MailTxtSendDto mailTxtSendDto) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(mailTxtSendDto.getEmailAddress());
        mailMessage.setSubject(mailTxtSendDto.getSubject());
        mailMessage.setText(mailTxtSendDto.getContent());

        try {
            mailSender.send(mailMessage);
            log.info("[+] 이메일이 성공적으로 전송되었습니다 to: {}", mailTxtSendDto.getEmailAddress());
        } catch (Exception e) {
            log.error("[-] 이메일 전송 중 오류가 발생했습니다 to: {}, error: {}", mailTxtSendDto.getEmailAddress(), e.getMessage());
            throw new MailException(MailErrorCode.MAIL_SEND_FAILURE);
        }
    }
}
