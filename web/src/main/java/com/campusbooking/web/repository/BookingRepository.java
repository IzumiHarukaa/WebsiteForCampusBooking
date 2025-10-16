package com.campusbooking.web.repository;

import com.campusbooking.web.actor.Person;
import com.campusbooking.web.model.Booking;
import com.campusbooking.web.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * A Spring Data JPA repository for the Booking entity.
 * This interface handles all database operations for booking requests.
 * Spring Data JPA automatically implements the methods based on their names.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    /**
     * Finds all bookings that currently have a specific status.
     * This is used by the Dean and Principal dashboards to find requests
     * pending their approval.
     *
     * @param status The status to search for (e.g., PENDING_DEAN_APPROVAL).
     * @return A List of all Booking objects with the given status.
     */
    List<Booking> findByStatus(Status status);

    /**
     * Finds all bookings created by a specific user, identified by their username.
     * This is used by the student dashboard to show a user only their own requests.
     *
     * @param name The username of the student.
     * @return A List of all Booking objects created by that user.
     */
    List<Booking> findByUserName(String name);

    /**
     * Finds all bookings assigned to a specific Staff Advisor that are also in a specific status.
     * This is used by the Staff Advisor dashboard to show only the requests they need to act on.
     *
     * @param staffAdvisor The Person object representing the assigned Staff Advisor.
     * @param status       The required status (e.g., PENDING_STAFF_APPROVAL).
     * @return A List of all relevant Booking objects.
     */
    List<Booking> findByAssignedStaffAdvisorAndStatus(Person staffAdvisor, Status status);

    /**
     * Finds all bookings assigned to a specific HOD that are also in a specific status.
     * This is used by the HOD dashboard to show only the requests they need to act on.
     *
     * @param hod    The Person object representing the assigned HOD.
     * @param status The required status (e.g., PENDING_HOD_APPROVAL).
     * @return A List of all relevant Booking objects.
     */
    List<Booking> findByAssignedHodAndStatus(Person hod, Status status);
}

