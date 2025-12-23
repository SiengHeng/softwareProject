# Course Enrollment System

This is a Spring Boot web application for a course enrollment system. It uses Spring Web for the web layer, Spring Data JPA for database access, Spring Security for security, and Thymeleaf for the view layer. The database is PostgreSQL, and it also integrates with Supabase.

## File Structure

Here is a breakdown of the significant files and directories in this project:

### Build and Configuration

*   **`build.gradle`**: The build script for the project using Gradle. It declares project dependencies (like Spring Boot, PostgreSQL, Supabase, Thymeleaf, Spring Security), configures plugins, and manages the build lifecycle.
*   **`.gitattributes`**: Specifies attributes for pathnames in the Git repository, for example to enforce consistent line endings.
*   **`.gitignore`**: Tells Git which files or directories to intentionally ignore and not track (e.g., build output, temporary files).
*   **`gradlew` (and `gradlew.bat`)**: Gradle Wrapper scripts that allow running Gradle tasks without a global Gradle installation, ensuring a consistent build environment.
*   **`gradle/wrapper/gradle-wrapper.jar`**: The executable JAR file for the Gradle Wrapper.
*   **`gradle/wrapper/gradle-wrapper.properties`**: Configuration for the Gradle Wrapper, specifying the version of Gradle to be used.
*   **`settings.gradle`**: For a multi-project Gradle build, this file defines which subprojects are part of the build.

### Spring Boot Application

*   **`src/main/java/com/example/demo/DemoApplication.java`**: The main class that bootstraps and runs the Spring Boot application.
*   **`src/main/resources/application.properties`**: Holds configuration settings for the Spring Boot application, such as database connection URLs, credentials, server port, and other environment-specific properties.
*   **`src/main/resources/schema.sql`**: A SQL script that defines the database schema. It contains `CREATE TABLE` and other DDL statements to set up the database tables.

### Configuration Classes

*   **`src/main/java/com/example/demo/config/DataLoader.java`**: Contains logic to pre-populate the database with initial data (e.g., default users, roles, or courses) when the application starts.
*   **`src/main/java/com/example/demo/config/SecurityConfig.java`**: Configures the security settings for the web application using Spring Security. It defines authentication mechanisms and authorization rules.
*   **`src/main/java/com/example/demo/config/SupabaseKtConfig.java`**: Sets up and configures the Supabase client within the Spring Boot application, allowing it to interact with a Supabase backend.

### Application Layers

#### Controller

*   **`src/main/java/com/example/demo/controller/HomeController.java`**: A Spring MVC controller that handles web requests. It maps specific URLs to methods that prepare data and return view names to be rendered by Thymeleaf.

#### Model (JPA Entities)

These classes are Java objects that represent tables in the database.

*   **`src/main/java/com/example/demo/model/Classroom.java`**: Represents the `Classroom` table.
*   **`src/main/java/com/example/demo/model/Course.java`**: Represents the `Course` table.
*   **`src/main/java/com/example/demo/model/Enrollment.java`**: Represents the `Enrollment` table, linking students to courses.
*   **`src/main/java/com/example/demo/model/Lecturer.java`**: Represents the `Lecturer` table.
*   **`src/main/java/com/example/demo/model/Role.java`**: Represents the `Role` table, used for defining user roles for authorization.
*   **`src/main/java/com/example/demo/model/Schedule.java`**: Represents the `Schedule` table.
*   **`src/main/java/com/example/demo/model/Student.java`**: Represents the `Student` table.
*   **`src/main/java/com/example/demo/model/User.java`**: Represents the central `User` table, containing common user information.

#### Repository (Data Access Layer)

These are Spring Data JPA repository interfaces that provide methods for performing database operations (create, read, update, delete) on entities.

*   **`src/main/java/com/example/demo/repository/ClassroomRepository.java`**
*   **`src/main/java/com/example/demo/repository/CourseRepository.java`**
*   **`src/main/java/com/example/demo/repository/EnrollmentRepository.java`**
*   **`src/main/java/com/example/demo/repository/LecturerRepository.java`**
*   **`src/main/java/com/example/demo/repository/RoleRepository.java`**
*   **`src/main/java/com/example/demo/repository/ScheduleRepository.java`**
*   **`src/main/java/com/example/demo/repository/StudentRepository.java`**
*   **`src/main/java/com/example/demo/repository/UserRepository.java`**

#### Service (Business Logic)

*   **`src/main/java/com/example/demo/service/UserDetailsServiceImpl.java`**: Implements Spring Security's `UserDetailsService`. It's responsible for loading user-specific data during the authentication process.

### Frontend and Static Resources

*   **`src/main/resources/templates/index.html`**: A Thymeleaf template file that renders the main index or home page.
*   **`src/main/resources/templates/login.html`**: A Thymeleaf template file that renders the login page.

### JavaScript Project

*   **`package.json`**: Lists the JavaScript project's metadata and its direct dependencies.
*   **`package-lock.json`**: Locks down the exact versions of the entire dependency tree for the JavaScript project.
*   **`node_modules/`**: Contains all the third-party JavaScript dependencies installed by npm or yarn.
