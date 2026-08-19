package com.example.demo.service;

import com.example.demo.endpoint.rest.dto.GraduateDTO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class GraduateXlsxGenerator {

  private static final String[] HEADERS = {
    "Rang", "Matricule", "Prenom", "Nom", "Parcours", "Moyenne generale"
  };

  public byte[] generate(List<GraduateDTO> graduates) throws IOException {
    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
      XSSFSheet sheet = workbook.createSheet("Diplomes");

      Row headerRow = sheet.createRow(0);
      for (int i = 0; i < HEADERS.length; i++) {
        Cell cell = headerRow.createCell(i, CellType.STRING);
        cell.setCellValue(HEADERS[i]);
      }

      int rowIndex = 1;
      for (GraduateDTO graduate : graduates) {
        Row row = sheet.createRow(rowIndex++);
        row.createCell(0, CellType.NUMERIC).setCellValue(graduate.ranking());
        row.createCell(1, CellType.STRING).setCellValue(graduate.studentId().toString());
        row.createCell(2, CellType.STRING).setCellValue(graduate.firstName());
        row.createCell(3, CellType.STRING).setCellValue(graduate.lastName());
        row.createCell(4, CellType.STRING).setCellValue(graduate.programCode());
        row.createCell(5, CellType.NUMERIC).setCellValue(graduate.generalAverage());
      }

      for (int i = 0; i < HEADERS.length; i++) {
        sheet.autoSizeColumn(i);
      }

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      workbook.write(out);
      return out.toByteArray();
    }
  }
}
