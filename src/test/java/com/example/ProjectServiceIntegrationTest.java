package com.example;

import static com.example.config.ApplicationConstants.COLLABORATIVE;
import static com.example.config.ApplicationConstants.COMPETITIVE;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.is;

import com.example.persistence.repositories.ProjectRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class ProjectServiceIntegrationTest {

  @BeforeAll
  static void beforeAll(WebApplicationContext webApplicationContext, @Autowired ProjectRepository projectRepository) {
    projectRepository.deleteAll();
    MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    RestAssuredMockMvc.mockMvc(mockMvc);
    RestAssured.baseURI = "http://localhost:8080/project-service/api/v1";
  }

  @Test
  public void shouldPostAndGetProjects() {
    String projectName = "Ultimate Tic-Tac-Toe";

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
                "projectName": "Tetris Blocks",
                "projectDescription": "Project for competing and solving the puzzle Tetris Blocks.",
                "projectType": "%s"
            }
            """.formatted(COMPETITIVE))
        .when()
        .post("/projects")
        .then()
        .statusCode(201)
        .body("size()", is(3))
        .body("projectName", is("Tetris Blocks"))
        .body("projectDescription", is("Project for competing and solving the puzzle Tetris Blocks."))
        .body("projectType", is(COMPETITIVE));

    given()
        .contentType(ContentType.JSON)
        .when()
        .get("/projects")
        .then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("[0].projectName", is(projectName))
        .body("[0].projectDescription", is("Project for collaborating and developing the game Ultimate Tic-Tac-Toe."))
        .body("[0].projectType", is(COLLABORATIVE))
        .body("[1].projectName", is("Tetris Blocks"))
        .body("[1].projectDescription", is("Project for competing and solving the puzzle Tetris Blocks."))
        .body("[1].projectType", is(COMPETITIVE));
  }
}
