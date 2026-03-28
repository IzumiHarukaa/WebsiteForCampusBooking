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
import com.campusbooking.web.model.Status;
import com.campusbooking.web.model.StudentApproverMapping;
import com.campusbooking.web.repository.ApprovedBookingHistoryRepository;
import com.campusbooking.web.repository.BookingRepository;
import com.campusbooking.web.repository.FacilityRepository;
import com.campusbooking.web.repository.StudentApproverMappingRepository;

@Controller
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private StudentApproverMappingRepository studentApproverMappingRepository;

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
        model.addAttribute("facilities", facilityRepository.findAll());
        model.addAttribute("staffAdvisors", personRepository.findByRole("ROLE_STAFF_ADVISOR"));
        model.addAttribute("hods", personRepository.findByRole("ROLE_HOD"));
        return "booking-form";
    }

    /**
     * Handles the submission of a new booking request from a student.
     */
    @PostMapping("/bookings")
    public String createBooking(@ModelAttribute Booking booking,
            @RequestParam(required = false) Long staffAdvisorId,
            @RequestParam(required = false) Long hodId,
            Principal principal) {
        // Securely find the currently logged-in user from the database
        Person person = personRepository.findByName(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user: " + principal.getName()));

        // Ensure the person is a student before associating them with the booking
        if (person instanceof User) {
            booking.setUser((User) person);
            handleStudentApproverMapping(person, staffAdvisorId, hodId);
        } else {
            // This is a safeguard in case a non-student tries to create a booking
            return "redirect:/student/dashboard?error=auth";
        }

        try {
            // Re-fetch facility from DB by ID to ensure it's managed
            if (booking.getFacility() != null && booking.getFacility().getId() != null) {
                facilityRepository.findById(booking.getFacility().getId()).ifPresent(booking::setFacility);
            }
            bookingService.createBooking(booking);
        } catch (IllegalArgumentException e) {
            return "redirect:/bookings/new?error="
                    + java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8);
        }

        // Bug 4 fix: use RedirectAttributes flash so the dashboard template can read
        // ${successMessage}
        return "redirect:/student/dashboard";
    }

    private void handleStudentApproverMapping(Person student, Long staffAdvisorId, Long hodId) {
        StudentApproverMapping mapping = studentApproverMappingRepository.findByStudent(student)
                .orElse(new StudentApproverMapping());
        mapping.setStudent(student);
        if (staffAdvisorId != null)
            personRepository.findById(staffAdvisorId).ifPresent(mapping::setStaffAdvisor);
        if (hodId != null)
            personRepository.findById(hodId).ifPresent(mapping::setHod);
        studentApproverMappingRepository.save(mapping);
    }

    @GetMapping("/bookings/{id}/edit")
    public String editBookingForm(@PathVariable String id, Model model, Principal principal) {
        java.util.Optional<Booking> optBooking = bookingService.getBookingById(id);
        if (optBooking.isEmpty() || !optBooking.get().getUser().getName().equals(principal.getName())
                || optBooking.get().getStatus() != Status.REJECTED) {
            return "redirect:/student/dashboard?error=auth";
        }
        model.addAttribute("booking", optBooking.get());
        model.addAttribute("facilities", facilityRepository.findAll());
        model.addAttribute("staffAdvisors", personRepository.findByRole("ROLE_STAFF_ADVISOR"));
        model.addAttribute("hods", personRepository.findByRole("ROLE_HOD"));

        studentApproverMappingRepository.findByStudent(optBooking.get().getUser()).ifPresent(mapping -> {
            model.addAttribute("selectedStaffAdvisorId",
                    mapping.getStaffAdvisor() != null ? mapping.getStaffAdvisor().getId() : null);
            model.addAttribute("selectedHodId", mapping.getHod() != null ? mapping.getHod().getId() : null);
        });

        return "booking-form";
    }

    @PostMapping("/bookings/{id}/reapply")
    public String reapplyBooking(@PathVariable String id, @ModelAttribute Booking updatedBooking,
            @RequestParam(required = false) Long staffAdvisorId,
            @RequestParam(required = false) Long hodId,
            Principal principal) {
        java.util.Optional<Booking> optBooking = bookingService.getBookingById(id);
        if (optBooking.isEmpty() || !optBooking.get().getUser().getName().equals(principal.getName())
                || optBooking.get().getStatus() != Status.REJECTED) {
            return "redirect:/student/dashboard?error=auth";
        }

        Booking existingBooking = optBooking.get();
        existingBooking.setEventName(updatedBooking.getEventName());
        existingBooking.setEventDescription(updatedBooking.getEventDescription());
        existingBooking.setDate(updatedBooking.getDate());
        existingBooking.setTimeSlot(updatedBooking.getTimeSlot());

        if (updatedBooking.getFacility() != null && updatedBooking.getFacility().getId() != null) {
            facilityRepository.findById(updatedBooking.getFacility().getId()).ifPresent(existingBooking::setFacility);
        }

        existingBooking.setPaSystemRequired(updatedBooking.isPaSystemRequired());

        // Bug 3 fix: run through createBooking() to enforce date & conflict validation.
        // createBooking() also excludes the booking's own ID from the conflict check,
        // so a reapplication for the same slot it previously held is allowed.
        try {
            bookingService.createBooking(existingBooking);
        } catch (IllegalArgumentException e) {
            return "redirect:/bookings/" + id + "/edit?error=" +
                    java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8);
        }

        // After createBooking sets status to PENDING_STAFF_APPROVAL, upgrade it to
        // REAPPLIED
        existingBooking.setStatus(Status.PENDING_STAFF_APPROVAL_REAPPLIED);
        bookingRepository.save(existingBooking);

        handleStudentApproverMapping(existingBooking.getUser(), staffAdvisorId, hodId);

        return "redirect:/student/dashboard";
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
            List<ApprovedBookingHistory> historyList = historyRepository
                    .findByApproverIdOrderByApprovalTimestampDesc(person.getId());
            model.addAttribute("historyList", historyList);
            model.addAttribute("username", principal.getName());
        }
        return "approver-history";
    }

    /**
     * Cancels a pending booking owned by the currently logged-in student.
     * Only PENDING_STAFF_APPROVAL and PENDING_STAFF_APPROVAL_REAPPLIED bookings can
     * be cancelled.
     */
    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable("id") String bookingId, Principal principal,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        boolean cancelled = bookingService.cancelBooking(bookingId, principal.getName());
        if (cancelled) {
            redirectAttrs.addFlashAttribute("successMessage", "Booking '" + bookingId + "' has been cancelled.");
        } else {
            redirectAttrs.addFlashAttribute("errorMessage",
                    "Could not cancel booking '" + bookingId
                            + "'. It may have already been forwarded for approval or does not belong to you.");
        }
        return "redirect:/student/dashboard";
    }
}
