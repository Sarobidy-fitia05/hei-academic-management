package com.example.demo.endpoint.rest.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.demo.service.GraduationService;
import java.io.File;
import java.nio.file.Files;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GraduatesViewControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private GraduationService graduationService;

  @Test
  @WithMockUser(roles = "ADMIN")
  void showGraduatesPage_retourneLaVueGraduates() throws Exception {
    mockMvc
        .perform(get("/graduates"))
        .andExpect(status().isOk())
        .andExpect(view().name("graduates"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void downloadXlsx_retourneLeFichierAvecContentDisposition() throws Exception {
    UUID graduationListId = UUID.randomUUID();
    File fakeFile = File.createTempFile("graduates-view-test-", ".xlsx");
    Files.write(fakeFile.toPath(), "fake-xlsx-content".getBytes());

    when(graduationService.downloadXlsx(graduationListId)).thenReturn(fakeFile);

    mockMvc
        .perform(get("/graduates/export").param("graduationListId", graduationListId.toString()))
        .andExpect(status().isOk())
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                .string(
                    "Content-Disposition", org.hamcrest.Matchers.containsString("diplomes.xlsx")));
  }
}
