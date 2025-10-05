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
}
