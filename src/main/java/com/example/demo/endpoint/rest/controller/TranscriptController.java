package com.example.demo.endpoint.rest.controller;

import com.example.demo.endpoint.rest.dto.AnnualResultDTO;
import com.example.demo.endpoint.rest.dto.TranscriptStatusResponse;
import com.example.demo.service.TranscriptService;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/transcripts")
public class TranscriptController {

  private final TranscriptService transcriptService;

  public TranscriptController(TranscriptService transcriptService) {
    this.transcriptService = transcriptService;
  }

  @PostMapping("/generate")
  public ResponseEntity<TranscriptStatusResponse> generate(@RequestBody AnnualResultDTO resultData)
      throws IOException {
    return ResponseEntity.ok(transcriptService.generate(resultData));
  }

  @GetMapping("/{studentId}")
  public ResponseEntity<TranscriptStatusResponse> getStatus(@PathVariable Long studentId) {
    return ResponseEntity.ok(transcriptService.getStatus(studentId));
  }
}
