package com.campusbooking.web.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "rejected_bookings")
public class RejectedBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalBookingId;
    
    // We can store a snapshot of the rejected data or just the reason and timestamp
    private String rejectionReason;
    
    private String rejectedByRole;
    private String rejectedByName;
    
    private LocalDateTime rejectedAt;

    public RejectedBooking() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOriginalBookingId() { return originalBookingId; }
    public void setOriginalBookingId(String originalBookingId) { this.originalBookingId = originalBookingId; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public String getRejectedByRole() { return rejectedByRole; }
    public void setRejectedByRole(String rejectedByRole) { this.rejectedByRole = rejectedByRole; }

    public String getRejectedByName() { return rejectedByName; }
    public void setRejectedByName(String rejectedByName) { this.rejectedByName = rejectedByName; }

    public LocalDateTime getRejectedAt() { return rejectedAt; }
    public void setRejectedAt(LocalDateTime rejectedAt) { this.rejectedAt = rejectedAt; }
}
