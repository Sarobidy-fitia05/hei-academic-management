package com.example.demo.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.endpoint.rest.dto.GraduateDTO;
import com.example.demo.entity.ProgramCode;
import com.example.demo.service.GraduationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GraduationControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private GraduationService graduationService;

  @Test
  @WithMockUser(roles = "ADMIN")
  void generate_retourneLaListeClasseeEnJson() throws Exception {
    UUID studentId = UUID.randomUUID();
    List<GraduateDTO> raw =
        List.of(new GraduateDTO(studentId, "Tsiory", "Rakoto", "TN", 15.0, null));
    List<GraduateDTO> ranked =
        List.of(new GraduateDTO(studentId, "Tsiory", "Rakoto", "TN", 15.0, 1));

    when(graduationService.generate(eq(2026), eq(ProgramCode.TN), eq(2026), any()))
        .thenReturn(ranked);

    mockMvc
        .perform(
            post("/api/admin/graduates/generate")
                .param("promotionYear", "2026")
                .param("programCode", "TN")
                .param("graduationYear", "2026")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(raw)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].ranking").value(1));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void downloadXlsx_retourneLeFichierXlsx() throws Exception {
    UUID graduationListId = UUID.randomUUID();
    File fakeFile = File.createTempFile("graduates-test-", ".xlsx");
    Files.write(fakeFile.toPath(), "fake-xlsx-content".getBytes());

    when(graduationService.downloadXlsx(graduationListId)).thenReturn(fakeFile);

    mockMvc
        .perform(
            get("/api/admin/graduates/export")
                .param("graduationListId", graduationListId.toString()))
        .andExpect(status().isOk());
  }

  @Test
  void endpoints_refusesSansAuthentification() throws Exception {
    mockMvc
        .perform(
            get("/api/admin/graduates/export")
                .param("graduationListId", UUID.randomUUID().toString()))
        .andExpect(status().isUnauthorized());
  }
}
