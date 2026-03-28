package com.campusbooking.web.controller;

import com.campusbooking.web.model.Booking;
import com.campusbooking.web.model.Status;
import com.campusbooking.web.repository.BookingRepository;
import com.campusbooking.web.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import java.util.List;

/**
 * This controller handles displaying the correct dashboard for each user role
 * after they log in.
 */
@Controller
public class DashboardController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PersonRepository personRepository;

    /** Number of rows per page, configurable in application.properties */
    @Value("${app.pagination.page-size:10}")
    private int pageSize;

    /**
     * Displays the dashboard for a logged-in student.
     * Supports ?page=N for pagination.
     */
    @GetMapping("/student/dashboard")
    public String studentDashboard(Model model, Principal principal,
            @RequestParam(defaultValue = "0") int page) {
        List<Booking> allBookings = bookingRepository.findByUserName(principal.getName());
        int totalPages = (int) Math.ceil((double) allBookings.size() / pageSize);
        int safePage = Math.max(0, Math.min(page, Math.max(0, totalPages - 1)));
        List<Booking> pageBookings = allBookings.subList(
                Math.min(safePage * pageSize, allBookings.size()),
                Math.min(safePage * pageSize + pageSize, allBookings.size()));
        model.addAttribute("bookings", pageBookings);
        model.addAttribute("username", principal.getName());
        model.addAttribute("currentPage", safePage);
        model.addAttribute("totalPages", totalPages);
        return "student-dashboard";
    }

    /**
     * Displays the dashboard for the Staff Advisor.
     * It fetches only booking requests with the status PENDING_STAFF_APPROVAL.
     */
    @GetMapping("/staff-advisor/dashboard")
    public String staffAdvisorDashboard(Model model, Authentication authentication,
            @RequestParam(defaultValue = "0") int page) {
        prepareApproverModel(model, authentication, "Staff Advisor", page);
        return "staff-advisor-dashboard";
    }

    @GetMapping("/hod/dashboard")
    public String hodDashboard(Model model, Authentication authentication,
            @RequestParam(defaultValue = "0") int page) {
        prepareApproverModel(model, authentication, "HOD", page);
        return "hod-dashboard";
    }

    @GetMapping("/dean/dashboard")
    public String deanDashboard(Model model, Authentication authentication,
            @RequestParam(defaultValue = "0") int page) {
        prepareApproverModel(model, authentication, "Dean", page);
        return "dean-dashboard";
    }

    @GetMapping("/principal/dashboard")
    public String principalDashboard(Model model, Authentication authentication,
            @RequestParam(defaultValue = "0") int page) {
        prepareApproverModel(model, authentication, "Principal", page);
        return "principal-dashboard";
    }

    /**
     * Displays the page shown to approvers when they have no pending requests.
     */
    @GetMapping("/no-pending-requests")
    public String noPendingRequests() {
        return "no-pending-requests";
    }

    private void prepareApproverModel(Model model, Authentication authentication, String roleName, int page) {
        com.campusbooking.web.actor.Person person = personRepository.findByName(authentication.getName()).orElse(null);
        Long approverId = person != null ? person.getId() : -1L;

        List<Booking> allPending = new java.util.ArrayList<>();

        if ("Staff Advisor".equals(roleName)) {
            allPending.addAll(bookingRepository.findPendingForStaffAdvisor(Status.PENDING_STAFF_APPROVAL, approverId));
            allPending.addAll(
                    bookingRepository.findPendingForStaffAdvisor(Status.PENDING_STAFF_APPROVAL_REAPPLIED, approverId));
        } else if ("HOD".equals(roleName)) {
            allPending.addAll(bookingRepository.findPendingForHOD(Status.PENDING_HOD_APPROVAL, approverId));
            allPending.addAll(bookingRepository.findPendingForHOD(Status.PENDING_HOD_APPROVAL_REAPPLIED, approverId));
        } else if ("Dean".equals(roleName)) {
            allPending.addAll(bookingRepository.findByStatus(Status.PENDING_DEAN_APPROVAL));
            allPending.addAll(bookingRepository.findByStatus(Status.PENDING_DEAN_APPROVAL_REAPPLIED));
        } else if ("Principal".equals(roleName)) {
            allPending.addAll(bookingRepository.findByStatus(Status.PENDING_PRINCIPAL_APPROVAL));
            allPending.addAll(bookingRepository.findByStatus(Status.PENDING_PRINCIPAL_APPROVAL_REAPPLIED));
        }

        int totalPages = (int) Math.ceil((double) allPending.size() / pageSize);
        int safePage = Math.max(0, Math.min(page, Math.max(0, totalPages - 1)));
        List<Booking> pageBookings = allPending.subList(
                Math.min(safePage * pageSize, allPending.size()),
                Math.min(safePage * pageSize + pageSize, allPending.size()));

        model.addAttribute("bookings", pageBookings);
        model.addAttribute("username", authentication.getName());
        model.addAttribute("role", roleName);
        model.addAttribute("currentPage", safePage);
        model.addAttribute("totalPages", totalPages);
    }
}
