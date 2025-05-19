package com.todayter.domain.user.service;

import com.todayter.domain.user.dao.CertificationNumberDao;
import com.todayter.global.exception.CustomException;
import com.todayter.global.exception.ErrorCode;
import io.lettuce.core.RedisConnectionException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@RequiredArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final CertificationNumberDao certificationNumberDao;

    public void sendNumber(String email) throws NoSuchAlgorithmException, MessagingException {
        String certificationNumber = getCertificationNumber();
        String content = String.format("%s \n 3분이 초과될 시 해당 인증 번호로 이메일 인증이 불가능합니다.", certificationNumber);

        try {
            certificationNumberDao.saveCertificationNumber(email, certificationNumber);
            sendMail(email, content);
        } catch (RedisConnectionException e) {
            throw new CustomException(ErrorCode.REDIS_NOT_CONNECT);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.REDIS_NOT_CONNECT);
        }
    }

    private static String getCertificationNumber() throws NoSuchAlgorithmException {
        String result;

        do {
            int i = SecureRandom.getInstanceStrong().nextInt(999999);
            result = String.valueOf(i);
        } while (result.length() != 6);

        return result;
    }

    private void sendMail(String email, String content) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        helper.setTo(email);
        helper.setSubject("todayter 사이트 인증 요청 메일입니다.");
        helper.setText(content);
        mailSender.send(mimeMessage);
    }

    public void verifyEmail(String certificationNumber, String email) {
        if (isVerify(certificationNumber, email)) {
            throw new CustomException(ErrorCode.VERIFY_NOT_ALLOWED);
        }
        certificationNumberDao.removeCertificationNumber(email);
    }

    private boolean isVerify(String certificationNumber, String email) {

        return !(certificationNumberDao.hasKey(email) &&
                certificationNumberDao.getCertificationNumber(email)
                        .equals(certificationNumber));
    }
}
