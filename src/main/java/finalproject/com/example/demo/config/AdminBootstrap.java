package finalproject.com.example.demo.config;

import finalproject.com.example.demo.entity.Role;
import finalproject.com.example.demo.entity.User;
import finalproject.com.example.demo.repository.RoleRepository;
import finalproject.com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);
    private static final String ADMIN_EMAIL = "julievmf@gmail.com";
    private static final String DEMO_PASSWORD = "DemoPass123!";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminBootstrap(UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        Role adminRole = ensureRoleExists("ROLE_ADMIN");
        Role userRole = ensureRoleExists("ROLE_USER");

        User admin = userRepository.findByEmail(ADMIN_EMAIL);
        if (admin == null) {
            User seeded = new User();
            seeded.setEmail(ADMIN_EMAIL);
            seeded.setFullName("Julie Folkerts");
            seeded.setBlocked(false);
            seeded.setCreatedAt(LocalDateTime.now());
            seeded.setPassword(passwordEncoder.encode(DEMO_PASSWORD));
            seeded.setRoles(List.of(adminRole, userRole));
            userRepository.save(seeded);
            log.info("Seeded admin user {} with default password", ADMIN_EMAIL);
            return;
        }

        boolean updated = false;
        if (!passwordEncoder.matches(DEMO_PASSWORD, admin.getPassword())) {
            admin.setPassword(passwordEncoder.encode(DEMO_PASSWORD));
            updated = true;
        }

        if (admin.isBlocked()) {
            admin.setBlocked(false);
            updated = true;
        }

        List<Role> roles = admin.getRoles();
        if (roles == null) {
            roles = new ArrayList<>();
        }
        if (!hasRole(roles, adminRole.getRole())) {
            roles.add(adminRole);
            updated = true;
        }
        if (!hasRole(roles, userRole.getRole())) {
            roles.add(userRole);
            updated = true;
        }
        admin.setRoles(roles);

        if (admin.getCreatedAt() == null) {
            admin.setCreatedAt(LocalDateTime.now());
            updated = true;
        }

        if (updated) {
            userRepository.save(admin);
            log.info("Updated admin user {} to match demo credentials and roles", ADMIN_EMAIL);
        }
    }

    private Role ensureRoleExists(String roleName) {
        Role role = roleRepository.findByRole(roleName);
        if (role != null) {
            return role;
        }
        Role created = new Role();
        created.setRole(roleName);
        return roleRepository.save(created);
    }

    private boolean hasRole(List<Role> roles, String roleName) {
        return roles.stream().anyMatch(role -> roleName.equals(role.getRole()));
    }
}