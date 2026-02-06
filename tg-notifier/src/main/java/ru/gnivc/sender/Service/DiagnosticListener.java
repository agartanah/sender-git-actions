package ru.gnivc.sender.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.gnivc.sender.Models.CommitEvent;

@Component
public class DiagnosticListener {
    private final Controllers.TelegramSender telegramSender;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String KAFKA_TOPIC = "git-notifications";

    public DiagnosticListener(Controllers.TelegramSender telegramSender) {
        this.telegramSender = telegramSender;
    }

    @KafkaListener(topics = KAFKA_TOPIC, groupId = "diagnostic-group")
    public void diagnose(@Payload String message) {
        try {
            CommitEvent event = objectMapper.readValue(message, CommitEvent.class);

            String botMessage = String.format(
                    "🔥 Новое сообщение в GitHub 🔥"
                            + "\nАвтор: " + event.author()
                            + "\nРепозиторий: " + event.repositoryName()
                            + "\nВетка: " + event.branch()
                            + "\nСообщение: " + event.commitMessage()
                            + "\nСсылка: " + event.commitUrl()
            );

            telegramSender.sendMessageToChat(botMessage);
            System.out.println("✅ Message sent successfully!");
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            telegramSender.sendMessageToChat("Error parsing commit: " + message);
        }
    }
}