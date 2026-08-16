package com.example.demo.service;

import com.example.demo.endpoint.rest.dto.AnnualResultDTO;
import com.example.demo.endpoint.rest.dto.TranscriptStatusResponse;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.transcript.Transcript;
import com.example.demo.transcript.TranscriptRepository;
import com.example.demo.transcript.TranscriptStatus;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TranscriptService {

  private final TranscriptRepository transcriptRepository;
  private final TranscriptPdfGenerator pdfGenerator;
  private final BucketComponent bucketComponent;

  public TranscriptService(
      TranscriptRepository transcriptRepository,
      TranscriptPdfGenerator pdfGenerator,
      BucketComponent bucketComponent) {
    this.transcriptRepository = transcriptRepository;
    this.pdfGenerator = pdfGenerator;
    this.bucketComponent = bucketComponent;
  }

  public TranscriptStatusResponse generate(AnnualResultDTO resultData) throws IOException {
    byte[] pdfBytes = pdfGenerator.generate(resultData);

    String bucketKey =
        "transcripts/" + resultData.studentId() + "-" + resultData.academicYear() + ".pdf";
    File tempFile = File.createTempFile("transcript-", ".pdf");
    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
      fos.write(pdfBytes);
    }
    bucketComponent.upload(tempFile, bucketKey);

    Transcript transcript =
        transcriptRepository
            .findByStudentIdAndAcademicYear(resultData.studentId(), resultData.academicYear())
            .orElse(
                new Transcript(
                    resultData.studentId(),
                    resultData.academicYear(),
                    TranscriptStatus.COMPLETE,
                    bucketKey));
    transcript.setStatus(TranscriptStatus.COMPLETE);
    transcript.setPdfS3Key(bucketKey);
    transcript.setGeneratedAt(java.time.LocalDateTime.now());
    transcriptRepository.save(transcript);

    return toResponse(transcript);
  }

  public TranscriptStatusResponse getStatus(Long studentId) {
    Transcript transcript =
        transcriptRepository
            .findTopByStudentIdOrderByAcademicYearDesc(studentId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Aucun releve pour cet etudiant"));
    return toResponse(transcript);
  }

  public File downloadPdf(Long studentId) {
    Transcript transcript =
        transcriptRepository
            .findTopByStudentIdOrderByAcademicYearDesc(studentId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Aucun releve pour cet etudiant"));

    if (transcript.getPdfS3Key() == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Releve incomplet, PDF indisponible");
    }

    return bucketComponent.download(transcript.getPdfS3Key());
  }

  public String presignDownloadUrl(Long studentId) {
    Transcript transcript =
        transcriptRepository
            .findTopByStudentIdOrderByAcademicYearDesc(studentId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Aucun releve pour cet etudiant"));

    return bucketComponent.presign(transcript.getPdfS3Key(), Duration.ofMinutes(15)).toString();
  }

  private TranscriptStatusResponse toResponse(Transcript transcript) {
    return new TranscriptStatusResponse(
        transcript.getStudentId(), transcript.getStatus().name(), transcript.getGeneratedAt());
  }
}
