# School Management System

## Overview

The **School Management System** is a comprehensive web application built using **Spring Boot 3.x** and **Java 17**. It provides a complete solution for managing educational institutions with features for student management, teacher administration, course management, attendance tracking, and academic reporting.

## Key Features

### 🎓 Student Management
- Student registration and profile management
- Academic record tracking
- Attendance monitoring
- Performance analytics

### 👨‍🏫 Teacher Administration
- Teacher profile management
- Course assignment
- Grade management
- Attendance tracking

### 📚 Course Management
- Course creation and scheduling
- Curriculum management
- Resource allocation

### 📊 Reports & Analytics
- Attendance reports
- Performance analytics
- Academic progress tracking
- Export capabilities (PDF, Excel)

### 🔐 Security Features
- Role-based access control
- Secure authentication
- Data protection

## Technology Stack

- **Backend**: Spring Boot 3.2.5, Spring Security, Spring Data JPA
- **Frontend**: Thymeleaf, Bootstrap 5, JavaScript
- **Database**: MySQL (Production), H2 (Testing)
- **Build Tool**: Maven 3.x
- **Java Version**: 17
- **Containerization**: Docker
- **Deployment**: Kubernetes (EKS)

## Getting Started

1. **Prerequisites**
   - Java 17 or higher
   - Maven 3.6+
   - MySQL 8.0+

2. **Clone the Repository**
   ```bash
   git clone https://github.com/mohitbhatt10/fullstack_app.git
   cd fullstack_app/School_Management_App
   ```

3. **Build the Project**
   ```bash
   mvn clean install
   ```

4. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

## Project Structure

```
School_Management_App/
├── src/
│   ├── main/
│   │   ├── java/com/school/
│   │   │   ├── controller/     # REST Controllers
│   │   │   ├── service/        # Business Logic
│   │   │   ├── repository/     # Data Access Layer
│   │   │   ├── entity/         # JPA Entities
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   └── config/         # Configuration Classes
│   │   └── resources/
│   │       ├── templates/      # Thymeleaf Templates
│   │       ├── static/         # CSS, JS, Images
│   │       └── application.properties
│   └── test/                   # Unit & Integration Tests
├── target/                     # Build Output
├── Dockerfile                  # Container Configuration
├── k8s.yaml                   # Kubernetes Deployment
└── pom.xml                    # Maven Configuration
```

## Contributors

- **Development Team**: School Management Team
- **Project Lead**: Mohit Bhatt
- **Architecture**: Spring Boot Microservices

## License

This project is licensed under the MIT License - see the LICENSE file for details.
