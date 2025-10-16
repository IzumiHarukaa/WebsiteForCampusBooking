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
import java.util.stream.Stream;

/**
 * This controller manages all web requests related to the booking process,
 * including creating new requests and processing approvals.
 */
@Controller
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PersonRepository personRepository;

    /**
     * Displays the form for creating a new booking request.
     * It also fetches the lists of Staff Advisors and HODs to populate the dropdown menus.
     */
    @GetMapping("/bookings/new")
    public String showBookingForm(Model model) {
        Booking newBooking = new Booking();
        
        // Initialize all nested objects that the form requires to prevent NullPointerExceptions.
        newBooking.setFacility(new Facility());

        model.addAttribute("booking", newBooking);

        // Fetch all Staff Advisors and HODs from the database
        List<Person> staffAdvisors = personRepository.findByRole("ROLE_STAFF_ADVISOR");
        List<Person> hods = personRepository.findByRole("ROLE_HOD");
        
        // Combine them into a single list to pass to the view
        List<Person> approverList = Stream.concat(staffAdvisors.stream(), hods.stream()).toList();
        model.addAttribute("approvers", approverList);

        return "booking-form";
    }
    
    /**
     * Handles the submission of a new booking request from a student.
     * It securely associates the request with the currently logged-in student and the selected approvers.
     */
    @PostMapping("/bookings")
    public String createBooking(@ModelAttribute Booking booking, 
                                @RequestParam Long assignedStaffAdvisorId,
                                @RequestParam Long assignedHodId,
                                Principal principal) {
                                    
        // 1. Securely find the currently logged-in user (student)
        Person student = personRepository.findByName(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user: " + principal.getName()));

        // 2. Fetch the complete Person objects for the selected approvers using their IDs
        Person staffAdvisor = personRepository.findById(assignedStaffAdvisorId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Staff Advisor ID"));
        Person hod = personRepository.findById(assignedHodId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid HOD ID"));

        // 3. Set the complete, fetched objects on the new booking
        if (student instanceof User) {
            booking.setUser((User) student);
        } else {
            return "redirect:/student/dashboard?error=auth";
        }
        booking.setAssignedStaffAdvisor(staffAdvisor);
        booking.setAssignedHod(hod);
        
        // 4. Save the fully constructed booking object
        bookingService.createBooking(booking);
        
        // 5. Redirect back to the student's dashboard
        return "redirect:/student/dashboard";
    }

    /**
     * Processes an approval or rejection action submitted from an approver's dashboard.
     */
    @PostMapping("/bookings/{id}/process")
    public String processBooking(@PathVariable String id, 
                                 @RequestParam String decision,
                                 @RequestParam String approverRole,
                                 @RequestParam String approverName,
                                 @RequestParam String remark) {
        boolean isApproved = "approve".equalsIgnoreCase(decision);
        bookingService.processApproval(id, approverRole, approverName, isApproved, remark);
        
        // Redirect the approver back to their specific dashboard after taking action
        return switch (approverRole) {
            case "Staff Advisor" -> "redirect:/staff-advisor/dashboard";
            case "HOD" -> "redirect:/hod/dashboard";
            case "Dean" -> "redirect:/dean/dashboard";
            case "Principal" -> "redirect:/principal/dashboard";
            default -> "redirect:/"; // Fallback redirect
        };
    }
}

