package com.campusbooking.web.repository;

import com.campusbooking.web.actor.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    // Method to find a user by their name (which we use as the username)
    Optional<Person> findByName(String name);
}
