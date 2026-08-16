package com.example.demo.service.event;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.endpoint.event.model.SendTranscriptEmailRequested;
import com.example.demo.endpoint.rest.dto.AnnualResultDTO;
import com.example.demo.endpoint.rest.dto.GradeDTO;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.mail.Email;
import com.example.demo.mail.Mailer;
import com.example.demo.service.TranscriptService;
import com.example.demo.transcript.TranscriptRepository;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
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

  @MockBean private BucketComponent bucketComponent;

  @MockBean private Mailer mailer;

  @BeforeEach
  void cleanUp() {
    transcriptRepository.deleteAll();
  }

  @Test
  void accept_envoieLemailAvecLePdfEnPieceJointe() throws Exception {
    AnnualResultDTO data =
        new AnnualResultDTO(
            30L,
            "Tsiory",
            "Rakoto",
            2026,
            List.of(new GradeDTO("INFO101", "Algorithmique", 5, 15.5)),
            15.5,
            5);
    transcriptService.generate(data);

    File fakePdfFile = File.createTempFile("fake-transcript-", ".pdf");
    Files.write(fakePdfFile.toPath(), "%PDF-fake".getBytes());
    when(bucketComponent.download("transcripts/30-2026.pdf")).thenReturn(fakePdfFile);

    var event = new SendTranscriptEmailRequested(30L, "eleve@example.com");
    service.accept(event);

    verify(mailer).accept(any(Email.class));
  }

  @Test
  void accept_echoueSiAucunReleveTrouve() {
    var event = new SendTranscriptEmailRequested(999L, "personne@example.com");

    assertThatThrownBy(() -> service.accept(event))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("404");
  }
}
