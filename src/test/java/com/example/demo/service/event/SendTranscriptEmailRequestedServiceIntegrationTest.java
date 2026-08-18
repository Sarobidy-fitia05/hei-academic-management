package com.example.demo.service.event;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.endpoint.event.model.SendTranscriptEmailRequested;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.mail.Mailer;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.TranscriptRepository;
import com.example.demo.service.TranscriptService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@ActiveProfiles("test")
class SendTranscriptEmailRequestedServiceIntegrationTest {

  @Autowired private SendTranscriptEmailRequestedService service;
  @Autowired private TranscriptService transcriptService;
  @Autowired private TranscriptRepository transcriptRepository;
  @Autowired private DocumentRepository documentRepository;

  @MockBean private BucketComponent bucketComponent;
  @MockBean private Mailer mailer;

  @BeforeEach
  void cleanUp() {
    documentRepository.deleteAll();
    transcriptRepository.deleteAll();
  }

  @Test
  void accept_echoueSiAucunReleveTrouve() {
    var event = new SendTranscriptEmailRequested(UUID.randomUUID(), "personne@example.com");

    assertThatThrownBy(() -> service.accept(event))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("404");
  }
}
