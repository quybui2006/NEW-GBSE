package demo.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import demo.model.YeuCau;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final String RESEND_URL = "https://api.resend.com/emails";
    private static final RestTemplate restTemplate = new RestTemplate();

    @Value("${resend.api-key:}")
    private String apiKey;

    @Value("${email.from:}")
    private String emailFrom;

    @Value("${email.nhan:}")
    private String emailNhan;

    @Async
    public void guiThongBao(YeuCau yeuCau) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("[EmailService] RESEND_API_KEY not configured — skipping notification email.");
            return;
        }
        if (emailNhan == null || emailNhan.isBlank()) {
            log.warn("[EmailService] MAIL_RECEIVER not configured — skipping notification email.");
            return;
        }
        if (emailFrom == null || emailFrom.isBlank()) {
            log.warn("[EmailService] MAIL_FROM not configured — skipping notification email.");
            return;
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = Map.of(
                "from",    emailFrom,
                "to",      List.of(emailNhan),
                "subject", "New quote request from: " + yeuCau.getHoTen(),
                "text",
                    "Name:    " + yeuCau.getHoTen()         + "\n" +
                    "Phone:   " + yeuCau.getSoDienThoai()   + "\n" +
                    "Email:   " + yeuCau.getEmail()         + "\n" +
                    "Product: " + yeuCau.getSanPhamQuanTam()+ "\n" +
                    "Message: " + yeuCau.getNoiDung()
            );

            restTemplate.postForObject(RESEND_URL,
                    new HttpEntity<>(body, headers), String.class);

            log.info("[EmailService] Notification sent for request from: {}", yeuCau.getHoTen());
        } catch (Exception e) {
            log.error("[EmailService] Failed to send notification email: {}", e.getMessage(), e);
        }
    }
}
