package com.campusbooking.web.config;

import com.campusbooking.web.actor.Dean;
import com.campusbooking.web.actor.HOD;
import com.campusbooking.web.actor.Person;
import com.campusbooking.web.actor.Principal;
import com.campusbooking.web.actor.StaffAdvisor;
import com.campusbooking.web.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Automatically creates default staff users when the application starts if they don't already exist.
 * This is the secure alternative to allowing manual registration of these privileged roles.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createStaffIfNotFound("staff", "staff123", "ROLE_STAFF_ADVISOR");
        createStaffIfNotFound("hod", "hod123", "ROLE_HOD");
        createStaffIfNotFound("dean", "dean123", "ROLE_DEAN");
        createStaffIfNotFound("principal", "principal123", "ROLE_PRINCIPAL");
    }

    private void createStaffIfNotFound(String username, String rawPassword, String role) {
        if (personRepository.findByName(username).isEmpty()) {
            Person staffMember;
            switch (role) {
                case "ROLE_STAFF_ADVISOR":
                    staffMember = new StaffAdvisor();
                    break;
                case "ROLE_HOD":
                    staffMember = new HOD();
                    break;
                case "ROLE_DEAN":
                    staffMember = new Dean();
                    break;
                case "ROLE_PRINCIPAL":
                    staffMember = new Principal();
                    break;
                default:
                    return; // Ignore unknown roles
            }
            
            staffMember.setName(username);
            staffMember.setPassword(passwordEncoder.encode(rawPassword));
            staffMember.setRole(role);
            personRepository.save(staffMember);
            
            System.out.println("Seeded default user: " + username + " with role: " + role);
        }
    }
}
