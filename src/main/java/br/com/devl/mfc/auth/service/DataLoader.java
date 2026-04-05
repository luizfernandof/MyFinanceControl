package br.com.devl.mfc.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.devl.mfc.auth.entity.User;
import br.com.devl.mfc.auth.repository.UserRepository;

@Component
@Profile("dev")
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String seedEmail;
    private final String seedPassword;

    public DataLoader(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @org.springframework.beans.factory.annotation.Value("${mfc.seed-user.email:admin@email.com}") String seedEmail,
            @org.springframework.beans.factory.annotation.Value("${mfc.seed-user.password:admin}") String seedPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedEmail = seedEmail;
        this.seedPassword = seedPassword;
    }

    @Override
    public void run(String... args) {

        if (userRepository.findByEmail(seedEmail).isEmpty()) {

            User user = new User();
            user.setEmail(seedEmail);
            user.setPassword(passwordEncoder.encode(seedPassword));

            userRepository.save(user);

            log.info("Seed user created: {} (profile: dev)", seedEmail);
        }
    }
}
