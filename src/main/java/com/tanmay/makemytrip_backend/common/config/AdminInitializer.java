package com.tanmay.makemytrip_backend.common.config;

import com.tanmay.makemytrip_backend.user.entity.User;
import com.tanmay.makemytrip_backend.user.entity.UserRole;
import com.tanmay.makemytrip_backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final String adminEmail;
    private final String adminPassword;

    public AdminInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${admin.email}") String adminEmail,
            @Value("${admin.password}") String adminPassword) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {

        // Admin already exists → nothing to do.
        if (userRepository.existsByRole(UserRole.ADMIN)) {
            return;
        }

        // Avoid unique-email conflict.
        if (userRepository.existsByEmail(adminEmail)) {
            throw new IllegalStateException(
                    "Cannot create initial admin: email already exists: "
                            + adminEmail
            );
        }

        User admin = new User(
                "System",
                "Admin",
                adminEmail,
                passwordEncoder.encode(adminPassword),
                "9999999999"
        );

        admin.setRole(UserRole.ADMIN);
        admin.setActive(true);

        userRepository.save(admin);
    }
}