package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

import com.example.demo.endpoint.rest.dto.GraduateDTO;
import com.example.demo.entity.Program;
import com.example.demo.entity.ProgramCode;
import com.example.demo.entity.Promotion;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.GraduationListRepository;
import com.example.demo.repository.ProgramRepository;
import com.example.demo.repository.PromotionRepository;
import java.io.File;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class GraduationServiceIntegrationTest {

  @Autowired private GraduationService graduationService;
  @Autowired private GraduationListRepository graduationListRepository;
  @Autowired private DocumentRepository documentRepository;
  @Autowired private PromotionRepository promotionRepository;
  @Autowired private ProgramRepository programRepository;

  @MockBean private BucketComponent bucketComponent;

  private Integer testPromotionYear;

  @Autowired private com.example.demo.repository.GraduationRepository graduationRepository;

  @BeforeEach
  void cleanUp() {
    documentRepository.deleteAll();
    graduationRepository.deleteAll();
    graduationListRepository.deleteAll();

    testPromotionYear = 2099;
    if (!promotionRepository.existsByYear(testPromotionYear)) {
      Promotion promotion = new Promotion();
      promotion.setYear(testPromotionYear);
      promotion.setGroupPrefix("TEST");
      promotionRepository.save(promotion);
    }

    if (programRepository.findByCode(ProgramCode.TN).isEmpty()) {
      Program program = new Program();
      program.setCode(ProgramCode.TN);
      program.setLabel("Telecommunications");
      programRepository.save(program);
    }
  }

  @Test
  void generate_classeParMoyenneDecroissanteEtUploadeLeXlsx() throws Exception {
    List<GraduateDTO> raw =
        List.of(
            new GraduateDTO(UUID.randomUUID(), "Tsiory", "Rakoto", "TN", 12.0, null),
            new GraduateDTO(UUID.randomUUID(), "Jean", "Rasoa", "TN", 16.5, null));

    List<GraduateDTO> ranked =
        graduationService.generate(testPromotionYear, ProgramCode.TN, 2026, raw);

    assertThat(ranked.get(0).ranking()).isEqualTo(1);
    assertThat(ranked.get(0).generalAverage()).isEqualTo(16.5);
    verify(bucketComponent).upload(any(File.class), anyString());
  }

  @Test
  void generate_leve404SiPromotionIntrouvable() {
    List<GraduateDTO> raw = List.of();

    org.assertj.core.api.Assertions.assertThatThrownBy(
            () -> graduationService.generate(1900, ProgramCode.TN, 2026, raw))
        .isInstanceOf(org.springframework.web.server.ResponseStatusException.class)
        .hasMessageContaining("404");
  }

  @Test
  void generate_leve404SiProgrammeIntrouvable() {
    List<GraduateDTO> raw = List.of();

    org.assertj.core.api.Assertions.assertThatThrownBy(
            () -> graduationService.generate(testPromotionYear, ProgramCode.EL, 2026, raw))
        .isInstanceOf(org.springframework.web.server.ResponseStatusException.class)
        .hasMessageContaining("404");
  }

  @Test
  void downloadXlsx_retourneLeFichierSiDocumentExiste() throws Exception {
    List<GraduateDTO> raw =
        List.of(new GraduateDTO(UUID.randomUUID(), "Tsiory", "Rakoto", "TN", 14.0, null));

    graduationService.generate(testPromotionYear, ProgramCode.TN, 2026, raw);

    var graduationList = graduationListRepository.findAll().get(0);
    File fakeFile = File.createTempFile("graduates-dl-test-", ".xlsx");
    org.mockito.Mockito.when(bucketComponent.download(org.mockito.ArgumentMatchers.anyString()))
        .thenReturn(fakeFile);

    File result = graduationService.downloadXlsx(graduationList.getId());

    assertThat(result).isEqualTo(fakeFile);
  }

  @Test
  void downloadXlsx_leve404SiAucunDocument() {
    org.assertj.core.api.Assertions.assertThatThrownBy(
            () -> graduationService.downloadXlsx(UUID.randomUUID()))
        .isInstanceOf(org.springframework.web.server.ResponseStatusException.class)
        .hasMessageContaining("404");
  }
}
