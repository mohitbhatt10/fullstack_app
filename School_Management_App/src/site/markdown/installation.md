# Installation Guide

## Prerequisites

Before installing the School Management System, ensure you have the following components installed:

### Required Software
- **Java 17** or higher
- **Maven 3.6+**
- **MySQL 8.0+**
- **Git** (for cloning the repository)

### Optional (for Production)
- **Docker** (for containerization)
- **Kubernetes** (for orchestration)
- **AWS CLI** (for cloud deployment)

## Installation Steps

### 1. Clone the Repository
```bash
git clone https://github.com/mohitbhatt10/fullstack_app.git
cd fullstack_app/School_Management_App
```

### 2. Database Setup

#### MySQL Database Creation
```sql
CREATE DATABASE school_management;
CREATE USER 'school_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON school_management.* TO 'school_user'@'localhost';
FLUSH PRIVILEGES;
```

#### Configure Database Connection
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/school_management
spring.datasource.username=school_user
spring.datasource.password=password
```

### 3. Build the Application
```bash
mvn clean install
```

### 4. Run the Application

#### Development Mode
```bash
mvn spring-boot:run
```

#### Production Mode
```bash
java -jar target/school-management-system-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 5. Access the Application
- **URL**: http://localhost:8080
- **Default Admin**: admin@school.com / admin123
- **Default Teacher**: teacher@school.com / teacher123
- **Default Student**: student@school.com / student123

## Docker Installation

### 1. Build Docker Image
```bash
docker build -t school-management-app .
```

### 2. Run with Docker Compose
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: school_management
    ports:
      - "3306:3306"
  
  app:
    image: school-management-app
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DATABASE_URL: jdbc:mysql://mysql:3306/school_management
      DATABASE_USERNAME: root
      DATABASE_PASSWORD: root
    ports:
      - "8080:8080"
    depends_on:
      - mysql
```

## Kubernetes Deployment

### 1. Create Secrets
```bash
kubectl create secret generic db-credentials \
  --from-literal=DATABASE_URL="jdbc:mysql://your-rds-endpoint:3306/school_management" \
  --from-literal=DATABASE_USERNAME="root" \
  --from-literal=DATABASE_PASSWORD="your-password"
```

### 2. Deploy to Kubernetes
```bash
kubectl apply -f k8s.yaml
```

### 3. Access the Service
```bash
kubectl get service school-management-app-service
```

## Configuration

### Application Profiles
- **dev**: Development environment with H2 database
- **test**: Testing environment with in-memory database
- **prod**: Production environment with MySQL

### Environment Variables
```bash
export SPRING_PROFILES_ACTIVE=prod
export DATABASE_URL="jdbc:mysql://localhost:3306/school_management"
export DATABASE_USERNAME="school_user"
export DATABASE_PASSWORD="password"
export MAIL_HOST="smtp.gmail.com"
export MAIL_USERNAME="your-email@gmail.com"
export MAIL_PASSWORD="your-app-password"
```

## Troubleshooting

### Common Issues

1. **Port Already in Use**
   ```bash
   # Change port in application.properties
   server.port=8081
   ```

2. **Database Connection Failed**
   - Check MySQL service is running
   - Verify database credentials
   - Ensure database exists

3. **Maven Build Fails**
   ```bash
   mvn dependency:resolve
   mvn clean compile
   ```

4. **Docker Build Issues**
   ```bash
   docker system prune
   docker build --no-cache -t school-management-app .
   ```

### Logs and Debugging
```bash
# View application logs
tail -f logs/school-app.log

# Docker logs
docker logs school-management-app

# Kubernetes logs
kubectl logs deployment/school-management-app
```

## Performance Tuning

### JVM Options
```bash
java -Xmx2g -Xms1g -XX:+UseG1GC -jar target/school-management-system-0.0.1-SNAPSHOT.jar
```

### Database Optimization
```sql
-- Index optimization
CREATE INDEX idx_student_roll_number ON students(roll_number);
CREATE INDEX idx_attendance_date ON attendance(attendance_date);
```

For additional support, please refer to the [User Manual](user-manual.html) or create an issue in the GitHub repository.
