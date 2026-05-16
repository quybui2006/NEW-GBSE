package demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import demo.model.YeuCau;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${email.nhan}")
    private String emailNhan;

    @Value("${spring.mail.username:}")
    private String emailTu;

    @Async
    public void guiThongBao(YeuCau yeuCau) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            if (emailTu != null && !emailTu.isBlank()) {
                message.setFrom(emailTu);
            }
            message.setTo(emailNhan);
            message.setSubject("New quote request from: " + yeuCau.getHoTen());
            message.setText(
                "Name:    " + yeuCau.getHoTen() + "\n" +
                "Phone:   " + yeuCau.getSoDienThoai() + "\n" +
                "Email:   " + yeuCau.getEmail() + "\n" +
                "Product: " + yeuCau.getSanPhamQuanTam() + "\n" +
                "Message: " + yeuCau.getNoiDung()
            );
            mailSender.send(message);
        } catch (Exception e) {
            log.error("[EmailService] Failed to send notification email: {}", e.getMessage(), e);
        }
    }
}