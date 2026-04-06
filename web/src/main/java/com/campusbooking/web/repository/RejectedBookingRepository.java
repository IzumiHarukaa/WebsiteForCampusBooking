package com.campusbooking.web.repository;

import com.campusbooking.web.model.RejectedBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RejectedBookingRepository extends JpaRepository<RejectedBooking, Long> {
    List<RejectedBooking> findByOriginalBookingIdOrderByRejectedAtDesc(String originalBookingId);
}
