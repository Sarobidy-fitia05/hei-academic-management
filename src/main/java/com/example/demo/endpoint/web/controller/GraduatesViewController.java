package com.example.demo.endpoint.web.controller;

import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.GraduationListRepository;
import com.example.demo.service.GraduationService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GraduatesViewController {

  private final GraduationService graduationService;
  private final GraduationListRepository graduationListRepository;
  private final DocumentRepository documentRepository;
  private final BucketComponent bucketComponent;

  public GraduatesViewController(
      GraduationService graduationService,
      GraduationListRepository graduationListRepository,
      DocumentRepository documentRepository,
      BucketComponent bucketComponent) {
    this.graduationService = graduationService;
    this.graduationListRepository = graduationListRepository;
    this.documentRepository = documentRepository;
    this.bucketComponent = bucketComponent;
  }

  @GetMapping("/graduates")
  public String showGraduatesPage(Model model) {
    List<?> lists = graduationListRepository.findAll();

    Map<UUID, String> presignMap = new HashMap<>();
    lists.forEach(
        obj -> {
          // GraduationList est une entité ; on l'utilise de façon générique pour éviter import
          // circulaire
          try {
            UUID id = (UUID) obj.getClass().getMethod("getId").invoke(obj);
            var docs = documentRepository.findByGraduationListId(id);
            docs.stream()
                .findFirst()
                .ifPresent(
                    d -> {
                      try {
                        presignMap.put(
                            id,
                            bucketComponent
                                .presign(d.getS3Key(), Duration.ofMinutes(15))
                                .toString());
                      } catch (Exception e) {
                        // si presign échoue, on ignore et on laissera le fallback vers l'endpoint
                        // de l'app
                      }
                    });
          } catch (Exception ignored) {
            // ignore reflection errors (shouldn't happen si GraduationList a getId())
          }
        });

    model.addAttribute("graduationLists", lists);
    model.addAttribute("presignMap", presignMap);
    return "graduates";
  }

  @GetMapping("/graduates/export")
  @ResponseBody
  public ResponseEntity<byte[]> downloadXlsx(@RequestParam UUID graduationListId)
      throws IOException {
    File file = graduationService.downloadXlsx(graduationListId);
    byte[] xlsx = Files.readAllBytes(file.toPath());

    ContentDisposition contentDisposition =
        ContentDisposition.attachment().filename("diplomes.xlsx").build();

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
        .contentType(
            MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(xlsx);
  }
}
