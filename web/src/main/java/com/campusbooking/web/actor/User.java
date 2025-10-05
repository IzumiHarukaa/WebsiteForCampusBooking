package com.campusbooking.web.actor;

import jakarta.persistence.Entity;

/**
 * Represents a student user in the system.
 * Inherits from Person.
 */
@Entity
public class User extends Person {
    
    /**
     * No-argument constructor required by JPA.
     */
    public User() {}

    /**
     * Constructor to create a new User (student).
     * Automatically sets the role to "ROLE_STUDENT".
     * @param name The student's name (used as username).
     * @param password The student's password.
     */
    public User(String name, String password) {
        super(name, password, "ROLE_STUDENT");
    }
}
