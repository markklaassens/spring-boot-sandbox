package com.example.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.api.controller.ProjectController;
import com.example.services.interfaces.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ProjectController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(CorsConfig.class)
@MockBean(ProjectService.class)
class CorsConfigTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldAcceptGetRequest() throws Exception {
    mockMvc.perform(options("/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Origin", "https://test-origin.nl")
            .header("Access-Control-Request-Method", "GET")
        )
        .andExpect(status().isOk())
        .andExpect(header().string("Access-Control-Allow-Origin", "*"))
        .andExpect(header().string("Access-Control-Allow-Methods", "GET,POST"));
  }

  @Test
  void shouldRejectDeleteRequest() throws Exception {
    mockMvc.perform(options("/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Origin", "https://test-origin.nl")
            .header("Access-Control-Request-Method", "DELETE")
        )
        .andExpect(status().isForbidden());
  }
}
