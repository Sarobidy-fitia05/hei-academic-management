package com.example.demo.endpoint.rest.controller;

import com.example.demo.mail.Email;
import com.example.demo.mail.Mailer;
import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class GenerateAndSendPdfController {

  private final Mailer mailer;

  @GetMapping("/generate-and-send-pdf")
  @SneakyThrows
  public ResponseEntity<Map<String, String>> generateAndSend(
      @RequestParam String to, @RequestParam(defaultValue = "Test PDF") String content) {

    log.info("Generating PDF and sending to {}", to);

    File pdfFile = createSimplePdf(content);

    try {
      var email =
          new Email(
              new InternetAddress(to),
              List.of(),
              List.of(),
              "Votre document PDF",
              "<p>Bonjour,</p><p>Veuillez trouver ci-joint le document demandé.</p><p>"
                  + content
                  + "</p>",
              List.of(pdfFile));

      mailer.accept(email);

      log.info("Email with PDF successfully sent to {}", to);
      return ResponseEntity.ok(
          Map.of("status", "ok", "message", "PDF généré et email envoyé à " + to, "to", to));
    } finally {
      if (pdfFile.exists()) {
        pdfFile.delete();
      }
    }
  }

  private File createSimplePdf(String content) throws IOException {
    File tempFile = File.createTempFile("document-", ".pdf");

    try (PDDocument document = new PDDocument()) {
      PDPage page = new PDPage();
      document.addPage(page);

      try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
        stream.beginText();
        stream.setFont(PDType1Font.HELVETICA_BOLD, 16);
        stream.newLineAtOffset(50, 750);
        stream.showText("Document généré");
        stream.endText();

        stream.beginText();
        stream.setFont(PDType1Font.HELVETICA, 12);
        stream.newLineAtOffset(50, 700);

        // Découpe le contenu en lignes pour éviter les problèmes de longueur
        String[] lines = content.split("(?<=\\G.{80})");
        float y = 700;
        for (String line : lines) {
          stream.showText(line);
          y -= 20;
          stream.endText();
          stream.beginText();
          stream.setFont(PDType1Font.HELVETICA, 12);
          stream.newLineAtOffset(50, y);
        }
        stream.endText();
      }

      try (FileOutputStream fos = new FileOutputStream(tempFile)) {
        document.save(fos);
      }
    }

    return tempFile;
  }
}
