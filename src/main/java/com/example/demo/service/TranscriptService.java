package com.example.demo.service;

import com.example.demo.endpoint.rest.dto.AnnualResultDTO;
import com.example.demo.endpoint.rest.dto.TranscriptStatusResponse;
import com.example.demo.entity.Document;
import com.example.demo.entity.Transcript;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.TranscriptRepository;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TranscriptService {

  private final TranscriptRepository transcriptRepository;
  private final DocumentRepository documentRepository;
  private final TranscriptPdfGenerator pdfGenerator;
  private final BucketComponent bucketComponent;

  public TranscriptService(
      TranscriptRepository transcriptRepository,
      DocumentRepository documentRepository,
      TranscriptPdfGenerator pdfGenerator,
      BucketComponent bucketComponent) {
    this.transcriptRepository = transcriptRepository;
    this.documentRepository = documentRepository;
    this.pdfGenerator = pdfGenerator;
    this.bucketComponent = bucketComponent;
  }

  public TranscriptStatusResponse generate(AnnualResultDTO resultData) throws IOException {
    byte[] pdfBytes = pdfGenerator.generate(resultData);

    Transcript transcript = new Transcript();
    transcript.setType("YEAR");
    transcript.setStatus("COMPLETE");
    transcript.setGeneralAverage(resultData.annualAverage());
    transcript.setTotalCredits(resultData.totalCredits());
    transcript.setGeneratedAt(LocalDateTime.now());
    transcriptRepository.save(transcript);

    String bucketKey = "transcripts/" + transcript.getId() + ".pdf";
    File tempFile = File.createTempFile("transcript-", ".pdf");
    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
      fos.write(pdfBytes);
    }
    bucketComponent.upload(tempFile, bucketKey);

    Document document = new Document();
    document.setTranscript(transcript);
    document.setDocumentType("PDF");
    document.setS3Key(bucketKey);
    document.setCreatedAt(LocalDateTime.now());
    documentRepository.save(document);

    return toResponse(transcript);
  }

  public TranscriptStatusResponse getStatus(UUID studentId) {
    Transcript transcript =
        transcriptRepository.findByStudentIdOrderByGeneratedAtDesc(studentId).stream()
            .findFirst()
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Aucun releve pour cet etudiant"));
    return toResponse(transcript);
  }

  public File downloadPdf(UUID studentId) {
    Transcript transcript =
        transcriptRepository.findByStudentIdOrderByGeneratedAtDesc(studentId).stream()
            .findFirst()
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Aucun releve pour cet etudiant"));

    Document document =
        documentRepository.findByTranscriptId(transcript.getId()).stream()
            .findFirst()
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PDF indisponible"));

    return bucketComponent.download(document.getS3Key());
  }

  public String presignDownloadUrl(UUID studentId) {
    Transcript transcript =
        transcriptRepository.findByStudentIdOrderByGeneratedAtDesc(studentId).stream()
            .findFirst()
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Aucun releve pour cet etudiant"));

    Document document =
        documentRepository.findByTranscriptId(transcript.getId()).stream()
            .findFirst()
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PDF indisponible"));

    return bucketComponent.presign(document.getS3Key(), Duration.ofMinutes(15)).toString();
  }

  private TranscriptStatusResponse toResponse(Transcript transcript) {
    return new TranscriptStatusResponse(
        transcript.getId().toString(), transcript.getStatus(), transcript.getGeneratedAt());
  }
}
