package com.example.demo.service.event;

import com.example.demo.endpoint.event.model.SendEmailRequested;
import com.example.demo.mail.Email;
import com.example.demo.mail.Mailer;
import jakarta.mail.internet.InternetAddress;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class SendEmailRequestedService implements Consumer<SendEmailRequested> {

  private final Mailer mailer;

  @Override
  @SneakyThrows
  public void accept(SendEmailRequested event) {
    log.info("Sending async email to: {}", event.getTo());
    var email =
        new Email(
            new InternetAddress(event.getTo()),
            List.of(),
            List.of(),
            "Hello",
            "... world!",
            List.of());
    mailer.accept(email);
    log.info("Email successfully sent to: {}", event.getTo());
  }
}
