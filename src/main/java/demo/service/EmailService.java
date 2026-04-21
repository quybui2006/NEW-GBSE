package demo.service;

import demo.model.YeuCau;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${email.nhan}")
    private String emailNhan;

    public void guiThongBao(YeuCau yeuCau) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailNhan);
        message.setSubject("Yêu cầu báo giá mới từ: " + yeuCau.getHoTen());
        message.setText(
            "Khách hàng: " + yeuCau.getHoTen() + "\n" +
            "SĐT: " + yeuCau.getSoDienThoai() + "\n" +
            "Email: " + yeuCau.getEmail() + "\n" +
            "Sản phẩm quan tâm: " + yeuCau.getSanPhamQuanTam() + "\n" +
            "Nội dung: " + yeuCau.getNoiDung()
        );
        mailSender.send(message);
    }
}