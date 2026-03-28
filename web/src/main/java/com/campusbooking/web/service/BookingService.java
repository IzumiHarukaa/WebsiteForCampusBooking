package com.campusbooking.web.service;

import com.campusbooking.web.model.ApprovedBookingHistory;
import com.campusbooking.web.model.Booking;
import com.campusbooking.web.model.Status;
import com.campusbooking.web.repository.ApprovedBookingHistoryRepository;
import com.campusbooking.web.repository.BookingRepository;
import com.campusbooking.web.repository.PersonRepository;
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

    @Autowired
    private ApprovedBookingHistoryRepository historyRepository;

    @Autowired
    private PersonRepository personRepository;

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
        // FIX: Validate that the booking date is not in the past.
        if (booking.getDate() != null && booking.getDate().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("The selected event date cannot be in the past.");
        }
        
        if (booking.getFacility() != null && booking.getFacility().getName() != null) {
            boolean isConflict = bookingRepository.existsByFacilityNameIgnoreCaseAndDateAndTimeSlotIgnoreCaseAndStatusNot(
                    booking.getFacility().getName(),
                    booking.getDate(),
                    booking.getTimeSlot(),
                    Status.REJECTED
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

