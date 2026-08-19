package com.example.demo.service;

import com.example.demo.endpoint.rest.dto.GraduateDTO;
import com.example.demo.entity.Document;
import com.example.demo.entity.GraduationList;
import com.example.demo.entity.Program;
import com.example.demo.entity.ProgramCode;
import com.example.demo.entity.Promotion;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.GraduationListRepository;
import com.example.demo.repository.ProgramRepository;
import com.example.demo.repository.PromotionRepository;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GraduationService {

  private final GraduationListRepository graduationListRepository;
  private final DocumentRepository documentRepository;
  private final PromotionRepository promotionRepository;
  private final ProgramRepository programRepository;
  private final GraduateXlsxGenerator xlsxGenerator;
  private final BucketComponent bucketComponent;

  public GraduationService(
      GraduationListRepository graduationListRepository,
      DocumentRepository documentRepository,
      PromotionRepository promotionRepository,
      ProgramRepository programRepository,
      GraduateXlsxGenerator xlsxGenerator,
      BucketComponent bucketComponent) {
    this.graduationListRepository = graduationListRepository;
    this.documentRepository = documentRepository;
    this.promotionRepository = promotionRepository;
    this.programRepository = programRepository;
    this.xlsxGenerator = xlsxGenerator;
    this.bucketComponent = bucketComponent;
  }

  public List<GraduateDTO> generate(
      Integer promotionYear,
      ProgramCode programCode,
      Integer graduationYear,
      List<GraduateDTO> rawGraduates)
      throws IOException {
    Promotion promotion =
        promotionRepository
            .findByYear(promotionYear)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Promotion introuvable"));
    Program program =
        programRepository
            .findByCode(programCode)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Programme introuvable"));

    List<GraduateDTO> ranked = rank(rawGraduates);

    GraduationList graduationList = new GraduationList();
    graduationList.setPromotion(promotion);
    graduationList.setProgram(program);
    graduationList.setGraduationYear(graduationYear);
    graduationList.setGeneratedAt(LocalDateTime.now());
    graduationListRepository.save(graduationList);

    byte[] xlsxBytes = xlsxGenerator.generate(ranked);
    String bucketKey = "graduation/" + graduationList.getId() + ".xlsx";
    File tempFile = File.createTempFile("graduation-", ".xlsx");
    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
      fos.write(xlsxBytes);
    }
    bucketComponent.upload(tempFile, bucketKey);

    Document document = new Document();
    document.setGraduationList(graduationList);
    document.setDocumentType("XLSX");
    document.setS3Key(bucketKey);
    document.setCreatedAt(LocalDateTime.now());
    documentRepository.save(document);

    return ranked;
  }

  public File downloadXlsx(UUID graduationListId) {
    Document document =
        documentRepository.findByGraduationListId(graduationListId).stream()
            .findFirst()
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Aucun fichier XLSX pour cette liste"));

    return bucketComponent.download(document.getS3Key());
  }

  private List<GraduateDTO> rank(List<GraduateDTO> rawGraduates) {
    List<GraduateDTO> sorted =
        rawGraduates.stream()
            .sorted(Comparator.comparingDouble(GraduateDTO::generalAverage).reversed())
            .toList();

    return IntStream.range(0, sorted.size()).mapToObj(i -> withRank(sorted.get(i), i + 1)).toList();
  }

  private GraduateDTO withRank(GraduateDTO graduate, int rank) {
    return new GraduateDTO(
        graduate.studentId(),
        graduate.firstName(),
        graduate.lastName(),
        graduate.programCode(),
        graduate.generalAverage(),
        rank);
  }
}
