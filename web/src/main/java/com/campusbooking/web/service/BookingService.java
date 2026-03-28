package com.campusbooking.web.service;

import com.campusbooking.web.model.ApprovedBookingHistory;
import com.campusbooking.web.model.Booking;
import com.campusbooking.web.model.Status;
import com.campusbooking.web.model.RejectedBooking;
import com.campusbooking.web.repository.ApprovedBookingHistoryRepository;
import com.campusbooking.web.repository.BookingRepository;
import com.campusbooking.web.repository.PersonRepository;
import com.campusbooking.web.repository.RejectedBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ApprovedBookingHistoryRepository historyRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RejectedBookingRepository rejectedBookingRepository;

    private static final Map<Status, Status> nextStatusMap = new EnumMap<>(Status.class);
    static {
        nextStatusMap.put(Status.PENDING_STAFF_APPROVAL, Status.PENDING_HOD_APPROVAL);
        nextStatusMap.put(Status.PENDING_STAFF_APPROVAL_REAPPLIED, Status.PENDING_HOD_APPROVAL_REAPPLIED);
        nextStatusMap.put(Status.PENDING_HOD_APPROVAL, Status.PENDING_DEAN_APPROVAL);
        nextStatusMap.put(Status.PENDING_HOD_APPROVAL_REAPPLIED, Status.PENDING_DEAN_APPROVAL_REAPPLIED);
        nextStatusMap.put(Status.PENDING_DEAN_APPROVAL, Status.PENDING_PRINCIPAL_APPROVAL);
        nextStatusMap.put(Status.PENDING_DEAN_APPROVAL_REAPPLIED, Status.PENDING_PRINCIPAL_APPROVAL_REAPPLIED);
        nextStatusMap.put(Status.PENDING_PRINCIPAL_APPROVAL, Status.APPROVED);
        nextStatusMap.put(Status.PENDING_PRINCIPAL_APPROVAL_REAPPLIED, Status.APPROVED);
    }
    
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    
    public Optional<Booking> getBookingById(String id) {
        return bookingRepository.findById(id);
    }

    public Booking createBooking(Booking booking) {
        // FIX: Validate that the booking date is not in the past.
        if (booking.getDate() != null && booking.getDate().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("The selected event date cannot be in the past.");
        }
        
        if (booking.getFacility() != null && booking.getFacility().getId() != null) {
            // Conflict check: exclude REJECTED bookings AND the booking's own ID (for reapplies)
            boolean isConflict = bookingRepository.existsByFacilityIdAndDateAndTimeSlotIgnoreCaseAndStatusNotAndBookingIdNot(
                    booking.getFacility().getId(),
                    booking.getDate(),
                    booking.getTimeSlot(),
                    Status.REJECTED,
                    booking.getBookingId() != null ? booking.getBookingId() : "__NONE__"
            );
            if (isConflict) {
                throw new IllegalArgumentException("The selected facility is already booked for this date and time slot.");
            }
        }

        booking.setStatus(Status.PENDING_STAFF_APPROVAL);
        // FIX: Ensure new bookings always start with a non-null, empty remarks string.
        if (booking.getRemarks() == null) {
            booking.setRemarks("");
        }
        return bookingRepository.save(booking);
    }

    /**
     * Cancels a booking if it still belongs to the requesting user AND
     * is still in a pending state (not yet approved or already rejected).
     * Returns true if cancelled successfully, false if not authorised or wrong state.
     */
    public boolean cancelBooking(String bookingId, String requestingUsername) {
        Optional<Booking> opt = bookingRepository.findById(bookingId);
        if (opt.isEmpty()) return false;

        Booking booking = opt.get();

        // Only the owner student may cancel
        if (!booking.getUser().getName().equals(requestingUsername)) return false;

        // Only PENDING statuses can be cancelled (not already approved / rejected / reapplied in-flight)
        Set<Status> cancellableStatuses = Set.of(
            Status.PENDING_STAFF_APPROVAL,
            Status.PENDING_STAFF_APPROVAL_REAPPLIED
        );
        if (!cancellableStatuses.contains(booking.getStatus())) return false;

        // Use direct JPQL DELETE to avoid Hibernate cascade machinery
        // (prevents cascade-deleting the referenced Facility when booking is removed)
        bookingRepository.deleteByBookingIdDirect(booking.getBookingId());
        return true;
    }

    public Optional<Booking> processApproval(String bookingId, String approverRole, String approverName, boolean isApproved, String remark) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            return Optional.empty(); 
        }
        
        Booking booking = optionalBooking.get();
        
        if (!isValidStatusForRole(booking.getStatus(), approverRole)) {
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

        if (newStatus == Status.REJECTED) {
            RejectedBooking rb = new RejectedBooking();
            rb.setOriginalBookingId(booking.getBookingId());
            rb.setRejectionReason(remark);
            rb.setRejectedByRole(approverRole);
            rb.setRejectedByName(approverName);
            rb.setRejectedAt(java.time.LocalDateTime.now());
            rejectedBookingRepository.save(rb);
        }

        if (newStatus == Status.APPROVED) {
            ApprovedBookingHistory history = new ApprovedBookingHistory();
            history.setOriginalBookingId(booking.getBookingId());
            if (booking.getUser() != null) {
                history.setUserId(booking.getUser().getId());
                history.setUserName(booking.getUser().getName());
            }
            if (booking.getFacility() != null) {
                history.setFacilityId(booking.getFacility().getId());
                history.setFacilityName(booking.getFacility().getName());
            }
            personRepository.findByName(approverName).ifPresent(person -> {
                history.setApproverId(person.getId());
                history.setApproverName(person.getName());
            });
            history.setEventName(booking.getEventName());
            history.setEventDescription(booking.getEventDescription());
            history.setDate(booking.getDate());
            history.setTimeSlot(booking.getTimeSlot());
            history.setPaSystemRequired(booking.isPaSystemRequired());
            history.setRemarks(updatedRemarks.trim());
            
            historyRepository.save(history);
        }

        return Optional.of(booking);
    }

    private boolean isValidStatusForRole(Status status, String role) {
        return switch (role) {
            case "Staff Advisor" -> status == Status.PENDING_STAFF_APPROVAL || status == Status.PENDING_STAFF_APPROVAL_REAPPLIED;
            case "HOD" -> status == Status.PENDING_HOD_APPROVAL || status == Status.PENDING_HOD_APPROVAL_REAPPLIED;
            case "Dean" -> status == Status.PENDING_DEAN_APPROVAL || status == Status.PENDING_DEAN_APPROVAL_REAPPLIED;
            case "Principal" -> status == Status.PENDING_PRINCIPAL_APPROVAL || status == Status.PENDING_PRINCIPAL_APPROVAL_REAPPLIED;
            default -> false;
        };
    }
}

