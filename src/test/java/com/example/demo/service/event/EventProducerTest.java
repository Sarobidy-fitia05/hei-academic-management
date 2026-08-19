package com.example.demo.service.event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.datastructure.ListGrouper;
import com.example.demo.endpoint.event.EventProducer;
import com.example.demo.endpoint.event.model.SendTranscriptEmailRequested;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResponse;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResultEntry;

class EventProducerTest {

    @Test
    void accept_envoieUnBatchAuBusEtNeLevePasDException() {
        ObjectMapper om = new ObjectMapper();
        EventBridgeClient eventBridgeClient = mock(EventBridgeClient.class);
        ListGrouper<SendTranscriptEmailRequested> listGrouper = new ListGrouper<>();

        var event = new SendTranscriptEmailRequested(UUID.randomUUID(), "test@example.com");

        PutEventsResultEntry successEntry =
                PutEventsResultEntry.builder().eventId("evt-1").build();
        when(eventBridgeClient.putEvents(any(software.amazon.awssdk.services.eventbridge.model.PutEventsRequest.class)))
                .thenReturn(PutEventsResponse.builder().entries(List.of(successEntry)).build());

        EventProducer<SendTranscriptEmailRequested> eventProducer =
                new EventProducer<>(om, eventBridgeClient, "test-bus", listGrouper);

        eventProducer.accept(List.of(event));

        verify(eventBridgeClient).putEvents(any(software.amazon.awssdk.services.eventbridge.model.PutEventsRequest.class));
    }
}