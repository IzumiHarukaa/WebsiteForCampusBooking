<div align="center">
  
# 🎓 Campus Event Booking System
**A Next-Generation, Role-Based Facility Management & Approval Workflow Platform**

[![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-BCrypt-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![MySQL 8.0](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-HTML5-005C0F?style=for-the-badge&logo=thymeleaf&logoColor=white)](https://www.thymeleaf.org/)
[![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)

</div>

---

## 🏛️ System Architecture & Workflow

The Campus Event Booking System digitizes and enforces strict, multi-level bureaucratic approval chains essential for collegiate facility management. Through a highly secure, role-based access control (RBAC) architecture, moving a request from inception to execution requires targeted, sequential validation.

**The Sequential Approval Chain:**
1. **Initiation:** Student submits a comprehensive booking request.
2. **Level 1 (Staff Advisor):** Initial vetting of event scope and student conduct.
3. **Level 2 (Head of Department - HOD):** Departmental approval and resource allocation check.
4. **Level 3 (Dean):** Collegiate-level review for inter-departmental conflicts.
5. **Level 4 (Principal):** The final executive sign-off.

Only requests that traverse this sequence successfully are granted finalized status, ensuring complete administrative oversight.

---

## 🌟 Star Features

Our platform brings enterprise-grade management capabilities to campus administration through several standout architectural decisions:

* 🎯 **Targeted Approver Routing**
  Students don't just broadcast requests into the void. A dynamic dropdown allows them to securely select their specific Staff Advisor and HOD. The system then intelligently routes the request payload *only* to the authorized dashboards of the selected personnel, ensuring data privacy and preventing administrative overload.

* 🔄 **The Reapplication Engine**
  Rejections are no longer dead ends. If a request is declined (e.g., due to a scheduling clash), students can utilize the Reapplication Engine to edit and resubmit their proposal. These resurrected requests are flagged on approver dashboards with a critical **`REAPPLIED`** warning badge, preserving context and preventing redundant reviews.

* 📚 **Historical Audit Trail**
  To maintain strict institutional record-keeping, the system employs an intentionally redundant `approved_booking_history` table. Upon final executive approval or rigid cancellation, immutable data snapshots are generated. This architectural choice enables fast querying of historical events without impacting the performance of the active transactional booking pipeline.

* 🏢 **Dynamic Facility Selection**
  Hardcoded venues are an anti-pattern. Registration and booking forms populate venue options dynamically from the source database, guaranteeing that users can only select currently active, valid facilities.

---

## 🚀 Getting Started (Local Setup)

Follow these steps to deploy the application on your local development machine.

### 1. Database Initialization
First, ensure you have MySQL 8.0 installed and running. Execute the following SQL to scaffold the target database:

```sql
CREATE DATABASE IF NOT EXISTS campusbookingdb;
USE campusbookingdb;
```

### 2. Application Configuration
Navigate to the `src/main/resources` directory and configure your `application.properties` with your localized database credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/campusbookingdb?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD

# Hibernate auto-ddl execution
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 3. Build & Run via Maven
With the database ready and credentials configured, utilize Maven to build and spin up the Spring Boot application:

```bash
mvn clean install
mvn spring-boot:run
```



## 📂 Project Structure



Here is a high-level overview of the application's architecture (Standard Spring Boot MVC):



```text

web/
├── pom.xml                                   # Maven dependencies configuration
├── src/main/java/com/campusbooking/web/
│   ├── WebApplication.java                   # Main Spring Boot entry point
│   ├── actor/                                # User entity models (Single Table Inheritance)
│   │   ├── Person.java                       # Base abstract class for all users
│   │   ├── User.java                         # Student implementation
│   │   ├── Approver.java / Dean.java etc.    # Approver role implementations
│   ├── config/                               # Settings and Security
│   │   ├── WebSecurityConfig.java            # Spring Security setup
│   │   ├── RoleBasedAuthenticationSuccessHandler.java # Route users to role dashboards
│   │   ├── DataSeeder.java                   # Initializes mock data / default roles
│   ├── controller/                           # Web endpoints and routing
│   │   ├── BookingController.java            # Main logic for creating, processing, canceling bookings
│   │   ├── RegistrationController.java       # Handles sign-ups with specific access secrets
│   │   ├── DashboardController.java          # Handles rendering of HTML pages assigned to roles
│   ├── model/                                # Core Domain Objects (JPA Entities)
│   │   ├── Booking.java                      # Central booking entity
│   │   ├── Facility.java                     # Master list of venues
│   │   ├── Status.java                       # Enum tracking approval pipeline
│   │   ├── StudentApproverMapping.java       # Relates a Student to a specific Staff Advisor / HOD
│   ├── repository/                           # Spring Data JPA Repository Interfaces
│   ├── service/                              # Core Business Logic
│   │   └── BookingService.java               # Booking creation, conflict checks, and state transitions
├── src/main/resources/
│   ├── application.properties                # Spring boot configurations (DB, secrets, pagination)
│   ├── static/                               # Static web assets 
│   │   └── style.css                         # Global vanilla CSS
│   └── templates/                            # Thymeleaf HTML Views
│       ├── login.html / register.html        # Authentication UI
│       ├── student-dashboard.html            # Main student interface
│       ├── [role]-dashboard.html             # Interfaces for Staff Advisor, HOD, Dean, Principal
│       ├── booking-form.html                 # Booking creation/edit form

```

## Tables in the DataBase
### person
```sql
CREATE TABLE person (
    dtype VARCHAR(31) NOT NULL,
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL
);
```
### facility
```sql
CREATE TABLE facility (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    type VARCHAR(255)
);
```
### bookings
```sql
CREATE TABLE bookings (
    booking_id VARCHAR(255) PRIMARY KEY,
    date DATE,
    event_description VARCHAR(255),
    event_name VARCHAR(255),
    pa_system_required BIT(1),
    remarks VARCHAR(255),
    status ENUM('APPROVED','PENDING_DEAN_APPROVAL','PENDING_HOD_APPROVAL','PENDING_PRINCIPAL_APPROVAL','PENDING_STAFF_APPROVAL','PENDING_STAFF_APPROVAL_REAPPLIED','REJECTED'),
    time_slot VARCHAR(255),
    assigned_hod_id BIGINT,
    assigned_staff_advisor_id BIGINT,
    facility_id BIGINT,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_booking_hod FOREIGN KEY (assigned_hod_id) REFERENCES person(id),
    CONSTRAINT fk_booking_staff FOREIGN KEY (assigned_staff_advisor_id) REFERENCES person(id),
    CONSTRAINT fk_booking_facility FOREIGN KEY (facility_id) REFERENCES facility(id),
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES person(id)
);
```
### student_approver_mapping
```sql
CREATE TABLE student_approver_mapping (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT,
    staff_advisor_id BIGINT,
    hod_id BIGINT,
    CONSTRAINT fk_map_student FOREIGN KEY (student_id) REFERENCES person(id) ON DELETE CASCADE,
    CONSTRAINT fk_map_staff FOREIGN KEY (staff_advisor_id) REFERENCES person(id) ON DELETE CASCADE,
    CONSTRAINT fk_map_hod FOREIGN KEY (hod_id) REFERENCES person(id) ON DELETE CASCADE
);
```
### approved_booking_history
```sql
CREATE TABLE approved_booking_history (
    history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    original_booking_id VARCHAR(255),
    user_id BIGINT,
    facility_id BIGINT,
    approver_id BIGINT,
    user_name VARCHAR(255),
    facility_name VARCHAR(255),
    approver_name VARCHAR(255),
    event_name VARCHAR(255),
    event_description VARCHAR(255),
    date DATE,
    time_slot VARCHAR(255),
    pa_system_required BIT(1),
    remarks VARCHAR(255),
    approval_timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_history_original_booking FOREIGN KEY (original_booking_id) REFERENCES bookings(booking_id) ON DELETE SET NULL,
    CONSTRAINT fk_history_user FOREIGN KEY (user_id) REFERENCES person(id) ON DELETE SET NULL,
    CONSTRAINT fk_history_facility FOREIGN KEY (facility_id) REFERENCES facility(id) ON DELETE SET NULL,
    CONSTRAINT fk_history_approver FOREIGN KEY (approver_id) REFERENCES person(id) ON DELETE SET NULL
);
```
### rejected_bookings
```sql
CREATE TABLE rejected_bookings (
    history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    original_booking_id VARCHAR(255),
    user_id BIGINT,
    facility_id BIGINT,
    approver_id BIGINT,
    user_name VARCHAR(255),
    facility_name VARCHAR(255),
    approver_name VARCHAR(255),
    event_name VARCHAR(255),
    event_description VARCHAR(255),
    date DATE,
    time_slot VARCHAR(255),
    pa_system_required BIT(1),
    remarks VARCHAR(255),
    rejection_timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rejected_original_booking FOREIGN KEY (original_booking_id) REFERENCES bookings(booking_id) ON DELETE SET NULL,
    CONSTRAINT fk_rejected_user FOREIGN KEY (user_id) REFERENCES person(id) ON DELETE SET NULL,
    CONSTRAINT fk_rejected_facility FOREIGN KEY (facility_id) REFERENCES facility(id) ON DELETE SET NULL,
    CONSTRAINT fk_rejected_approver FOREIGN KEY (approver_id) REFERENCES person(id) ON DELETE SET NULL
);
```
## 🛣️ Future Roadmap

We are aggressively expanding the application's capabilities to establish it as the definitive standard for institutional facility management. Upcoming milestones include:

* 📧 **JavaMailSender Integration:** Asynchronous, real-time email notifications detailing approval metrics and rejection rationales.
* 📄 **PDF Entry Passes:** Automated generation of cryptographically verified PDF entry passes upon Principal approval.
* 📊 **Chart.js Admin Analytics:** A visual telemetry dashboard for administrative insights (e.g., most requested venues, department load averages) securely integrated with the Historical Audit Trail.

---
## Team:
* Sreedhar Raj Kannal
* Sivapriyan R S
* Karthik Nair
---
*Architected and engineered for excellence.*
