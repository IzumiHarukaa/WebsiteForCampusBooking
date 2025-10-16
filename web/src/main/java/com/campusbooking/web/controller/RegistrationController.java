package com.campusbooking.web.controller;

import com.campusbooking.web.actor.Dean;
import com.campusbooking.web.actor.HOD;
import com.campusbooking.web.actor.Person;
import com.campusbooking.web.actor.Principal;
import com.campusbooking.web.actor.StaffAdvisor;
import com.campusbooking.web.actor.User;
import com.campusbooking.web.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * This controller manages the new user registration process for public users.
 */
@Controller
public class RegistrationController {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Displays the user registration form.
     * @param model The Spring model to which attributes are added.
     * @return The name of the HTML template to render ("register.html").
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // We use a helper class (DTO) to safely bind the form data.
        model.addAttribute("registrationForm", new RegistrationForm());
        return "register";
    }

    /**
     * Handles the submission of the registration form.
     * @param registrationForm An object containing the submitted form data.
     * @return A redirect instruction to the login page.
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegistrationForm registrationForm) {
        
        String role = registrationForm.getRole();
        // Always encode the password before saving it to the database for security.
        String encodedPassword = passwordEncoder.encode(registrationForm.getPassword());
        String name = registrationForm.getName();

        // Use a switch expression to create the correct type of Person object based on the role.
        Person newUser = switch (role) {
            case "ROLE_STUDENT" -> new User(name, encodedPassword);
            case "ROLE_STAFF_ADVISOR" -> new StaffAdvisor();
            case "ROLE_HOD" -> new HOD();
            case "ROLE_DEAN" -> new Dean();
            case "ROLE_PRINCIPAL" -> new Principal();
            default -> null; // Handle case where role is unknown or invalid.
        };

        if (newUser == null) {
            // If the role is invalid, redirect back to the registration page with an error.
            return "redirect:/register?error";
        }

        // For roles that use a no-argument constructor (all approvers), we must set their properties manually.
        if (!"ROLE_STUDENT".equals(role)) {
            newUser.setName(name);
            newUser.setPassword(encodedPassword);
            newUser.setRole(role);
        }

        // Save the newly created user object to the database.
        personRepository.save(newUser);
        
        // Redirect to the login page with a success message.
        return "redirect:/login?success";
    }

    /**
     * A static inner class (DTO) to safely transfer data from the registration form.
     * This prevents exposing the actual database entities to the web layer directly.
     */
    public static class RegistrationForm {
        private String name;
        private String password;
        private String role;
        
        // Standard Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}

