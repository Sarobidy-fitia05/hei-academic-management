package com.example.demo.service.event;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.endpoint.event.model.SendTranscriptEmailRequested;
import com.example.demo.mail.Mailer;
import com.example.demo.service.TranscriptService;
import java.io.File;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SendTranscriptEmailRequestedServiceTest {

  @Mock private TranscriptService transcriptService;
  @Mock private Mailer mailer;

  @InjectMocks private SendTranscriptEmailRequestedService subject;

  @Test
  void accept_ok() throws Exception {
    // 1. Données de test
    UUID studentId = UUID.randomUUID();
    String recipientEmail = "test@example.com";
    File tempPdf = File.createTempFile("transcript-test-", ".pdf");

    // 2. Mocks
    when(transcriptService.downloadPdf(studentId)).thenReturn(tempPdf);
    SendTranscriptEmailRequested event =
        new SendTranscriptEmailRequested(studentId, recipientEmail);

    // 3. Exécution
    subject.accept(event);

    // 4. Vérifications
    verify(transcriptService).downloadPdf(studentId);
    verify(mailer).accept(any());
    assertFalse(tempPdf.exists(), "Le fichier temporaire PDF doit être supprimé après l'envoi");
  }
}
