package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.entity.Document;
import com.example.demo.entity.Transcript;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.TranscriptRepository;
import java.io.File;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class TranscriptServiceUnitTest {

  private TranscriptRepository transcriptRepository;
  private DocumentRepository documentRepository;
  private BucketComponent bucketComponent;
  private TranscriptService transcriptService;

  @BeforeEach
  void setUp() {
    transcriptRepository = mock(TranscriptRepository.class);
    documentRepository = mock(DocumentRepository.class);
    bucketComponent = mock(BucketComponent.class);
    TranscriptPdfGenerator pdfGenerator = mock(TranscriptPdfGenerator.class);
    transcriptService =
        new TranscriptService(
            transcriptRepository, documentRepository, pdfGenerator, bucketComponent);
  }

  @Test
  void getStatus_retourneLeStatutSiTranscriptExiste() {
    UUID studentId = UUID.randomUUID();
    Transcript transcript = new Transcript();
    transcript.setId(UUID.randomUUID());
    transcript.setStatus("COMPLETE");

    when(transcriptRepository.findByStudentIdOrderByGeneratedAtDesc(studentId))
        .thenReturn(List.of(transcript));

    var response = transcriptService.getStatus(studentId);

    assertThat(response.status()).isEqualTo("COMPLETE");
  }

  @Test
  void getStatus_renvoie404SiAucunTranscript() {
    UUID studentId = UUID.randomUUID();
    when(transcriptRepository.findByStudentIdOrderByGeneratedAtDesc(studentId))
        .thenReturn(List.of());

    assertThatThrownBy(() -> transcriptService.getStatus(studentId))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("404");
  }

  @Test
  void downloadPdf_retourneLeFichierSiTranscriptEtDocumentExistent() throws Exception {
    UUID studentId = UUID.randomUUID();
    Transcript transcript = new Transcript();
    transcript.setId(UUID.randomUUID());

    Document document = new Document();
    document.setS3Key("transcripts/abc.pdf");

    File fakeFile = File.createTempFile("transcript-test-", ".pdf");

    when(transcriptRepository.findByStudentIdOrderByGeneratedAtDesc(studentId))
        .thenReturn(List.of(transcript));
    when(documentRepository.findByTranscriptId(transcript.getId())).thenReturn(List.of(document));
    when(bucketComponent.download("transcripts/abc.pdf")).thenReturn(fakeFile);

    File result = transcriptService.downloadPdf(studentId);

    assertThat(result).isEqualTo(fakeFile);
  }

  @Test
  void downloadPdf_renvoie404SiAucunTranscript() {
    UUID studentId = UUID.randomUUID();
    when(transcriptRepository.findByStudentIdOrderByGeneratedAtDesc(studentId))
        .thenReturn(List.of());

    assertThatThrownBy(() -> transcriptService.downloadPdf(studentId))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("404");
  }

  @Test
  void downloadPdf_renvoie404SiAucunDocument() {
    UUID studentId = UUID.randomUUID();
    Transcript transcript = new Transcript();
    transcript.setId(UUID.randomUUID());

    when(transcriptRepository.findByStudentIdOrderByGeneratedAtDesc(studentId))
        .thenReturn(List.of(transcript));
    when(documentRepository.findByTranscriptId(transcript.getId())).thenReturn(List.of());

    assertThatThrownBy(() -> transcriptService.downloadPdf(studentId))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("404");
  }

  @Test
  void presignDownloadUrl_retourneLUrlSiTranscriptEtDocumentExistent() throws Exception {
    UUID studentId = UUID.randomUUID();
    Transcript transcript = new Transcript();
    transcript.setId(UUID.randomUUID());

    Document document = new Document();
    document.setS3Key("transcripts/abc.pdf");

    when(transcriptRepository.findByStudentIdOrderByGeneratedAtDesc(studentId))
        .thenReturn(List.of(transcript));
    when(documentRepository.findByTranscriptId(transcript.getId())).thenReturn(List.of(document));
    when(bucketComponent.presign(eq("transcripts/abc.pdf"), any(Duration.class)))
        .thenReturn(URI.create("https://bucket.example.com/transcripts/abc.pdf").toURL());

    String url = transcriptService.presignDownloadUrl(studentId);

    assertThat(url).contains("transcripts/abc.pdf");
  }

  @Test
  void presignDownloadUrl_renvoie404SiAucunTranscript() {
    UUID studentId = UUID.randomUUID();
    when(transcriptRepository.findByStudentIdOrderByGeneratedAtDesc(studentId))
        .thenReturn(List.of());

    assertThatThrownBy(() -> transcriptService.presignDownloadUrl(studentId))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("404");
  }

  @Test
  void presignDownloadUrl_renvoie404SiAucunDocument() {
    UUID studentId = UUID.randomUUID();
    Transcript transcript = new Transcript();
    transcript.setId(UUID.randomUUID());

    when(transcriptRepository.findByStudentIdOrderByGeneratedAtDesc(studentId))
        .thenReturn(List.of(transcript));
    when(documentRepository.findByTranscriptId(transcript.getId())).thenReturn(List.of());

    assertThatThrownBy(() -> transcriptService.presignDownloadUrl(studentId))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("404");
  }
}
