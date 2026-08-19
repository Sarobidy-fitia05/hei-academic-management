package com.example.demo.service.event;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.endpoint.event.model.SendTranscriptEmailRequested;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SendTranscriptEmailRequestedTest {

    @Test
    void gettersEtSetters_fonctionnentCorrectement() {
        var event = new SendTranscriptEmailRequested();
        UUID studentId = UUID.randomUUID();
        event.setStudentId(studentId);
        event.setRecipientEmail("test@example.com");

        assertThat(event.getStudentId()).isEqualTo(studentId);
        assertThat(event.getRecipientEmail()).isEqualTo("test@example.com");
    }

    @Test
    void maxConsumerDurationEtBackoff_ontLesValeursAttendues() {
        var event = new SendTranscriptEmailRequested(UUID.randomUUID(), "test@example.com");

        assertThat(event.maxConsumerDuration()).isEqualTo(Duration.ofSeconds(45));
        assertThat(event.maxConsumerBackoffBetweenRetries()).isEqualTo(Duration.ofSeconds(30));
    }

    @Test
    void equalsEtHashCode_sontCoherentsEntreEvenementsIdentiques() {
        UUID studentId = UUID.randomUUID();
        var event1 = new SendTranscriptEmailRequested(studentId, "test@example.com");
        var event2 = new SendTranscriptEmailRequested(studentId, "test@example.com");
        var event3 = new SendTranscriptEmailRequested(UUID.randomUUID(), "autre@example.com");

        assertThat(event1).isEqualTo(event2);
        assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
        assertThat(event1).isNotEqualTo(event3);
        assertThat(event1).isNotEqualTo(null);
        assertThat(event1).isEqualTo(event1);
        assertThat(event1).isNotEqualTo("une chaine de caracteres");
    }
}