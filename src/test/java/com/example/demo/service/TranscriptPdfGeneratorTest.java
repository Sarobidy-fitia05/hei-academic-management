package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.endpoint.rest.dto.AnnualResultDTO;
import com.example.demo.endpoint.rest.dto.GradeDTO;
import java.util.List;
import org.junit.jupiter.api.Test;

class TranscriptPdfGeneratorTest {

  private final TranscriptPdfGenerator generator = new TranscriptPdfGenerator();

  @Test
  void generate_produitUnPdfNonVide() throws Exception {
    AnnualResultDTO data =
        new AnnualResultDTO(
            1L,
            "Tsiory",
            "Rakoto",
            2026,
            List.of(
                new GradeDTO("INFO101", "Algorithmique", 5, 15.5),
                new GradeDTO("INFO102", "Bases de donnees", 4, 12.0)),
            13.8,
            9);

    byte[] pdf = generator.generate(data);

    assertThat(pdf).isNotEmpty();
    assertThat(new String(pdf, 0, 5)).isEqualTo("%PDF-");
  }

  @Test
  void generate_gereUneListeDeNotesVide() throws Exception {
    AnnualResultDTO data = new AnnualResultDTO(2L, "Jean", "Rasoa", 2026, List.of(), 0.0, 0);

    byte[] pdf = generator.generate(data);

    assertThat(pdf).isNotEmpty();
  }
}
