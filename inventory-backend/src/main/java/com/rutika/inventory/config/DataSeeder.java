package com.rutika.inventory.config;

import com.rutika.inventory.entity.Role;
import com.rutika.inventory.entity.User;
import com.rutika.inventory.enums.UserStatus;
import com.rutika.inventory.repository.RoleRepository;
import com.rutika.inventory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seedData() {
        seedRoles();
        seedAdminUser();
    }

    private void seedRoles() {
        if (!roleRepository.existsByRoleName("SUPER_ADMIN")) {
            Role superAdmin = new Role();
            superAdmin.setRoleName("SUPER_ADMIN");
            roleRepository.save(superAdmin);
        }
        if (!roleRepository.existsByRoleName("ADMIN")) {
            Role admin = new Role();
            admin.setRoleName("ADMIN");
            roleRepository.save(admin);
        }
    }

    private void seedAdminUser() {
        Role superAdminRole = roleRepository.findByRoleName("SUPER_ADMIN")
                .orElseThrow(() -> new RuntimeException("SUPER_ADMIN role not found"));

        User admin = userRepository.findByEmail("admin@gmail.com").orElse(null);
        if (admin == null) {
            admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@gmail.com");
            admin.setPhone("+1234567890");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setStatus(UserStatus.ACTIVE);
        }
        admin.setRole(superAdminRole);
        userRepository.save(admin);
    }
}
