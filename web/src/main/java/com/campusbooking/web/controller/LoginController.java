package com.campusbooking.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    /**
     * This method handles GET requests for the /login URL and displays the login page.
     * This is the crucial missing piece that will resolve the redirect loop.
     * @return The name of the HTML template to render ("login.html").
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

}
