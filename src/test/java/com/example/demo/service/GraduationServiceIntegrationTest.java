package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.endpoint.rest.dto.GraduateDTO;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.graduation.GraduationExportRepository;
import com.example.demo.graduation.Parcours;
import java.io.File;
import java.nio.file.Files;
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
class GraduationServiceIntegrationTest {

  @Autowired private GraduationService graduationService;

  @Autowired private GraduationExportRepository graduationExportRepository;

  @MockBean private BucketComponent bucketComponent;

  @BeforeEach
  void cleanUp() {
    graduationExportRepository.deleteAll();
  }

  @Test
  void generate_classeParMoyenneDecroissanteEtUploadeLeXlsx() throws Exception {
    List<GraduateDTO> raw =
        List.of(
            new GraduateDTO(1L, "Tsiory", "Rakoto", Parcours.TN, 12.0, null),
            new GraduateDTO(2L, "Jean", "Rasoa", Parcours.TN, 16.5, null));

    List<GraduateDTO> ranked = graduationService.generate(100L, Parcours.TN, raw);

    assertThat(ranked.get(0).studentId()).isEqualTo(2L);
    assertThat(ranked.get(0).ranking()).isEqualTo(1);
    assertThat(ranked.get(1).studentId()).isEqualTo(1L);
    assertThat(ranked.get(1).ranking()).isEqualTo(2);
    verify(bucketComponent).upload(any(File.class), anyString());
  }

  @Test
  void downloadXlsx_renvoie404SiAucunExport() {
    assertThatThrownBy(() -> graduationService.downloadXlsx(999L, Parcours.EL))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("404");
  }

  @Test
  void downloadXlsx_appelleLeBucketAvecLaBonneCle() throws Exception {
    List<GraduateDTO> raw = List.of(new GraduateDTO(5L, "Aina", "Rabe", Parcours.EL, 13.0, null));
    graduationService.generate(200L, Parcours.EL, raw);

    File fakeXlsxFile = File.createTempFile("fake-graduates-", ".xlsx");
    Files.write(fakeXlsxFile.toPath(), "fake-xlsx".getBytes());
    when(bucketComponent.download("graduates/200-EL.xlsx")).thenReturn(fakeXlsxFile);

    File result = graduationService.downloadXlsx(200L, Parcours.EL);

    assertThat(result).isEqualTo(fakeXlsxFile);
  }
}
