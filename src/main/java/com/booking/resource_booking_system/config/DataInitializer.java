package com.booking.resource_booking_system.config;

import com.booking.resource_booking_system.entity.User;
import com.booking.resource_booking_system.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (userRepository.findByUsername("admin").isEmpty()) {

            User admin = new User();

            admin.setUsername("admin");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(
                    passwordEncoder.encode("admin123")
            );
            admin.setRole("ADMIN");

            userRepository.save(admin);
        }

        if (userRepository.findByUsername("user").isEmpty()) {

            User user = new User();

            user.setUsername("user");
            user.setEmail("user@gmail.com");
            user.setPassword(
                    passwordEncoder.encode("user123")
            );
            user.setRole("USER");

            userRepository.save(user);
        }
    }
}