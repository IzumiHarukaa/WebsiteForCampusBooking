package com.campusbooking.web.controller;

import com.campusbooking.web.actor.Person;
import com.campusbooking.web.actor.User;
import com.campusbooking.web.model.Booking;
import com.campusbooking.web.model.Facility;
import com.campusbooking.web.repository.PersonRepository;
import com.campusbooking.web.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import java.util.List;
import com.campusbooking.web.model.ApprovedBookingHistory;
import com.campusbooking.web.repository.ApprovedBookingHistoryRepository;

@Controller
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ApprovedBookingHistoryRepository historyRepository;

    /**
     * Displays the form for creating a new booking request.
     */
    @GetMapping("/bookings/new")
    public String showBookingForm(Model model) {
        Booking newBooking = new Booking();
        // Initialize nested objects to prevent errors in the form template
        newBooking.setFacility(new Facility());
        model.addAttribute("booking", newBooking); 
        return "booking-form";
    }
    
    /**
     * Handles the submission of a new booking request from a student.
     */
    @PostMapping("/bookings")
    public String createBooking(@ModelAttribute Booking booking, Principal principal) {
        // Securely find the currently logged-in user from the database
        Person person = personRepository.findByName(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user: " + principal.getName()));

        // Ensure the person is a student before associating them with the booking
        if (person instanceof User) {
            booking.setUser((User) person);
        } else {
            // This is a safeguard in case a non-student tries to create a booking
            return "redirect:/student/dashboard?error=auth";
        }
        
        try {
            bookingService.createBooking(booking);
        } catch (IllegalArgumentException e) {
            return "redirect:/bookings/new?error=" + java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8);
        }
        
        // Redirect back to the student's dashboard to show the updated list
        return "redirect:/student/dashboard?success=" + java.net.URLEncoder.encode("Booking request submitted successfully.", java.nio.charset.StandardCharsets.UTF_8);
    }

    /**
     * Processes an approval or rejection action from an approver.
     */
    @PostMapping("/bookings/{id}/process")
    public String processBooking(@PathVariable String id, 
                                 @RequestParam String decision,
                                 @RequestParam(required = false, defaultValue = "") String remark,
                                 Principal principal,
                                 org.springframework.security.core.Authentication authentication) {
        boolean isApproved = "approve".equalsIgnoreCase(decision);
        String approverName = principal.getName();
        
        String authority = authentication.getAuthorities().iterator().next().getAuthority();
        String approverRole = switch (authority) {
            case "ROLE_STAFF_ADVISOR" -> "Staff Advisor";
            case "ROLE_HOD" -> "HOD";
            case "ROLE_DEAN" -> "Dean";
            case "ROLE_PRINCIPAL" -> "Principal";
            default -> "Unknown";
        };

        bookingService.processApproval(id, approverRole, approverName, isApproved, remark);
        
        // Redirect the approver back to their specific dashboard after taking action
        return switch (authority) {
            case "ROLE_STAFF_ADVISOR" -> "redirect:/staff-advisor/dashboard";
            case "ROLE_HOD" -> "redirect:/hod/dashboard";
            case "ROLE_DEAN" -> "redirect:/dean/dashboard";
            case "ROLE_PRINCIPAL" -> "redirect:/principal/dashboard";
            default -> "redirect:/"; // Fallback redirect
        };
    }

    /**
     * Fetches the approval history for the currently logged-in approver.
     */
    @GetMapping("/approver/history")
    public String showApproverHistory(Model model, Principal principal) {
        Person person = personRepository.findByName(principal.getName()).orElse(null);
        if (person != null) {
            List<ApprovedBookingHistory> historyList = historyRepository.findByApproverIdOrderByApprovalTimestampDesc(person.getId());
            model.addAttribute("historyList", historyList);
            model.addAttribute("username", principal.getName());
        }
        return "approver-history";
    }
}

