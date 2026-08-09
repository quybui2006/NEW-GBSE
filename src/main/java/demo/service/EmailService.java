package demo.service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import demo.model.ContactRequest;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final String RESEND_URL = "https://api.resend.com/emails";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final RestTemplate restTemplate;

    @Value("${resend.api-key:}")
    private String apiKey;

    @Value("${email.from:}")
    private String emailFrom;

    @Value("${email.nhan:}")
    private String emailNhan;

    public EmailService(@Qualifier("emailRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    public void sendNotification(ContactRequest request) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("[EmailService] RESEND_API_KEY not configured — skipping notification.");
            return;
        }
        if (emailNhan == null || emailNhan.isBlank()) {
            log.warn("[EmailService] MAIL_RECEIVER not configured — skipping notification.");
            return;
        }
        if (emailFrom == null || emailFrom.isBlank()) {
            log.warn("[EmailService] MAIL_FROM not configured — skipping notification.");
            return;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            String time = request.getCreatedAt() != null
                    ? request.getCreatedAt().format(FMT) : "—";

            Map<String, Object> body = Map.of(
                "from",    emailFrom,
                "to",      List.of(emailNhan),
                "subject", "[OMEAX] New quote request from " + request.getFullName(),
                "html",    buildHtml(request, time)
            );

            restTemplate.postForObject(RESEND_URL, new HttpEntity<>(body, headers), String.class);
            log.info("[EmailService] Notification sent for request id={}", request.getId());

        } catch (RestClientException e) {
            // Request is already persisted in DB — no data is lost.
            // Logged with stack trace for manual follow-up via Railway logs.
            log.error("[EmailService] Failed to send notification (id={}): {}",
                      request.getId(), e.getMessage(), e);
        }
    }

    private String buildHtml(ContactRequest request, String time) {
        String message = request.getMessage() != null
                ? request.getMessage().replace("&", "&amp;").replace("<", "&lt;").replace("\n", "<br>")
                : "—";
        String email = request.getEmail() != null ? request.getEmail() : "—";

        // Colours are literal hex on purpose: mail clients strip <style> blocks and
        // do not support CSS custom properties, so these mirror theme.css by hand.
        return "<div style=\"font-family:Arial,sans-serif;max-width:600px;margin:0 auto\">"
             + "<div style=\"background:#1B6D42;padding:24px 32px;border-radius:8px 8px 0 0\">"
             + "<h2 style=\"color:#fff;margin:0\">New Quote Request</h2>"
             + "<p style=\"color:rgba(255,255,255,.78);margin:4px 0 0\">OMEAX Admin Notification</p>"
             + "</div>"
             + "<div style=\"background:#EAF6EF;padding:32px;border:1px solid #D5E3DA;border-top:none;border-radius:0 0 8px 8px\">"
             + "<table style=\"width:100%;border-collapse:collapse\">"
             + row("Full name", "<strong>" + request.getFullName() + "</strong>")
             + row("Phone",     "<strong>" + request.getPhone() + "</strong>")
             + row("Email",     email)
             + row("Message",   message)
             + row("Time",      time)
             + "</table>"
             + "</div></div>";
    }

    private static String row(String label, String value) {
        return "<tr>"
             + "<td style=\"padding:8px 0;color:#6B7770;width:120px\">" + label + "</td>"
             + "<td style=\"padding:8px 0;color:#17221B\">" + value + "</td>"
             + "</tr>";
    }
}
