package com.example.demo.endpoint.rest.controller;

import com.example.demo.endpoint.event.EventProducer;
import com.example.demo.endpoint.event.model.SendTranscriptEmailRequested;
import com.example.demo.endpoint.rest.dto.AnnualResultDTO;
import com.example.demo.endpoint.rest.dto.TranscriptStatusResponse;
import com.example.demo.service.TranscriptService;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/transcripts")
public class TranscriptController {

  private final TranscriptService transcriptService;
  private final EventProducer<SendTranscriptEmailRequested> eventProducer;

  public TranscriptController(
          TranscriptService transcriptService,
          EventProducer<SendTranscriptEmailRequested> eventProducer) {
    this.transcriptService = transcriptService;
    this.eventProducer = eventProducer;
  }

  @PostMapping("/generate")
  public ResponseEntity<TranscriptStatusResponse> generate(@RequestBody AnnualResultDTO resultData)
          throws IOException {
    return ResponseEntity.ok(transcriptService.generate(resultData));
  }

  @GetMapping("/{studentId}")
  public ResponseEntity<TranscriptStatusResponse> getStatus(@PathVariable UUID studentId) {
    return ResponseEntity.ok(transcriptService.getStatus(studentId));
  }

  @GetMapping(value = "/{studentId}/download", produces = MediaType.APPLICATION_PDF_VALUE)
  public ResponseEntity<byte[]> download(@PathVariable UUID studentId) throws IOException {
    byte[] pdf = Files.readAllBytes(transcriptService.downloadPdf(studentId).toPath());
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(pdf);
  }

  @PostMapping("/{studentId}/send")
  public ResponseEntity<Void> sendByEmail(
          @PathVariable UUID studentId, @RequestParam String email) {
    var event = new SendTranscriptEmailRequested(studentId, email);
    eventProducer.accept(List.of(event));
    return ResponseEntity.accepted().build();
  }
}