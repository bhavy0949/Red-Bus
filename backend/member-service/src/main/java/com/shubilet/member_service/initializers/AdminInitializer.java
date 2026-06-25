package com.shubilet.member_service.initializers;

import com.shubilet.member_service.common.enums.Role;
import com.shubilet.member_service.models.AdminInfo;
import com.shubilet.member_service.models.User;
import com.shubilet.member_service.repositories.AdminInfoRepository;
import com.shubilet.member_service.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

    private final UserRepository userRepository;
    private final AdminInfoRepository adminInfoRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository,
                            AdminInfoRepository adminInfoRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.adminInfoRepository = adminInfoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("AdminInitializer run method called.");

        if (!userRepository.existsByEmail("shubilet@example.com")) {
            logger.info("No system admin found. Creating default administrator.");

            User user = new User(
                    "shubilet@example.com",
                    passwordEncoder.encode("SecurePassword123!"),
                    Role.ADMIN
            );
            userRepository.save(user);

            AdminInfo adminInfo = new AdminInfo(user, "System", "Administrator");
            adminInfoRepository.save(adminInfo);

            logger.info("System administrator created successfully.");
        }
    }
}
