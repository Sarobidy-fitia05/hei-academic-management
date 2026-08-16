package com.example.demo.service.event;

import com.example.demo.endpoint.event.model.SendTranscriptEmailRequested;
import com.example.demo.mail.Email;
import com.example.demo.mail.Mailer;
import com.example.demo.service.TranscriptService;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.stereotype.Service;

@Service
public class SendTranscriptEmailRequestedService implements Consumer<SendTranscriptEmailRequested> {

  private final TranscriptService transcriptService;
  private final Mailer mailer;

  public SendTranscriptEmailRequestedService(TranscriptService transcriptService, Mailer mailer) {
    this.transcriptService = transcriptService;
    this.mailer = mailer;
  }

  @Override
  public void accept(SendTranscriptEmailRequested event) {
    try {
      File pdfFile = transcriptService.downloadPdf(event.getStudentId());
      InternetAddress recipient = new InternetAddress(event.getRecipientEmail());

      Email email =
          new Email(
              recipient,
              List.of(),
              List.of(),
              "Votre releve de notes",
              "<p>Bonjour,</p><p>Veuillez trouver ci-joint votre releve de notes.</p>",
              List.of(pdfFile));

      mailer.accept(email);
    } catch (AddressException e) {
      throw new RuntimeException(e);
    }
  }
}
