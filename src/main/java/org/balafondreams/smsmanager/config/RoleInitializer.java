package org.balafondreams.smsmanager.config;

import lombok.RequiredArgsConstructor;
import org.balafondreams.smsmanager.domain.entities.user.ERole;
import org.balafondreams.smsmanager.domain.entities.user.Role;
import org.balafondreams.smsmanager.repository.RoleRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements ApplicationRunner {
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // Initialiser les rôles par défaut si ils n'existent pas
        for (ERole roleEnum : ERole.values()) {
            if (!roleRepository.existsByName(roleEnum)) {
                Role role = new Role();
                role.setName(roleEnum);
                roleRepository.save(role);
            }
        }
    }
}