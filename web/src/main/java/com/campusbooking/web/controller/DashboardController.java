package com.campusbooking.web.controller;

import com.campusbooking.web.actor.Person;
import com.campusbooking.web.model.Booking;
import com.campusbooking.web.model.Status;
import com.campusbooking.web.repository.BookingRepository;
import com.campusbooking.web.repository.PersonRepository;
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

    @Autowired
    private PersonRepository personRepository; // Used to find the currently logged-in approver

    /**
     * Displays the dashboard for a logged-in student, showing only their requests.
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
     * It fetches only booking requests assigned to this specific advisor.
     */
    @GetMapping("/staff-advisor/dashboard")
    public String staffAdvisorDashboard(Model model, Authentication authentication) {
        // Find the Person object for the currently logged-in user
        Person currentUser = personRepository.findByName(authentication.getName()).orElse(null);
        if (currentUser != null) {
            // Fetch only the bookings assigned to this advisor with the correct pending status
            List<Booking> pendingBookings = bookingRepository.findByAssignedStaffAdvisorAndStatus(currentUser, Status.PENDING_STAFF_APPROVAL);
            model.addAttribute("bookings", pendingBookings);
        }
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", "Staff Advisor");
        return "staff-advisor-dashboard";
    }

    /**
     * Displays the dashboard for the HOD.
     * It fetches only booking requests assigned to this specific HOD.
     */
    @GetMapping("/hod/dashboard")
    public String hodDashboard(Model model, Authentication authentication) {
        Person currentUser = personRepository.findByName(authentication.getName()).orElse(null);
        if (currentUser != null) {
            List<Booking> pendingBookings = bookingRepository.findByAssignedHodAndStatus(currentUser, Status.PENDING_HOD_APPROVAL);
            model.addAttribute("bookings", pendingBookings);
        }
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", "HOD");
        return "hod-dashboard";
    }

    /**
     * Displays the dashboard for the Dean.
     * The Dean sees all requests pending their approval.
     */
    @GetMapping("/dean/dashboard")
    public String deanDashboard(Model model, Authentication authentication) {
        List<Booking> pendingBookings = bookingRepository.findByStatus(Status.PENDING_DEAN_APPROVAL);
        model.addAttribute("bookings", pendingBookings);
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", "Dean");
        return "dean-dashboard";
    }

    /**
     * Displays the dashboard for the Principal.
     * The Principal sees all requests pending their approval.
     */
    @GetMapping("/principal/dashboard")
    public String principalDashboard(Model model, Authentication authentication) {
        List<Booking> pendingBookings = bookingRepository.findByStatus(Status.PENDING_PRINCIPAL_APPROVAL);
        model.addAttribute("bookings", pendingBookings);
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", "Principal");
        return "principal-dashboard";
    }

    /**
     * Displays the page shown to approvers when they have no pending requests.
     */
    @GetMapping("/no-pending-requests")
    public String noPendingRequests() {
        return "no-pending-requests";
    }
}

