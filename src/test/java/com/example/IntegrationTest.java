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

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
class IntegrationTest {

  @BeforeAll
  static void beforeAll(WebApplicationContext webApplicationContext, @Autowired ProjectRepository projectRepository) {
    projectRepository.deleteAll();
    MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
    RestAssuredMockMvc.mockMvc(mockMvc);
    RestAssured.baseURI = "http://localhost:8080/project-service/api/v1";
  }

  String projectName = "Ultimate Tic-Tac-Toe";
  String userToAdd = "user";
  String newUser = "newuser";

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
