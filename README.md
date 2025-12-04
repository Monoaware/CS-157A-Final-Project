# Library Management System — CS-157A Final Project
## 📚 Project Overview

This is a library database system that allows users to keep track of book information, library members & their account statuses, borrow/return records, and library fines.

This is a full-stack Library Database Management System built with:

- **Java** + **Spring Boot** for backend  
- **Gradle** as build system  
- **PostgreSQL** (or any relational DB) for data storage  
- **Thymeleaf**, HTML/CSS/JavaScript for frontend  
- Support for: books, copies, members, loans, holds, staff & member roles  

It allows library staff and members to:

- Manage catalog titles and copies (add, edit, delete)  
- Track loans / returns  
- Place and manage holds when books are unavailable  
- Enforce permissions (staff vs members)  

---

## 🧰 Tech Stack & Architecture

### Backend  
- Java 17+  
- Spring Boot (Web, JDBC, Thymeleaf)  
- Gradle build system  
- DAO layer using `JdbcTemplate`  

### Database  
- PostgreSQL (or similar SQL DB)  
- SQL schema with tables: `titles`, `copies`, `users`, `loans`, `holds`, etc.  
- Foreign-key constraints (with cascading deletes where appropriate)  

### Frontend / Presentation Layer  
- Thymeleaf HTML templates  
- CSS for styling  
- JavaScript (fetch API) for dynamic UI behavior  

### Architecture Style  
Three-Tier Architecture:  
1. **Presentation Layer** — Thymeleaf + HTML/CSS/JS  
2. **Application / Business Logic Layer** — Spring Boot service & controller classes  
3. **Data Access Layer** — DAOs using JDBC + database  

---

## 🚀 Getting Started

### Prerequisites  
- Java JDK 17+  
- PostgreSQL (or other SQL database)  
- Git  
- Gradle

### Setup Steps  

1. Clone the repository:

    ```
    git clone https://github.com/Monoaware/CS-157A-Final-Project.git
    cd CS-157A-Final-Project
    ```

2. Configure database credentials  
   Update `src/main/resources/application.properties` (or appropriate config) with your DB URL, username, and password.  

3. Prepare the database  
   Run the SQL schema and setup scripts (or let your application auto-initialize tables if configured).  

4. Build with Gradle:

    ```bash
    ./gradlew build
    ```

5. Run the application:

    ```bash
    ./gradlew run
    ```

6. Open in browser:

    ```
    http://localhost:8080
    ```

---

## 🔧 Dependencies (Gradle)

Key dependencies:

```groovy
implementation 'org.springframework.boot:spring-boot-starter-web'
implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
implementation 'org.springframework.boot:spring-boot-starter-jdbc'
runtimeOnly 'org.postgresql:postgresql'
developmentOnly 'org.springframework.boot:spring-boot-devtools'
