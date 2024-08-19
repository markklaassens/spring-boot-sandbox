package com.example;

import static com.example.TestConstants.CREATOR;
import static com.example.TestConstants.PROJECT;
import static com.example.TestConstants.PROJECT2;
import static com.example.TestConstants.USER;
import static com.example.config.ApplicationConstants.COLLABORATIVE;
import static com.example.config.ApplicationConstants.COMPETITIVE;
import static com.example.config.ApplicationConstants.ROLE_USER;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.example.exceptions.ProjectNotFoundException;
import com.example.exceptions.UserNotFoundException;
import com.example.persistence.entities.UserRole;
import com.example.persistence.repositories.ProjectRepository;
import com.example.persistence.repositories.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class IntegrationTest {

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private UserRepository userRepository;

  @BeforeAll
  static void beforeAll(WebApplicationContext webApplicationContext, @Autowired ProjectRepository projectRepository) {
    projectRepository.deleteAll();
    MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
    RestAssuredMockMvc.mockMvc(mockMvc);
    RestAssured.baseURI = "http://localhost:8080/project-service/api/v1";
  }

  @BeforeEach
  void setUp() {
    projectRepository.deleteAll();
  }

  @Test
  @WithMockUser(username = "creator", roles = CREATOR)
  @Transactional
  public void shouldPostAddUsersAndGetCreatorProjectsToProjectsAsCreator() {
    String projectName = "Ultimate Tic-Tac-Toe";
    String userToAdd = "user";

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
        .body("addedUsers[0]", is("user"))
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

    val foundProject = projectRepository.findByProjectName(projectName).orElseThrow(
        () -> new ProjectNotFoundException("Project with name '%s' not found in database.".formatted(projectName)));
    assertEquals(projectName, foundProject.getProjectName());
    assertTrue(foundProject.getProjectUsers().contains(userRepository.findByUsername(userToAdd).orElseThrow(
        () -> new UserNotFoundException("User with username '%s' not found in project user list.".formatted(userToAdd)))
    ));
    assertEquals(1, foundProject.getProjectUsers().size());
  }

  @Test
  @WithMockUser(roles = USER)
  void shouldGetProjectsAsUser() {
    projectRepository.save(PROJECT);
    projectRepository.save(PROJECT2);

    given()
        .contentType(ContentType.JSON)
        .when()
        .get("/projects")
        .then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("[0].projectName", is("Ultimate Tic-Tac-Toe"))
        .body("[0].projectDescription", is("Project for collaborating and developing the game Ultimate Tic-Tac-Toe."))
        .body("[0].projectType", is(COLLABORATIVE))
        .body("[1].projectName", is("Tetris Blocks"))
        .body("[1].projectDescription", is("Project for competing and solving the puzzle Tetris Blocks."))
        .body("[1].projectType", is(COMPETITIVE));
  }

  @Test
  @WithMockUser()
  void shouldAddUsers() {
    String username = "newuser";

    val result = given()
        .contentType(ContentType.JSON)
        .body("""
            {
                "username": "%s",
                "userPassword": "test123"
            }
            """.formatted(username))
        .when()
        .post("/users")
        .then()
        .statusCode(201).extract().response();

    assertEquals(username, result.body().print());
    val addedUser = userRepository.findByUsername("newuser").orElseThrow(() ->
        new UserNotFoundException("User with username '%s' not found in database.".formatted(username))
    );
    assertThat(addedUser.getUserRoles().stream().map(UserRole::getUserRoleValue).toList()).contains(ROLE_USER);
  }

  @Test
  @WithMockUser(roles = CREATOR)
  void shouldNotGetProjectsAsCreator() {
    given()
        .contentType(ContentType.JSON)
        .when()
        .get("/projects")
        .then()
        .statusCode(403);
  }

  @Test
  @WithMockUser(roles = USER)
  void shouldNotPostProjectsAsUser() {
    given()
        .contentType(ContentType.JSON)
        .body("""
            {
                "projectName": "Ultimate Tic-Tac-Toe",
                "projectDescription": "Project for collaborating and developing the game Ultimate Tic-Tac-Toe.",
                "projectType": "%s"
            }
            """.formatted(COLLABORATIVE))
        .when()
        .post("/projects")
        .then()
        .statusCode(403);
  }

  @Test
  @WithMockUser(roles = USER)
  void shouldNotUpdateProjectUsersAsUser() {
    given()
        .contentType(ContentType.JSON)
        .body("""
            {
                "projectName": "Ultimate Tic-Tac-Toe",
                "usernames": ["user"]
            }
            """)
        .when()
        .put("/projects")
        .then()
        .statusCode(403);
  }

  @Test
  @WithMockUser(roles = USER)
  void shouldNotSearchCreatorProjectsUsersAsUser() {
    given()
        .contentType(ContentType.JSON)
        .when()
        .get("/users/creator-projects")
        .then()
        .statusCode(403);
  }
}
