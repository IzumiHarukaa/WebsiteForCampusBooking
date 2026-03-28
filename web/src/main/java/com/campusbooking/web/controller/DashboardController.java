package com.campusbooking.web.controller;

import com.campusbooking.web.model.Booking;
import com.campusbooking.web.model.Status;
import com.campusbooking.web.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;
import java.util.List;

/**
 * This controller handles displaying the correct dashboard for each user role after they log in.
 */
@Controller
public class DashboardController {

    @Autowired
    private BookingRepository bookingRepository;

    /**
     * Displays the dashboard for a logged-in student.
     * It fetches only the booking requests created by that specific student.
     */
    @GetMapping("/student/dashboard")
    public String studentDashboard(Model model, Principal principal) {
        List<Booking> myBookings = bookingRepository.findByUserName(principal.getName());
        model.addAttribute("bookings", myBookings);
        model.addAttribute("username", principal.getName());
        return "student-dashboard";
    }

    /**
     * Displays the dashboard for the Staff Advisor.
     * It fetches only booking requests with the status PENDING_STAFF_APPROVAL.
     */
    @GetMapping("/staff-advisor/dashboard")
    public String staffAdvisorDashboard(Model model, Authentication authentication) {
        prepareApproverModel(model, authentication, Status.PENDING_STAFF_APPROVAL, "Staff Advisor");
        return "staff-advisor-dashboard";
    }

    /**
     * Displays the dashboard for the HOD.
     * It fetches only booking requests with the status PENDING_HOD_APPROVAL.
     */
    @GetMapping("/hod/dashboard")
    public String hodDashboard(Model model, Authentication authentication) {
        prepareApproverModel(model, authentication, Status.PENDING_HOD_APPROVAL, "HOD");
        return "hod-dashboard";
    }

    /**
     * Displays the dashboard for the Dean.
     * It fetches only booking requests with the status PENDING_DEAN_APPROVAL.
     */
    @GetMapping("/dean/dashboard")
    public String deanDashboard(Model model, Authentication authentication) {
        prepareApproverModel(model, authentication, Status.PENDING_DEAN_APPROVAL, "Dean");
        return "dean-dashboard";
    }

    /**
     * Displays the dashboard for the Principal.
     * It fetches only booking requests with the status PENDING_PRINCIPAL_APPROVAL.
     */
    @GetMapping("/principal/dashboard")
    public String principalDashboard(Model model, Authentication authentication) {
        prepareApproverModel(model, authentication, Status.PENDING_PRINCIPAL_APPROVAL, "Principal");
        return "principal-dashboard";
    }

    /**
     * Displays the page shown to approvers when they have no pending requests.
     */
    @GetMapping("/no-pending-requests")
    public String noPendingRequests() {
        return "no-pending-requests";
    }
    
    /**
     * A private helper method to reduce code duplication. It prepares the data needed
     * for all approver dashboards.
     */
    private void prepareApproverModel(Model model, Authentication authentication, Status requiredStatus, String roleName) {
        List<Booking> pendingBookings = bookingRepository.findByStatus(requiredStatus);
        model.addAttribute("bookings", pendingBookings);
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", roleName);
    }
}

