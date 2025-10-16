package com.campusbooking.web.config;

import com.campusbooking.web.model.Status;
import com.campusbooking.web.repository.BookingRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * A custom success handler that directs users to different pages based on their role after a successful login.
 * This class is a Spring Component, so it can be automatically discovered and used by the security configuration.
 */
@Component
public class RoleBasedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    // Inject the BookingRepository to check the database for pending requests.
    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Set a default URL in case no specific role logic matches.
        String redirectUrl = "/"; 

        // Get the first role (authority) of the logged-in user.
        GrantedAuthority authority = authentication.getAuthorities().stream().findFirst().orElse(null);
        if (authority == null) {
            response.sendRedirect(redirectUrl);
            return;
        }

        // Use a switch statement to determine the correct redirect URL.
        switch (authority.getAuthority()) {
            case "ROLE_STUDENT":
                redirectUrl = "/student/dashboard";
                break;
            case "ROLE_STAFF_ADVISOR":
                // Check if there are any bookings with the status PENDING_STAFF_APPROVAL.
                // If the list is empty, redirect to the "no requests" page; otherwise, go to the dashboard.
                redirectUrl = bookingRepository.findByStatus(Status.PENDING_STAFF_APPROVAL).isEmpty()
                    ? "/no-pending-requests" : "/staff-advisor/dashboard";
                break;
            case "ROLE_HOD":
                redirectUrl = bookingRepository.findByStatus(Status.PENDING_HOD_APPROVAL).isEmpty()
                    ? "/no-pending-requests" : "/hod/dashboard";
                break;
            case "ROLE_DEAN":
                redirectUrl = bookingRepository.findByStatus(Status.PENDING_DEAN_APPROVAL).isEmpty()
                    ? "/no-pending-requests" : "/dean/dashboard";
                break;
            case "ROLE_PRINCIPAL":
                 redirectUrl = bookingRepository.findByStatus(Status.PENDING_PRINCIPAL_APPROVAL).isEmpty()
                    ? "/no-pending-requests" : "/principal/dashboard";
                break;
        }
        
        // Perform the redirect to the determined URL.
        response.sendRedirect(redirectUrl);
    }
}

