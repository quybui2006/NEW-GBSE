package demo.config;

import java.security.SecureRandom;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import demo.model.User;
import demo.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

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

            if (adminPassword != null && !adminPassword.trim().isEmpty()) {
                log.info("[DataInitializer] Admin '{}' created using ADMIN_PASSWORD env var.", adminUsername);
            } else {
                // Password is NOT logged — logging plaintext credentials is a security risk.
                // Set ADMIN_PASSWORD as an environment variable before first deployment so the
                // password is known. If you missed this, reset the hash directly in the database.
                log.warn("[DataInitializer] Admin '{}' created with a randomly-generated password. " +
                         "Set the ADMIN_PASSWORD environment variable and restart to configure a " +
                         "known password, or reset the hash directly in the database.",
                         adminUsername);
            }

        } else if (adminPassword != null && !adminPassword.trim().isEmpty()) {
            userRepository.findByUsername(adminUsername).ifPresent(admin -> {
                admin.setPassword(passwordEncoder.encode(adminPassword));
                userRepository.save(admin);
            });
            log.info("[DataInitializer] Admin password updated from config (username: {})", adminUsername);

        } else {
            log.info("[DataInitializer] Admin '{}' already exists. Set ADMIN_PASSWORD env var to reset.", adminUsername);
        }
    }
}