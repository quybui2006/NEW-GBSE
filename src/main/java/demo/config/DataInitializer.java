package demo.config;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import demo.model.User;
import demo.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ Ẩn cả username
    @Value("${ADMIN_USERNAME:admin}")
    private String adminUsername;

    // ✅ Ẩn cả password
    @Value("${ADMIN_PASSWORD:#{null}}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername(adminUsername)) {
            User admin = new User();
            admin.setUsername(adminUsername);

            String password;
            if (adminPassword != null && !adminPassword.isEmpty()) {
                password = adminPassword;
            } else {
                byte[] randomBytes = new byte[12];
                new SecureRandom().nextBytes(randomBytes);
                password = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
            }

            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole("ADMIN");
            admin.setEnabled(true);
            userRepository.save(admin);

            System.out.println("============================================");
            System.out.println("🔐 TÀI KHOẢN QUẢN TRỊ:");
            System.out.println("   Username: " + adminUsername);
            System.out.println("   Password: " + password);
            System.out.println("   ⚠️ LƯU LẠI NGAY - CHỈ HIỂN THỊ 1 LẦN!");
            System.out.println("============================================");
        }
    }
}