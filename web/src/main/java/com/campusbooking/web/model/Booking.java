package com.campusbooking.web.model;

import com.campusbooking.web.actor.Person;
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

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    private String bookingId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "facility_id")
    private Facility facility;

    // --- NEW FIELDS TO STORE ASSIGNED APPROVERS ---
    @ManyToOne
    @JoinColumn(name = "assigned_staff_advisor_id")
    private Person assignedStaffAdvisor;

    @ManyToOne
    @JoinColumn(name = "assigned_hod_id")
    private Person assignedHod;
    // --- END OF NEW FIELDS ---

    private String eventName;
    private String eventDescription;
    
    @DateTimeFormat(pattern = "dd/MM/yy")
    private LocalDate date;
    
    private String timeSlot;
    private boolean paSystemRequired;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String remarks;
    
    public Booking() {}

    // Getters and Setters
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Facility getFacility() { return facility; }
    public void setFacility(Facility facility) { this.facility = facility; }
    public Person getAssignedStaffAdvisor() { return assignedStaffAdvisor; }
    public void setAssignedStaffAdvisor(Person assignedStaffAdvisor) { this.assignedStaffAdvisor = assignedStaffAdvisor; }
    public Person getAssignedHod() { return assignedHod; }
    public void setAssignedHod(Person assignedHod) { this.assignedHod = assignedHod; }
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

