package com.rutika.inventory.config;

import com.rutika.inventory.entity.User;
import com.rutika.inventory.enums.UserStatus;
import com.rutika.inventory.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void seedAdminUser() {
        if (!userRepository.existsByEmail("admin@gmail.com")) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@gmail.com");
            admin.setPhone("+1234567890");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setStatus(UserStatus.ACTIVE);
            userRepository.save(admin);
        }
    }
}
