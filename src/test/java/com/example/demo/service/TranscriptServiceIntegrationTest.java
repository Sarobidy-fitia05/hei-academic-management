package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.endpoint.rest.dto.AnnualResultDTO;
import com.example.demo.endpoint.rest.dto.GradeDTO;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.transcript.TranscriptRepository;
import java.io.File;
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
class TranscriptServiceIntegrationTest {

  @Autowired private TranscriptService transcriptService;

  @Autowired private TranscriptRepository transcriptRepository;

  @MockBean private BucketComponent bucketComponent;

  @BeforeEach
  void cleanUp() {
    transcriptRepository.deleteAll();
  }

  @Test
  void generate_uploadeLePdfEtEnregistreLeReleve() throws Exception {
    AnnualResultDTO data =
        new AnnualResultDTO(
            10L,
            "Tsiory",
            "Rakoto",
            2026,
            List.of(new GradeDTO("INFO101", "Algorithmique", 5, 15.5)),
            15.5,
            5);

    var response = transcriptService.generate(data);

    assertThat(response.studentId()).isEqualTo(10L);
    assertThat(response.status()).isEqualTo("COMPLETE");
    verify(bucketComponent).upload(any(File.class), anyString());
  }

  @Test
  void getStatus_renvoie404SiAucunReleve() {
    assertThatThrownBy(() -> transcriptService.getStatus(999L))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("404");
  }

  @Test
  void downloadPdf_appelleLeBucketAvecLaBonneCle() throws Exception {
    AnnualResultDTO data =
        new AnnualResultDTO(
            20L,
            "Jean",
            "Rasoa",
            2026,
            List.of(new GradeDTO("INFO102", "Bases de donnees", 4, 12.0)),
            12.0,
            4);
    transcriptService.generate(data);

    byte[] fakePdf = "%PDF-fake".getBytes();
    when(bucketComponent.download("transcripts/20-2026.pdf")).thenReturn(fakePdf);

    byte[] result = transcriptService.downloadPdf(20L);

    assertThat(result).isEqualTo(fakePdf);
  }
}
