package com.campusbooking.web.controller;

/**
 * Data Transfer Object (DTO) for the user registration form.
 * Extracted to a top-level class to ensure reliable Thymeleaf th:field binding
 * and to avoid "Invalid property" errors that can occur with static inner classes.
 */
public class RegistrationForm {

    private String name;
    private String password;
    private String role;
    private String secretCode;

    // Default no-arg constructor required by Thymeleaf / Spring MVC
    public RegistrationForm() {}

    // Standard Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getSecretCode() { return secretCode; }
    public void setSecretCode(String secretCode) { this.secretCode = secretCode; }
}
