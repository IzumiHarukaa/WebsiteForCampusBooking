package com.campusbooking.web.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "approved_booking_history")
public class ApprovedBookingHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;
    
    private String originalBookingId;
    private Long userId;
    private Long facilityId;
    private Long approverId;
    
    private String userName;
    private String facilityName;
    private String approverName;
    
    private String eventName;
    private String eventDescription;
    
    private LocalDate date;
    private String timeSlot;
    
    private boolean paSystemRequired;
    
    private String remarks;
    
    @Column(insertable = false, updatable = false)
    private LocalDateTime approvalTimestamp;

    public ApprovedBookingHistory() {}

    public Long getHistoryId() { return historyId; }
    public void setHistoryId(Long historyId) { this.historyId = historyId; }

    public String getOriginalBookingId() { return originalBookingId; }
    public void setOriginalBookingId(String originalBookingId) { this.originalBookingId = originalBookingId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getFacilityId() { return facilityId; }
    public void setFacilityId(Long facilityId) { this.facilityId = facilityId; }

    public Long getApproverId() { return approverId; }
    public void setApproverId(Long approverId) { this.approverId = approverId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

    public String getApproverName() { return approverName; }
    public void setApproverName(String approverName) { this.approverName = approverName; }

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

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getApprovalTimestamp() { return approvalTimestamp; }
    public void setApprovalTimestamp(LocalDateTime approvalTimestamp) { this.approvalTimestamp = approvalTimestamp; }
}
