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
// Unused import has been removed.

@Controller
public class RegistrationController {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Use a helper class (DTO) to hold the form data
        model.addAttribute("registrationForm", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegistrationForm registrationForm) {
        
        String role = registrationForm.getRole();
        String encodedPassword = passwordEncoder.encode(registrationForm.getPassword());
        String name = registrationForm.getName();

        // FIX: Converted the 'switch' to a more modern switch expression.
        Person newUser = switch (role) {
            case "ROLE_STUDENT" -> new User(name, encodedPassword);
            case "ROLE_STAFF_ADVISOR" -> new StaffAdvisor();
            case "ROLE_HOD" -> new HOD();
            case "ROLE_DEAN" -> new Dean();
            case "ROLE_PRINCIPAL" -> new Principal();
            default -> null; // Handle case where role is unknown
        };

        if (newUser == null) {
            // If the role is invalid, redirect with an error
            return "redirect:/register?error";
        }

        // For approver roles, the no-arg constructor is used, so we must set the properties.
        // The User constructor already handles this, but this block ensures all approvers are set up correctly.
        if (!"ROLE_STUDENT".equals(role)) {
            newUser.setName(name);
            newUser.setPassword(encodedPassword);
            newUser.setRole(role);
        }

        personRepository.save(newUser);
        return "redirect:/login?success";
    }

    /**
     * A static inner class (DTO) to safely transfer data from the registration form.
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

