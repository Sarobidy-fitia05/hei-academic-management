package com.example.demo.endpoint.rest.controller;

import com.example.demo.endpoint.event.EventProducer;
import com.example.demo.endpoint.event.model.SendEmailRequested;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class HelloWorldController {

  private final EventProducer<SendEmailRequested> eventProducer;

  @GetMapping("/hello")
  public String helloWorld(@RequestParam String to) {
    var event = SendEmailRequested.builder().to(to).build();
    eventProducer.accept(List.of(event));
    return "... world!";
  }
}
