package com.example.demo.endpoint.rest.controller;

import com.example.demo.endpoint.rest.dto.GraduateDTO;
import com.example.demo.graduation.Parcours;
import com.example.demo.service.GraduationService;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/graduates")
public class GraduationController {

  private final GraduationService graduationService;

  public GraduationController(GraduationService graduationService) {
    this.graduationService = graduationService;
  }

  @PostMapping("/generate")
  public ResponseEntity<List<GraduateDTO>> generate(
      @RequestParam Long promotionId,
      @RequestParam Parcours parcours,
      @RequestBody List<GraduateDTO> rawGraduates)
      throws IOException {
    return ResponseEntity.ok(graduationService.generate(promotionId, parcours, rawGraduates));
  }

  @GetMapping(
      value = "/export",
      produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
  public ResponseEntity<byte[]> downloadXlsx(
      @RequestParam Long promotionId, @RequestParam Parcours parcours) throws IOException {
    byte[] xlsx =
        Files.readAllBytes(graduationService.downloadXlsx(promotionId, parcours).toPath());
    return ResponseEntity.ok()
        .contentType(
            MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(xlsx);
  }
}
