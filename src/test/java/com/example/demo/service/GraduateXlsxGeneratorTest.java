package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.endpoint.rest.dto.GraduateDTO;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

class GraduateXlsxGeneratorTest {

  private final GraduateXlsxGenerator generator = new GraduateXlsxGenerator();

  @Test
  void generate_produitUnClasseurAvecEnteteEtLignes() throws Exception {
    List<GraduateDTO> graduates =
        List.of(
            new GraduateDTO(UUID.randomUUID(), "Tsiory", "Rakoto", "TN", 15.8, 1),
            new GraduateDTO(UUID.randomUUID(), "Jean", "Rasoa", "EL", 14.2, 2));

    byte[] xlsx = generator.generate(graduates);

    try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(xlsx))) {
      XSSFSheet sheet = workbook.getSheetAt(0);
      assertThat(sheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("Rang");
      assertThat(sheet.getRow(1).getCell(2).getStringCellValue()).isEqualTo("Tsiory");
      assertThat(sheet.getRow(2).getCell(2).getStringCellValue()).isEqualTo("Jean");
      assertThat(sheet.getPhysicalNumberOfRows()).isEqualTo(3);
    }
  }

  @Test
  void generate_gereUneListeVide() throws Exception {
    byte[] xlsx = generator.generate(List.of());

    try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(xlsx))) {
      XSSFSheet sheet = workbook.getSheetAt(0);
      assertThat(sheet.getPhysicalNumberOfRows()).isEqualTo(1);
    }
  }
}
