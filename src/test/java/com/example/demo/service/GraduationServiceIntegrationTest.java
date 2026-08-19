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
import com.example.demo.repository.GraduationRepository;
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
    List<GraduateDTO> raw = List.of(
            new GraduateDTO(UUID.randomUUID(), "Tsiory", "Rakoto", "TN", 12.0, null),
            new GraduateDTO(UUID.randomUUID(), "Jean", "Rasoa", "TN", 16.5, null));

    List<GraduateDTO> ranked = graduationService.generate(testPromotionYear, ProgramCode.TN, 2026, raw);

    assertThat(ranked.get(0).ranking()).isEqualTo(1);
    assertThat(ranked.get(0).generalAverage()).isEqualTo(16.5);
    verify(bucketComponent).upload(any(File.class), anyString());
  }
}