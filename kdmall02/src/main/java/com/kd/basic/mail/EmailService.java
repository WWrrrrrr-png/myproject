package com.kd.basic.mail;

import java.util.List;
import java.util.Map;
import java.util.Random;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


// 기존 코드를 최적화
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private static final int AUTH_CODE_LENGTH = 8;
    private static final String UTF_8_ENCODING = "UTF-8";

    private final JavaMailSender mailSender; // G-Mail SMTP 메일 서버 정보
    private final SpringTemplateEngine templateEngine; // Thymeleaf 템플릿 엔진

    // 메일 발송 공통 메서드
    private void sendEmail(EmailDTO dto, String htmlContent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, UTF_8_ENCODING);

            mimeMessageHelper.setTo(dto.getReceiverMail());
            mimeMessageHelper.setFrom(new InternetAddress(dto.getSenderMail(), dto.getSenderName()));
            mimeMessageHelper.setSubject(dto.getSubject());
            mimeMessageHelper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (Exception ex) {
            log.error("메일 발송 실패: {}", ex.getMessage(), ex);
            throw new RuntimeException("메일 발송 중 오류가 발생했습니다.", ex);
        }
    }

    // 단순 메시지를 포함한 메일 발송
    public void sendMail(String type, EmailDTO dto, String message) {
        String htmlContent = generateHtmlContent(message, type);
        sendEmail(dto, htmlContent);
    }

    // 주문 내역을 포함한 메일 발송
    public void sendMail(String type, EmailDTO dto, List<Map<String, Object>> orderInfo, int orderTotalPrice) {
        String htmlContent = generateOrderHtmlContent(orderInfo, orderTotalPrice, type);
        sendEmail(dto, htmlContent);
    }

    // 인증 코드 및 임시 비밀번호 생성
    public String createAuthCode() {
        Random random = new Random();
        StringBuilder authCode = new StringBuilder(AUTH_CODE_LENGTH);

        for (int i = 0; i < AUTH_CODE_LENGTH; i++) {
            int randomType = random.nextInt(3);
            switch (randomType) {
                case 0 -> authCode.append((char) (random.nextInt(26) + 'a')); // 소문자
                case 1 -> authCode.append((char) (random.nextInt(26) + 'A')); // 대문자
                default -> authCode.append(random.nextInt(10)); // 숫자
            }
        }
        return authCode.toString();
    }

    // Thymeleaf를 사용한 HTML 컨텐츠 생성 (일반 메시지)
    private String generateHtmlContent(String message, String templateName) {
        Context context = new Context();
        context.setVariable("message", message);
        return templateEngine.process(templateName, context);
    }

    // Thymeleaf를 사용한 HTML 컨텐츠 생성 (주문 내역)
    private String generateOrderHtmlContent(List<Map<String, Object>> orderInfo, int orderTotalPrice, String templateName) {
        Context context = new Context();
        context.setVariable("order_info", orderInfo);
        context.setVariable("order_total_price", orderTotalPrice);
        return templateEngine.process(templateName, context);
    }
}
