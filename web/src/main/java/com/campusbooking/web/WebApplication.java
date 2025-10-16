package com.campusbooking.web;

import com.campusbooking.web.actor.SuperUser;
import com.campusbooking.web.repository.PersonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * The main entry point for the Spring Boot application.
 * The @SpringBootApplication annotation enables auto-configuration and component scanning.
 */
@SpringBootApplication
public class WebApplication {

    /**
     * The main method which is used to run the Spring Boot application.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

    /**
     * This method is a Spring Bean that runs once on application startup.
     * It's used here to initialize "seed data" in the database. Specifically,
     * it checks for the existence of the 'root' super user and creates it if it's missing.
     *
     * @param personRepository The repository for accessing user data.
     * @param passwordEncoder The service for hashing passwords.
     * @return A CommandLineRunner instance that executes the initialization logic.
     */
    @Bean
    CommandLineRunner initSuperUser(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if a user with the name "root" already exists in the database.
            personRepository.findByName("root").ifPresentOrElse(
                user -> {
                    // If the user already exists, print a message to the console and do nothing.
                    System.out.println("Super user 'root' already exists.");
                },
                () -> {
                    // If the 'root' user does not exist, create a new SuperUser object.
                    SuperUser rootUser = new SuperUser();
                    rootUser.setName("root");
                    // Securely hash the password before saving it.
                    rootUser.setPassword(passwordEncoder.encode("superuser*123"));
                    rootUser.setRole("ROLE_SUPER_USER");
                    // Save the new user to the database.
                    personRepository.save(rootUser);
                    System.out.println("Super user 'root' created successfully.");
                }
            );
        };
    }
}

