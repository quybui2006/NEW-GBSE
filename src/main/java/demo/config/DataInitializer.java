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

    @Value("${ADMIN_USERNAME:admin}")
    private String adminUsername;

    // ✅ SỬA: Dùng giá trị rỗng thay vì #{null}
    @Value("${ADMIN_PASSWORD:}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        boolean userExists = userRepository.existsByUsername(adminUsername);

        if (!userExists) {
            User admin = new User();
            admin.setUsername(adminUsername);

            String password;
            if (adminPassword != null && !adminPassword.trim().isEmpty()) {
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
            System.out.println("TAI KHOAN QUAN TRI:");
            System.out.println("   Username: " + adminUsername);
            System.out.println("   Password: " + password);
            System.out.println("   LUU LAI NGAY - CHI HIEN THI 1 LAN!");
            System.out.println("============================================");

        } else if (adminPassword != null && !adminPassword.trim().isEmpty()) {
            // User exists and explicit password provided via env/config — update it
            userRepository.findByUsername(adminUsername).ifPresent(admin -> {
                admin.setPassword(passwordEncoder.encode(adminPassword));
                userRepository.save(admin);
            });
            System.out.println("[DataInitializer] Admin password updated from config (username: " + adminUsername + ")");

        } else {
            System.out.println("[DataInitializer] Admin user '" + adminUsername + "' already exists.");
            System.out.println("[DataInitializer] To reset password: set ADMIN_PASSWORD env var and restart.");
        }
    }
}