package com.example;

import static com.example.TestConstants.CREATOR;
import static com.example.config.ApplicationConstants.COLLABORATIVE;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.example.persistence.repositories.ProjectRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Integration test class for testing the project and user management endpoints.
 *
 * <p>This class is annotated with {@code @SpringBootTest} to load the application context and run
 * tests with a defined port. The {@code @TestMethodOrder(OrderAnnotation.class)} annotation ensures that the test methods
 * are executed in a specific order, which is crucial given the interdependencies between the tests.
 *
 * <p>The test methods are executed in the following order:
 *
 * <ol>
 *   <li>{@link #shouldPostAddUsersAndGetCreatorProjectsToProjectsAsCreator()} - Creates a new project,
 *       adds users to it, and retrieves the project details as the creator. This test is executed first
 *       because it sets up the initial project data required for subsequent tests.</li>
 *
 *   <li>{@link #shouldGetProjectsWithoutRole()} - Retrieves the list of projects without any user role.
 *       This test is executed second to verify that the project created in the first test is visible
 *       without needing specific roles.</li>
 *
 *   <li>{@link #shouldAddUserWithoutRole()} - Adds a new user without requiring any roles. This test is
 *       executed third to set up a new user that will be added to the project in the next test.</li>
 *
 *   <li>{@link #shouldAddNewUserWithRoleCreator()} - As the creator, adds the newly created user to the
 *       project. This test is executed last as it builds on the setup from the first and third tests.</li>
 * </ol>
 *
 * <p>The order of tests is critical to ensure that the state established in one test is available
 * for the next. For example, the project and users created in the first and third tests are necessary
 * for the operations tested in the fourth method.
 *
 * <p>The test environment uses a "test" profile to isolate this suite from production configurations,
 * and it leverages {@code MockMvc} and {@code RestAssuredMockMvc} to simulate HTTPS requests.
 *
 * <p>Note: The base URI for RestAssured is set to 'https', indicating that these tests are executed over HTTPS.
 */
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
class IntegrationTest {

  private final String projectName = "Ultimate Tic-Tac-Toe";
  private final String newUser = "newuser";

  @BeforeAll
  static void beforeAll(
      final WebApplicationContext webApplicationContext,
      @Autowired final ProjectRepository projectRepository
  ) {
    projectRepository.deleteAll();
    MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
    RestAssuredMockMvc.mockMvc(mockMvc);
    RestAssured.baseURI = "https://localhost:8443/spring-boot-sandbox/api/v1";
  }

  @Test
  @WithMockUser(username = "creator", roles = CREATOR)
  @Order(1)
  public void shouldPostAddUsersAndGetCreatorProjectsToProjectsAsCreator() {
    given()
        .contentType(ContentType.JSON)
        .body("""
            {
                "projectName": "%s",
                "projectDescription": "Project for collaborating and developing the game Ultimate Tic-Tac-Toe.",
                "projectType": "%s"
            }
            """.formatted(projectName, COLLABORATIVE))
        .when()
        .post("/projects")
        .then()
        .statusCode(201)
        .body("size()", is(3))
        .body("projectName", is(projectName))
        .body("projectDescription", is("Project for collaborating and developing the game Ultimate Tic-Tac-Toe."))
        .body("projectType", is(COLLABORATIVE));

    val userToAdd = "user";
    given()
        .contentType(ContentType.JSON)
        .body("""
            {
                "projectName": "%s",
                "usernames": ["%s"]
            }
            """.formatted(projectName, userToAdd))
        .when()
        .put("/projects")
        .then()
        .statusCode(200)
        .body("size()", is(3))
        .body("projectName", is(projectName))
        .body("addedUsers[0]", is(userToAdd))
        .body("addedUsers", hasSize(1))
        .body("notAddedUsers", hasSize(0));

    given()
        .contentType(ContentType.JSON)
        .when()
        .get("/users/creator-projects")
        .then()
        .body("size()", is(1))
        .body("[0].projectName", is(projectName))
        .body("[0].projectDescription", is("Project for collaborating and developing the game Ultimate Tic-Tac-Toe."))
        .body("[0].projectType", is(COLLABORATIVE));

  }

  @Test
  @Order(2)
  void shouldGetProjectsWithoutRole() {

    given()
        .contentType(ContentType.JSON)
        .when()
        .get("/projects")
        .then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("[0].projectName", is(projectName))
        .body("[0].projectDescription", is("Project for collaborating and developing the game Ultimate Tic-Tac-Toe."))
        .body("[0].projectType", is(COLLABORATIVE));
  }

  @Test
  @Order(3)
  void shouldAddUserWithoutRole() {

    given()
        .contentType(ContentType.JSON)
        .body("""
            {
                "username": "%s",
                "userPassword": "test123"
            }
            """.formatted(newUser))
        .when()
        .post("/users")
        .then()
        .statusCode(201);
  }

  @Test
  @WithMockUser(username = "creator", roles = CREATOR)
  @Order(4)
  void shouldAddNewUserWithRoleCreator() {
    given()
        .contentType(ContentType.JSON)
        .body("""
            {
                "projectName": "%s",
                "usernames": ["%s"]
            }
            """.formatted(projectName, newUser))
        .when()
        .put("/projects")
        .then()
        .statusCode(200)
        .body("size()", is(3))
        .body("projectName", is(projectName))
        .body("addedUsers[0]", is(newUser))
        .body("addedUsers", hasSize(1))
        .body("notAddedUsers", hasSize(0));
  }
}
