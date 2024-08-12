# Spring Boot Sandbox Repository

Welcome to my Spring Boot sandbox repository, where I experiment with Spring Boot and other Java functionalities. 
Feel free to browse the code, experiment with it, and provide feedback.

## Contents

This repository serves as a sandbox Spring Boot project where I explore various Spring Boot features, 
such as dependency management, RESTful services, database integration, and more. It’s designed to be 
a starting point for further experiments and learning. The application uses Hibernate for creating the database tables 
and clears them on each startup of the application. Mandatory records are loaded after startup using the `import.sql` file in `src/main/resources`.

## Getting Started

To get started with this repository, follow these steps:

### Prerequisites

- Ensure you have Java Development Kit (JDK) installed.
- [Maven](https://maven.apache.org/install.html) (`mvn`) installed for dependency management and build processes.
- A code editor or IDE that supports Java, such as [IntelliJ IDEA](https://www.jetbrains.com/idea/) or [Eclipse](https://www.eclipse.org/).
- A Docker engine running.

### Setup Instructions

- Copy the `template.env` file and rename it to `.env`. Set your local password, leaving the username as it is.
- Run `docker compose up -d` to start the database.
- Optionally run `mvn versions:display-property-updates` and `mvn versions:display-parent-updates` to check for dependency updates.
- Run the Spring Boot application with the 'local' profile from your IDE.
- Visit the [Swagger UI](http://localhost:8080/project-service/api/v1/swagger-ui/index.html) to explore and test the API endpoints.

### Future Improvements

- Add collaborators, competitors and admins to projects.
- Add Spring Security for securing endpoints.
- Introduce asynchronous database actions using Reactor.
- Implement event sourcing using Kafka.
