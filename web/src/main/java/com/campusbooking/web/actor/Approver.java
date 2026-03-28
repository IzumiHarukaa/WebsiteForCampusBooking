package com.campusbooking.web.actor;

/**
 * Abstract base class for all approver types.
 * Inherits from the Person entity.
 */
public abstract class Approver extends Person {
    
    /**
     * No-argument constructor required by JPA.
     */
    public Approver() {}

    /**
     * Constructor to create an approver with credentials and a role.
     * @param name The approver's name (used as username).
     * @param password The approver's password.
     * @param role The approver's role.
     */
    public Approver(String name, String password, String role) {
        super(name, password, role);
    }
}

