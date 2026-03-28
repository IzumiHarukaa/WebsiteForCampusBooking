package com.campusbooking.web.repository;

import com.campusbooking.web.model.Booking;
import com.campusbooking.web.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    // Find all bookings with a specific status
    List<Booking> findByStatus(Status status);

    // Find all bookings created by a specific user (by name)
    List<Booking> findByUserName(String name);

    // Check if a booking exists for the given facility, date, and timeslot (excluding rejected bookings, and optionally excluding a specific booking ID)
    boolean existsByFacilityIdAndDateAndTimeSlotIgnoreCaseAndStatusNotAndBookingIdNot(
        Long facilityId, 
        java.time.LocalDate date, 
        String timeSlot, 
        Status status,
        String bookingId
    );

    @org.springframework.data.jpa.repository.Query("SELECT b FROM Booking b " +
            "JOIN StudentApproverMapping map ON b.user.id = map.student.id " +
            "WHERE b.status = :status AND map.staffAdvisor.id = :approverId")
    List<Booking> findPendingForStaffAdvisor(@org.springframework.data.repository.query.Param("status") Status status, 
                                             @org.springframework.data.repository.query.Param("approverId") Long approverId);

    @org.springframework.data.jpa.repository.Query("SELECT b FROM Booking b " +
            "JOIN StudentApproverMapping map ON b.user.id = map.student.id " +
            "WHERE b.status = :status AND map.hod.id = :approverId")
    List<Booking> findPendingForHOD(@org.springframework.data.repository.query.Param("status") Status status, 
                                    @org.springframework.data.repository.query.Param("approverId") Long approverId);
    /**
     * Deletes a booking by its primary key using a direct JPQL DELETE statement.
     * This bypasses Hibernate's entity lifecycle (and thus CascadeType), avoiding
     * unintended cascade deletions to related entities like Facility.
     */
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Query("DELETE FROM Booking b WHERE b.bookingId = :bookingId")
    void deleteByBookingIdDirect(@org.springframework.data.repository.query.Param("bookingId") String bookingId);
}
