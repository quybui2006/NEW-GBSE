package demo.config;

import java.security.SecureRandom;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import demo.model.User;
import demo.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           @Value("${ADMIN_USERNAME:admin}") String adminUsername,
                           @Value("${ADMIN_PASSWORD:}") String adminPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

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

        } else {
            // Admin exists. Do NOT auto-update the password on every restart —
            // anyone who can set env vars on the hosting platform could silently
            // hijack the admin account. To change the password, update the BCrypt
            // hash directly in the database:
            //   UPDATE users SET password = '<bcrypt_hash>' WHERE username = 'admin';
            log.info("[DataInitializer] Admin '{}' already exists — no changes made.", adminUsername);
        }
    }
}