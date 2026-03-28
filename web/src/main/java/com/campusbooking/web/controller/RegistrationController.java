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

@Controller
public class RegistrationController {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Use the top-level RegistrationForm DTO to hold the form data
        model.addAttribute("registrationForm", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegistrationForm registrationForm) {

        String encodedPassword = passwordEncoder.encode(registrationForm.getPassword());
        String name = registrationForm.getName();
        String role = registrationForm.getRole();
        String secretCode = registrationForm.getSecretCode();

        Person newUser;
        String trimmedCode = secretCode != null ? secretCode.trim() : "";

        if ("ROLE_STAFF_ADVISOR".equals(role) && "staff".equalsIgnoreCase(trimmedCode)) {
            newUser = new StaffAdvisor();
            newUser.setRole(role);
        } else if ("ROLE_HOD".equals(role) && "hod".equalsIgnoreCase(trimmedCode)) {
            newUser = new HOD();
            newUser.setRole(role);
        } else if ("ROLE_DEAN".equals(role) && "dean".equalsIgnoreCase(trimmedCode)) {
            newUser = new Dean();
            newUser.setRole(role);
        } else if ("ROLE_PRINCIPAL".equals(role) && "principal".equalsIgnoreCase(trimmedCode)) {
            newUser = new Principal();
            newUser.setRole(role);
        } else if ("ROLE_STUDENT".equals(role) || role == null || role.isEmpty()) {
            newUser = new User(name, encodedPassword);
            newUser.setRole("ROLE_STUDENT");
        } else {
            // They selected a staff role but provided the wrong secret code
            return "redirect:/register?error=Invalid+secret+code+for+the+selected+role";
        }

        newUser.setName(name);
        newUser.setPassword(encodedPassword);

        personRepository.save(newUser);
        return "redirect:/login?success";
    }
}


