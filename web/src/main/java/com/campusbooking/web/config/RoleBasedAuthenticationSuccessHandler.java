package com.campusbooking.web.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import java.io.IOException;

public class RoleBasedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Redirect user based on their specific role after successful login
        for (GrantedAuthority auth : authentication.getAuthorities()) {
            switch (auth.getAuthority()) {
                case "ROLE_STUDENT":
                    response.sendRedirect("/student/dashboard");
                    return;
                case "ROLE_STAFF_ADVISOR":
                    response.sendRedirect("/staff-advisor/dashboard");
                    return;
                case "ROLE_HOD":
                    response.sendRedirect("/hod/dashboard");
                    return;
                case "ROLE_DEAN":
                    response.sendRedirect("/dean/dashboard");
                    return;
                case "ROLE_PRINCIPAL":
                    response.sendRedirect("/principal/dashboard");
                    return;
                default:
                    response.sendRedirect("/"); // Fallback to home
                    return;
            }
        }
    }
}

