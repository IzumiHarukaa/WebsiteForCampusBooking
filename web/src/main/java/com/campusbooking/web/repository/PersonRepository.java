package com.campusbooking.web.repository;

import com.campusbooking.web.actor.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * A Spring Data JPA repository for the Person entity.
 * This interface handles all database operations for all types of users (Students and Approvers).
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    /**
     * Finds a person in the database by their unique name (used as the username).
     * This is used by the UserDetailsService for authentication.
     *
     * @param name The username to search for.
     * @return An Optional containing the Person if found, or an empty Optional otherwise.
     */
    Optional<Person> findByName(String name);

    /**
     * Finds all people in the database who have a specific role.
     * This is used to populate the dropdown menus on the new booking form.
     *
     * @param role The role to search for (e.g., "ROLE_STAFF_ADVISOR").
     * @return A List of all Person objects matching the given role.
     */
    List<Person> findByRole(String role);
}

