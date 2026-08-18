package com.example.demo.endpoint.web.controller;

import com.example.demo.graduation.Parcours;
import com.example.demo.promotion.PromotionPlaceholderService;
import com.example.demo.service.GraduationService;
import java.io.IOException;
import java.nio.file.Files;
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

  private final PromotionPlaceholderService promotionService;
  private final GraduationService graduationService;

  public GraduatesViewController(
      PromotionPlaceholderService promotionService, GraduationService graduationService) {
    this.promotionService = promotionService;
    this.graduationService = graduationService;
  }

  @GetMapping("/graduates")
  public String showGraduatesPage(Model model) {
    model.addAttribute("promotions", promotionService.listAll());
    model.addAttribute("parcoursOptions", Parcours.values());
    return "graduates";
  }

  @GetMapping("/graduates/export")
  @ResponseBody
  public ResponseEntity<byte[]> downloadXlsx(
      @RequestParam Long promotionId, @RequestParam Parcours parcours) throws IOException {
    byte[] xlsx =
        Files.readAllBytes(graduationService.downloadXlsx(promotionId, parcours).toPath());

    String filename = "diplomes-" + promotionId + "-" + parcours + ".xlsx";
    ContentDisposition contentDisposition =
        ContentDisposition.attachment().filename(filename).build();

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
        .contentType(
            MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(xlsx);
  }
}
