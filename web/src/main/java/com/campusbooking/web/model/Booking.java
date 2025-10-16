package com.campusbooking.web.model;

import com.campusbooking.web.actor.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

/**
 * Represents a single event booking request in the database.
 * This is the central entity that connects users, facilities, and the approval workflow.
 */
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    private String bookingId;

    /**
     * Establishes a many-to-one relationship with the User entity.
     * Many bookings can belong to one user.
     * The `user_id` column in the `bookings` table will be the foreign key.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Establishes a many-to-one relationship with the Facility entity.
     * CascadeType.ALL ensures that if a new facility is created with a booking,
     * it gets saved to the database automatically.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "facility_id")
    private Facility facility;

    private String eventName;
    private String eventDescription;
    
    // Tells Spring how to convert the "dd/MM/yy" string from the form into a LocalDate object.
    @DateTimeFormat(pattern = "dd/MM/yy")
    private LocalDate date;
    
    private String timeSlot;
    private boolean paSystemRequired;

    // Stores the enum value as a string (e.g., "PENDING_STAFF_APPROVAL") in the database.
    @Enumerated(EnumType.STRING)
    private Status status;

    private String remarks;
    
    /**
     * No-argument constructor required by JPA.
     */
    public Booking() {}

    // --- Getters and Setters for all fields ---

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Facility getFacility() { return facility; }
    public void setFacility(Facility facility) { this.facility = facility; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getEventDescription() { return eventDescription; }
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public boolean isPaSystemRequired() { return paSystemRequired; }
    public void setPaSystemRequired(boolean paSystemRequired) { this.paSystemRequired = paSystemRequired; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}

