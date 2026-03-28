package com.campusbooking.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    /**
     * Redirects the root URL to the login page.
     * This prevents the 404 Whitelabel Error when visiting http://localhost:8080/
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    /**
     * This method handles GET requests for the /login URL and displays the login page.
     * @return The name of the HTML template to render ("login.html").
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

}
