package com.campusbooking.web.repository;

import com.campusbooking.web.model.ApprovedBookingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovedBookingHistoryRepository extends JpaRepository<ApprovedBookingHistory, Long> {
    List<ApprovedBookingHistory> findByApproverIdOrderByApprovalTimestampDesc(Long approverId);
}
