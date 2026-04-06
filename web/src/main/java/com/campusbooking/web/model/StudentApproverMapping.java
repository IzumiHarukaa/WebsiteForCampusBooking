package com.campusbooking.web.model;

import com.campusbooking.web.actor.Person;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "student_approver_mapping")
public class StudentApproverMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Person student;

    @ManyToOne
    @JoinColumn(name = "staff_advisor_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Person staffAdvisor;

    @ManyToOne
    @JoinColumn(name = "hod_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Person hod;

    public StudentApproverMapping() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Person getStudent() { return student; }
    public void setStudent(Person student) { this.student = student; }
    public Person getStaffAdvisor() { return staffAdvisor; }
    public void setStaffAdvisor(Person staffAdvisor) { this.staffAdvisor = staffAdvisor; }
    public Person getHod() { return hod; }
    public void setHod(Person hod) { this.hod = hod; }
}
