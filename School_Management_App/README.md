# School Management System

A comprehensive School Management System built with Spring Boot.

## Features

- User management (Students, Teachers, Admins)
- Course management
- Enrollment management
- Grade tracking
- Schedule management
- Bulk Student Import from Excel

## Technology Stack

- Java 17
- Spring Boot 3.0
- Spring Security
- Spring Data JPA
- Thymeleaf
- Bootstrap 5
- H2 Database (for development)
- Apache POI (for Excel processing)

## Getting Started

1. Clone the repository
2. Run `mvn clean install` to build the application
3. Run `mvn spring-boot:run` to start the application
4. Access the application at http://localhost:8080

## Default Credentials

- Admin: admin/admin
- Teacher: teacher/password
- Student: student/password

## Student Import Feature

The system allows administrators to import multiple students at once using an Excel file. Here's how it works:

1. Navigate to the Admin dashboard
2. Click on "Import Students" in the sidebar or on the Students list page
3. Download the template Excel file
4. Fill in student details in the template
5. Upload the completed Excel file
6. Review the import results

The Excel file should contain the following columns:
- First Name
- Last Name
- Email
- Username
- Password
- Roll Number
- Department (CSE, ECE, ME, CE, EEE, IT)
- Semester (1-8)

Notes:
- The maximum file size for upload is 10MB
- Only .xlsx and .xls formats are supported
- The first row is treated as a header and skipped during import
