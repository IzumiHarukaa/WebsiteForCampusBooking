package com.campusbooking.web.controller;

import com.campusbooking.web.actor.Person;
import com.campusbooking.web.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SuperUserController {

    @Autowired
    private PersonRepository personRepository;

    /**
     * Displays the Super User dashboard with a list of all users.
     */
    @GetMapping("/super-user/dashboard")
    public String showSuperUserDashboard(Model model, Authentication authentication) {
        String currentUsername = authentication.getName();
        // Fetch all users from the database, excluding the currently logged-in Super User
        List<Person> allUsers = personRepository.findAll().stream()
            .filter(user -> !user.getName().equals(currentUsername))
            .collect(Collectors.toList());
        
        model.addAttribute("users", allUsers);
        model.addAttribute("username", currentUsername);
        return "super-user-dashboard";
    }

    /**
     * Handles the request to remove a user.
     */
    @PostMapping("/super-user/remove")
    public String removeUser(@RequestParam Long userId) {
        // The cascading delete we set up earlier will also remove the user's bookings
        personRepository.deleteById(userId);
        return "redirect:/super-user/dashboard";
    }
}
