package back.app.utils;

import back.app.data.model.user.ERole;
import back.app.data.model.user.UserModel;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import back.app.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Hidden
public class MyInitialization implements CommandLineRunner {

    @Value("${back.admin.email}")
    private String adminEmail;

    @Value("${back.admin.password}")
    private String adminPassword;

    @Autowired
    private UserService userService;

    @Autowired
    private InitializeSettings initializeSettings;

    @Override
    public void run(String... args) throws Exception {
        log.info("Checking if admin user exists");
        if (!userService.existsByEmail(adminEmail)) {
            log.info("Creating admin user with email: " + adminEmail);
            UserModel user = userService.createUser("Goujon", "Quentin", adminEmail, adminPassword, "00000000000");
            userService.updateRole(user.getId(), ERole.ROLE_SUPERVISEUR);
        }

        initializeSettings.init();
    }
}

