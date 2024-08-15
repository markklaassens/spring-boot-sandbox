# Spring Boot Sandbox Repository

Welcome to my Spring Boot sandbox repository, where I experiment with Spring Boot and other Java functionalities. 
Feel free to browse the code, experiment with it, and provide feedback.

## Contents

This repository serves as a sandbox Spring Boot project where I explore various Spring Boot features, 
such as dependency management, RESTful services, database integration, and more. It’s designed to be 
a starting point for further experiments and learning. The application uses Hibernate for creating the database tables 
and clears them on each startup of the application. Essential records are loaded after startup using the SQL scripts file in `src/main/resources`.
The `DatabaseInitializer` class executes these scripts once the application is ready. 

By default, the application includes three users: a creator, a user, and an admin. The creator has the CREATOR role, 
the user has the USER role, and the admin has both roles. These roles determine access to the endpoints defined in the ProjectServiceController.

## Getting Started

To get started with this repository, follow these steps:

### Prerequisites

- Ensure you have Java Development Kit (JDK) installed.
- [Maven](https://maven.apache.org/install.html) (`mvn`) installed for dependency management and build processes.
- A Docker engine running.
- Preferably a code editor or IDE that supports Java, such as [IntelliJ IDEA](https://www.jetbrains.com/idea/).


### Setup Instructions

- Clone the repository to your local machine, preferably using the default develop branch, as it includes the latest approved features.
- Copy the `template.env` file and rename it to `.env`. Place it in the same directory. Set your local password while leaving the username unchanged.
- Run `docker compose up -d` to start the database.
- In `src/main/resources`, locate `insert_userstemplate.txt` copy it and rename it to `insert_users.sql`. Place it in the same directory.
- Use the `PasswordEncoder` utility located in `src/test/tools/` to generate the encoded passwords:
    - Enter your desired password and run the `PasswordEncoder` utility class manually from your IDE.
    - Note: The `PasswordEncoder` utility is in the test directory, so it won’t be included in the compiled application or scanned by Spring Boot.
- Replace the placeholders `<REPLACE_WITH_ENCODED_CREATOR_PASSWORD>`, `<REPLACE_WITH_ENCODED_USER_PASSWORD>` and `<REPLACE_WITH_ENCODED_ADMIN_PASSWORD>` in your SQL script (`insert_users.sql`) with the encoded values you generated.
- (Optional) run `mvn versions:display-property-updates` and `mvn versions:display-parent-updates` to check for dependency updates and apply those in `pom.xml`.
- Run the Spring Boot application with the 'local' profile from your IDE.
- (Alternative) If you don't use IntelliJ IDEA you can run the application with: `mvn spring-boot:run -Dspring-boot.run.profiles=local` from the root directory.
- Visit the [Swagger UI](http://localhost:8080/project-service/api/v1/swagger-ui/index.html).
- Log in with the username and password you set by clicking the white and green Authorize button on the right side of the Swagger UI.
- By default, the POST endpoint is accessible only to the creator, while the GET endpoint is accessible only to the user. The admin can access both endpoints.

### Troubleshooting

#### General Run Configuration Errors

The error described below is not project-specific but can occur when the IDE’s run configuration is not set up correctly.

If the following error occurs:
````
2024-08-15T17:25:59.670+02:00  WARN 64199 --- [project-service] [MI TCP Accept-0] sun.rmi.transport.tcp                    : RMI TCP Accept-0: accept loop for ServerSocket[addr=0.0.0.0/0.0.0.0,localport=58947] throws

java.io.IOException: The server sockets created using the LocalRMIServerSocketFactory only accept connections from clients running on the host where the RMI remote objects have been exported.
````
##### Update Your Run Configuration:

In your IDE (like IntelliJ IDEA or Eclipse), go to your application's run configuration settings.
Look for Modify Options and select Add VM Options.
Add the following line as VM option:
`````-Djava.rmi.server.hostname=localhost`````

##### Why This Fix Works
The RMI service uses the LocalRMIServerSocketFactory to restrict connections to the local host. 
If the JVM does not know which specific interface to bind to, it defaults to 0.0.0.0 (all interfaces), 
causing this error. By explicitly setting localhost, you ensure the service binds only to the loopback address (127.0.0.1), 
matching the connection restrictions of the LocalRMIServerSocketFactory.

## Future Improvements

- Automatically add the creator to the project upon creation.
- Enable an endpoint for adding users to the database.
- Enable an endpoint for adding users to the project.

