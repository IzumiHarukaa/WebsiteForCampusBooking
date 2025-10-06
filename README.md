# WebsiteForCampusBooking
This project was created with the help of Gemini

Campus Event Booking System
1. Introduction
This project is a role-based web application for managing event bookings on a college campus. It digitizes the entire process, from a student's initial request to the final approval from the Principal, through a clear, multi-level approval workflow.

The system is designed to provide a secure and efficient experience for different user roles, ensuring that each user only sees the information and actions relevant to their position.

2. Core Features
User Registration & Secure Login: Users can register for an account with a specific role (Student, Staff Advisor, HOD, etc.). Passwords are securely hashed, and the login system is managed by Spring Security.

Role-Based Dashboards: After logging in, users are redirected to a dashboard tailored to their role:

Student Dashboard: Students can submit new event booking requests and view the status (Pending, Approved, Rejected) of their past submissions.

Approver Dashboards: Each level of approver (Staff Advisor, HOD, Dean, Principal) has a dedicated dashboard that shows only the booking requests currently awaiting their specific approval.

Multi-Level Approval Workflow: A booking request submitted by a student must be approved sequentially:

Staff Advisor

HOD

Dean

Principal

A rejection at any level stops the process. An approval moves the request to the next level.

Dynamic Frontend: The user interface is built with Thymeleaf and styled with a modern dark theme, providing a clean and responsive experience.

3. Technologies Used
Backend:

Java 25

Spring Boot 3: Core framework for the application.

Spring Security: For handling authentication (login) and authorization (role-based access).

Spring Data JPA (Hibernate): For communicating with the database and mapping Java objects to database tables.

Database:

MySQL

Frontend:

Thymeleaf: A modern server-side Java template engine for creating dynamic HTML pages.

HTML5 & CSS3: For structuring and styling the web pages.

Build Tool:

Maven: For managing project dependencies and building the application.

4. How to Run the Application
Database Setup: Ensure you have a MySQL server running. Create a database named campus_booking. The application will automatically create the necessary tables on its first run.

Configuration: Open the src/main/resources/application.properties file and update the spring.datasource.username and spring.datasource.password fields to match your MySQL credentials.

Run the App: Execute the main method in the WebApplication.java file from your IDE, or run the command mvnw spring-boot:run in your project's root directory.

Access: Open your web browser and navigate to http://localhost:8080.

5. Contributors & Acknowledgements
This project was a collaborative effort.

Team Members(Mains):

Sreedhar

Karthik Nair

Sivapriyan R S

Special Thanks:
We would also like to acknowledge the significant assistance provided by Google's Gemini, which served as an AI collaborator. Gemini was instrumental in generating foundational code, debugging complex errors (such as issues with Spring Security, Maven builds, and Thymeleaf templates), and offering architectural guidance throughout the development process.
