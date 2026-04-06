package com.campusbooking.web.controller;

import com.campusbooking.web.actor.Dean;
import com.campusbooking.web.actor.HOD;
import com.campusbooking.web.actor.Person;
import com.campusbooking.web.actor.Principal;
import com.campusbooking.web.actor.StaffAdvisor;
import com.campusbooking.web.actor.User;
import com.campusbooking.web.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    /**
     * Secret codes injected from application.properties — easy to rotate without
     * recompiling.
     */
    @Value("${app.registration.secret.staff-advisor}")
    private String staffAdvisorSecret;

    @Value("${app.registration.secret.hod}")
    private String hodSecret;

    @Value("${app.registration.secret.dean}")
    private String deanSecret;

    @Value("${app.registration.secret.principal}")
    private String principalSecret;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
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

        if ("ROLE_STAFF_ADVISOR".equals(role) && staffAdvisorSecret.equalsIgnoreCase(trimmedCode)) {
            newUser = new StaffAdvisor();
            newUser.setRole(role);
        } else if ("ROLE_HOD".equals(role) && hodSecret.equalsIgnoreCase(trimmedCode)) {
            newUser = new HOD();
            newUser.setRole(role);
        } else if ("ROLE_DEAN".equals(role) && deanSecret.equalsIgnoreCase(trimmedCode)) {
            newUser = new Dean();
            newUser.setRole(role);
        } else if ("ROLE_PRINCIPAL".equals(role) && principalSecret.equalsIgnoreCase(trimmedCode)) {
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

        // Bug fix: Prevent duplicate usernames — check before saving
        if (personRepository.findByName(name).isPresent()) {
            return "redirect:/register?error=Username+already+taken.+Please+choose+a+different+name.";
        }

        personRepository.save(newUser);
        return "redirect:/login?success";
    }
}
