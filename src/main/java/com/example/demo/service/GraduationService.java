package com.example.demo.service;

import com.example.demo.endpoint.rest.dto.GraduateDTO;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.graduation.GraduationExport;
import com.example.demo.graduation.GraduationExportRepository;
import com.example.demo.graduation.Parcours;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GraduationService {

  private final GraduationExportRepository graduationExportRepository;
  private final GraduateXlsxGenerator xlsxGenerator;
  private final BucketComponent bucketComponent;

  public GraduationService(
      GraduationExportRepository graduationExportRepository,
      GraduateXlsxGenerator xlsxGenerator,
      BucketComponent bucketComponent) {
    this.graduationExportRepository = graduationExportRepository;
    this.xlsxGenerator = xlsxGenerator;
    this.bucketComponent = bucketComponent;
  }

  public List<GraduateDTO> generate(
      Long promotionId, Parcours parcours, List<GraduateDTO> rawGraduates) throws IOException {
    List<GraduateDTO> ranked = rank(rawGraduates);

    byte[] xlsxBytes = xlsxGenerator.generate(ranked);

    String bucketKey = "graduates/" + promotionId + "-" + parcours + ".xlsx";
    File tempFile = File.createTempFile("graduates-", ".xlsx");
    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
      fos.write(xlsxBytes);
    }
    bucketComponent.upload(tempFile, bucketKey);

    GraduationExport export =
        graduationExportRepository
            .findTopByPromotionIdAndParcoursOrderByGeneratedAtDesc(promotionId, parcours)
            .orElse(new GraduationExport(promotionId, parcours, bucketKey));
    export.setXlsxS3Key(bucketKey);
    export.setGeneratedAt(java.time.LocalDateTime.now());
    graduationExportRepository.save(export);

    return ranked;
  }

  public File downloadXlsx(Long promotionId, Parcours parcours) {
    GraduationExport export =
        graduationExportRepository
            .findTopByPromotionIdAndParcoursOrderByGeneratedAtDesc(promotionId, parcours)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Aucun export de diplomes pour cette promotion/parcours"));

    return bucketComponent.download(export.getXlsxS3Key());
  }

  private List<GraduateDTO> rank(List<GraduateDTO> rawGraduates) {
    List<GraduateDTO> sorted =
        rawGraduates.stream()
            .sorted(Comparator.comparingDouble(GraduateDTO::generalAverage).reversed())
            .toList();

    return java.util.stream.IntStream.range(0, sorted.size())
        .mapToObj(i -> withRank(sorted.get(i), i + 1))
        .toList();
  }

  private GraduateDTO withRank(GraduateDTO graduate, int rank) {
    return new GraduateDTO(
        graduate.studentId(),
        graduate.firstName(),
        graduate.lastName(),
        graduate.parcours(),
        graduate.generalAverage(),
        rank);
  }
}
