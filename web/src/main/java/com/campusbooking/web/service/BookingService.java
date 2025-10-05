package com.campusbooking.web.service;

import com.campusbooking.web.model.Booking;
import com.campusbooking.web.model.Status;
import com.campusbooking.web.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    private static final Map<Status, Status> nextStatusMap = new EnumMap<>(Status.class);
    static {
        nextStatusMap.put(Status.PENDING_STAFF_APPROVAL, Status.PENDING_HOD_APPROVAL);
        nextStatusMap.put(Status.PENDING_HOD_APPROVAL, Status.PENDING_DEAN_APPROVAL);
        nextStatusMap.put(Status.PENDING_DEAN_APPROVAL, Status.PENDING_PRINCIPAL_APPROVAL);
        nextStatusMap.put(Status.PENDING_PRINCIPAL_APPROVAL, Status.APPROVED);
    }
    
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    
    public Optional<Booking> getBookingById(String id) {
        return bookingRepository.findById(id);
    }

    public Booking createBooking(Booking booking) {
        booking.setStatus(Status.PENDING_STAFF_APPROVAL);
        // FIX: Ensure new bookings always start with a non-null, empty remarks string.
        if (booking.getRemarks() == null) {
            booking.setRemarks("");
        }
        return bookingRepository.save(booking);
    }

    public Optional<Booking> processApproval(String bookingId, String approverRole, String approverName, boolean isApproved, String remark) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            return Optional.empty(); 
        }
        
        Booking booking = optionalBooking.get();
        Status requiredStatus = getRequiredStatusForRole(approverRole);
        
        if (booking.getStatus() != requiredStatus) {
            return Optional.empty();
        }
        
        Status newStatus = isApproved ? nextStatusMap.get(booking.getStatus()) : Status.REJECTED;
        
        // FIX: Defensively handle null remarks to prevent the NullPointerException.
        String updatedRemarks = (booking.getRemarks() != null) ? booking.getRemarks() : "";
        
        if (remark != null && !remark.trim().isEmpty()) {
             updatedRemarks = updatedRemarks + " " + approverRole + " (" + approverName + "): " + remark;
        }
        
        booking.setStatus(newStatus);
        booking.setRemarks(updatedRemarks.trim()); 
        
        bookingRepository.save(booking);
        return Optional.of(booking);
    }

    private Status getRequiredStatusForRole(String role) {
        return switch (role) {
            case "Staff Advisor" -> Status.PENDING_STAFF_APPROVAL;
            case "HOD" -> Status.PENDING_HOD_APPROVAL;
            case "Dean" -> Status.PENDING_DEAN_APPROVAL;
            case "Principal" -> Status.PENDING_PRINCIPAL_APPROVAL;
            default -> null;
        };
    }
}

