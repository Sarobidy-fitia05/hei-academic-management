package com.example.demo.endpoint.event.model;

import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class SendEmailWithPdfRequested extends PojaEvent {
  private String to;
  private String bucketKey; // clé du fichier PDF dans le bucket S3

  @Override
  public Duration maxConsumerDuration() {
    return Duration.ofSeconds(60); // un peu plus large car download + envoi
  }

  @Override
  public Duration maxConsumerBackoffBetweenRetries() {
    return Duration.ofSeconds(30);
  }
}
