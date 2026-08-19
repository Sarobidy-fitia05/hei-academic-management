package com.example.demo.endpoint.web.controller;

import com.example.demo.service.GraduationService;
import java.io.IOException;
import java.nio.file.Files;
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

  public GraduatesViewController(GraduationService graduationService) {
    this.graduationService = graduationService;
  }

  @GetMapping("/graduates")
  public String showGraduatesPage(Model model) {
    return "graduates";
  }

  @GetMapping("/graduates/export")
  @ResponseBody
  public ResponseEntity<byte[]> downloadXlsx(@RequestParam UUID graduationListId)
      throws IOException {
    byte[] xlsx = Files.readAllBytes(graduationService.downloadXlsx(graduationListId).toPath());

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
