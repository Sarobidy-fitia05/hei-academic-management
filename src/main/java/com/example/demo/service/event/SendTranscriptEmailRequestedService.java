package com.example.demo.service.event;

import com.example.demo.endpoint.event.model.SendTranscriptEmailRequested;
import com.example.demo.mail.Email;
import com.example.demo.mail.Mailer;
import com.example.demo.service.TranscriptService;
import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SendTranscriptEmailRequestedService implements Consumer<SendTranscriptEmailRequested> {

  private final TranscriptService transcriptService;
  private final Mailer mailer;

  @Override
  @SneakyThrows
  public void accept(SendTranscriptEmailRequested event) {
    File pdfFile = null;
    try {
      // getStudentId() est déjà un UUID, pas besoin de UUID.fromString()
      pdfFile = transcriptService.downloadPdf(event.getStudentId());

      InternetAddress recipient = new InternetAddress(event.getRecipientEmail());

      Email email =
          new Email(
              recipient,
              List.of(),
              List.of(),
              "HEI - Votre relevé de notes",
              "<p>Bonjour,</p><p>Veuillez trouver ci-joint votre relevé de notes officiel.</p>",
              List.of(pdfFile));

      mailer.accept(email);
    } finally {
      if (pdfFile != null && pdfFile.exists()) {
        pdfFile.delete();
      }
    }
  }
}
