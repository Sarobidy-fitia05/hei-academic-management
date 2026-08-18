package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

import com.example.demo.endpoint.rest.dto.AnnualResultDTO;
import com.example.demo.endpoint.rest.dto.GradeDTO;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.TranscriptRepository;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TranscriptServiceIntegrationTest {

  @Autowired private TranscriptService transcriptService;

  @Autowired private TranscriptRepository transcriptRepository;

  @Autowired private DocumentRepository documentRepository;

  @MockBean private BucketComponent bucketComponent;

  @BeforeEach
  void cleanUp() {
    documentRepository.deleteAll();
    transcriptRepository.deleteAll();
  }

  @Test
  void generate_uploadeLePdfEtEnregistreLeReleve() throws Exception {
    AnnualResultDTO data =
        new AnnualResultDTO(
            "e2000000-0000-4000-8000-000000000005",
            "Tsiory",
            "Rakoto",
            2026,
            List.of(new GradeDTO("INFO101", "Algorithmique", 5, 15.5)),
            15.5,
            5);

    var response = transcriptService.generate(data);

    assertThat(response.status()).isEqualTo("COMPLETE");
    verify(bucketComponent).upload(any(File.class), anyString());
  }
}
