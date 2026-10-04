package store.pharmaquick.config;

import store.pharmaquick.user.entity.Role;
import store.pharmaquick.user.entity.User;
import store.pharmaquick.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.user-id}")
    private String adminUserId;

    @Value("${admin.name}")
    private String adminName;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.mobile}")
    private String adminMobile;

    @Value("${admin.password}")
    private String adminPassword;

    public AdminInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // Check whether an ADMIN already exists
        boolean adminExists = userRepository.findAll()
                .stream()
                .anyMatch(user -> user.getRole() == Role.ADMIN);

        if (adminExists) {
            System.out.println("ADMIN already exists. Skipping admin creation.");
            return;
        }

        // Create ADMIN
        User admin = new User();

        admin.setUserId(adminUserId);
        admin.setName(adminName);
        admin.setEmail(adminEmail);
        admin.setMobile(adminMobile);

        // Hash admin password before saving
        admin.setPassword(
                passwordEncoder.encode(adminPassword)
        );

        // IMPORTANT: This account is ADMIN
        admin.setRole(Role.ADMIN);

        userRepository.save(admin);

        System.out.println("======================================");
        System.out.println("Initial ADMIN account created.");
        System.out.println("Admin User ID: " + adminUserId);
        System.out.println("Admin Email: " + adminEmail);
        System.out.println("======================================");
    }
}