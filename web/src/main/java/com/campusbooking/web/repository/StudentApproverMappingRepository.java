package com.campusbooking.web.repository;

import com.campusbooking.web.actor.Person;
import com.campusbooking.web.model.StudentApproverMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentApproverMappingRepository extends JpaRepository<StudentApproverMapping, Long> {
    Optional<StudentApproverMapping> findByStudent(Person student);
}
