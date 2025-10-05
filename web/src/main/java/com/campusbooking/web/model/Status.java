package com.campusbooking.web.model;

// This should be an enum, not a class.
public enum Status {
    PENDING_STAFF_APPROVAL,
    PENDING_HOD_APPROVAL,
    PENDING_DEAN_APPROVAL,
    PENDING_PRINCIPAL_APPROVAL,
    APPROVED,
    REJECTED
}