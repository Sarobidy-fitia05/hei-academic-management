package com.example.demo.service;

import com.example.demo.endpoint.rest.dto.AnnualResultDTO;
import com.example.demo.endpoint.rest.dto.GradeDTO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

@Service
public class TranscriptPdfGenerator {

  private static final float MARGIN = 50;
  private static final float LINE_HEIGHT = 18;
  private static final PDFont TITLE_FONT = PDType1Font.HELVETICA_BOLD;
  private static final PDFont TEXT_FONT = PDType1Font.HELVETICA;

  public byte[] generate(AnnualResultDTO data) throws IOException {
    try (PDDocument document = new PDDocument()) {
      PDPage page = new PDPage();
      document.addPage(page);

      float y = page.getMediaBox().getHeight() - MARGIN;

      try (PDPageContentStream content = new PDPageContentStream(document, page)) {
        y = writeLine(content, TITLE_FONT, 16, MARGIN, y, "Releve de notes");
        y -= LINE_HEIGHT;

        y =
            writeLine(
                content,
                TEXT_FONT,
                12,
                MARGIN,
                y,
                "Etudiant : " + data.studentFirstName() + " " + data.studentLastName());
        y =
            writeLine(
                content, TEXT_FONT, 12, MARGIN, y, "Annee academique : " + data.academicYear());
        y -= LINE_HEIGHT;

        y = writeLine(content, TITLE_FONT, 12, MARGIN, y, "Matiere");
        content.beginText();
        content.setFont(TEXT_FONT, 12);
        content.newLineAtOffset(300, y + LINE_HEIGHT);
        content.showText("Credits");
        content.newLineAtOffset(80, 0);
        content.showText("Note");
        content.endText();
        y -= LINE_HEIGHT;

        for (GradeDTO grade : data.grades()) {
          y = writeGradeLine(content, y, grade);
        }

        y -= LINE_HEIGHT;
        y = writeLine(content, TITLE_FONT, 12, MARGIN, y, "Total credits : " + data.totalCredits());
        writeLine(
            content,
            TITLE_FONT,
            12,
            MARGIN,
            y,
            "Moyenne annuelle : " + String.format("%.2f", data.annualAverage()));
      }

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      document.save(out);
      return out.toByteArray();
    }
  }

  private float writeGradeLine(PDPageContentStream content, float y, GradeDTO grade)
      throws IOException {
    content.beginText();
    content.setFont(TEXT_FONT, 11);
    content.newLineAtOffset(MARGIN, y);
    content.showText(grade.courseReference() + " - " + grade.courseTitle());
    content.newLineAtOffset(300 - MARGIN, 0);
    content.showText(String.valueOf(grade.credits()));
    content.newLineAtOffset(80, 0);
    content.showText(String.format("%.2f", grade.grade()));
    content.endText();
    return y - LINE_HEIGHT;
  }

  private float writeLine(
      PDPageContentStream content, PDFont font, int size, float x, float y, String text)
      throws IOException {
    content.beginText();
    content.setFont(font, size);
    content.newLineAtOffset(x, y);
    content.showText(text);
    content.endText();
    return y - LINE_HEIGHT;
  }
}
