package com.example.demo.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.endpoint.event.EventProducer;
import com.example.demo.endpoint.event.model.SendTranscriptEmailRequested;
import com.example.demo.endpoint.rest.dto.TranscriptStatusResponse;
import com.example.demo.service.TranscriptService;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
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
class TranscriptControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private TranscriptService transcriptService;

  @MockBean private EventProducer<SendTranscriptEmailRequested> eventProducer;

  @Test
  @WithMockUser(roles = "STUDENT")
  void getStatus_retourneLeStatutDuReleve() throws Exception {
    UUID studentId = UUID.randomUUID();
    when(transcriptService.getStatus(studentId))
        .thenReturn(
            new TranscriptStatusResponse(studentId.toString(), "COMPLETE", LocalDateTime.now()));

    mockMvc
        .perform(get("/api/student/transcripts/" + studentId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("COMPLETE"));
  }

  @Test
  @WithMockUser(roles = "STUDENT")
  void download_retourneLePdf() throws Exception {
    UUID studentId = UUID.randomUUID();
    File fakePdf = File.createTempFile("transcript-test-", ".pdf");
    Files.write(fakePdf.toPath(), "%PDF-fake".getBytes());

    when(transcriptService.downloadPdf(studentId)).thenReturn(fakePdf);

    mockMvc
        .perform(get("/api/student/transcripts/" + studentId + "/download"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(roles = "STUDENT")
  void sendByEmail_publieUnEvenementEtRetourne202() throws Exception {
    UUID studentId = UUID.randomUUID();

    mockMvc
        .perform(
            post("/api/student/transcripts/" + studentId + "/send")
                .param("email", "eleve@example.com"))
        .andExpect(status().isAccepted());

    verify(eventProducer).accept(any(List.class));
  }

  @Test
  void endpoints_refusesSansAuthentification() throws Exception {
    mockMvc
        .perform(get("/api/student/transcripts/" + UUID.randomUUID()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(roles = "STUDENT")
  void getStatus_renvoie404SiAucunReleve() throws Exception {
    UUID studentId = UUID.randomUUID();
    when(transcriptService.getStatus(studentId))
        .thenThrow(
            new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND));

    mockMvc.perform(get("/api/student/transcripts/" + studentId)).andExpect(status().isNotFound());
  }
}
