package com.campusbooking.web.controller;

import com.campusbooking.web.repository.ApprovedBookingHistoryRepository;
import com.campusbooking.web.repository.BookingRepository;
import com.campusbooking.web.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * TEMPORARY ADMIN CONTROLLER — used only to reset data for testing.
 * Remove this file after testing is complete.
 */
@Controller
public class AdminResetController {

    @Autowired
    private ApprovedBookingHistoryRepository historyRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PersonRepository personRepository;

    /**
     * Shows the admin reset confirmation page.
     */
    @GetMapping("/admin/reset")
    public String showResetPage() {
        return "admin-reset";
    }

    /**
     * Deletes all history, bookings, and users from the database.
     * Then redirects to login so the DataSeeder can seed fresh default users on next startup.
     */
    @PostMapping("/admin/reset")
    public String performReset() {
        historyRepository.deleteAll();
        bookingRepository.deleteAll();
        personRepository.deleteAll();
        return "redirect:/admin/reset?success";
    }
}
