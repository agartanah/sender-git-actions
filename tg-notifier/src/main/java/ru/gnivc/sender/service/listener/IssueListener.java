package ru.gnivc.sender.service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.gnivc.sender.models.IssueCommentEvent;
import ru.gnivc.sender.models.IssueEvent;
import ru.gnivc.sender.service.TelegramSender;

@Component
public class IssueListener {
    private final TelegramSender telegramSender;
    private static final String KAFKA_TOPIC = "git.issue";
    private final ObjectMapper objectMapper;

    public IssueListener(TelegramSender telegramSender, ObjectMapper objectMapper) {
        this.telegramSender = telegramSender;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = KAFKA_TOPIC, groupId = "issue-group")
    public void listen(@Payload String event) {
        try{
            IssueEvent issueEvent = objectMapper.readValue(event, IssueEvent.class);

            String issueActionText = switch (issueEvent.action()) {
                case "opened" -> "Открыта";
                case "closed" -> "Закрыта";
                case "reopened" -> "Открыта повторно";
                default -> issueEvent.action();
            };

            String botMessage = "🔥 Проблема " + issueEvent.repositoryName() + " : " + issueActionText + " 🔥"
                    + "\nСовершил действие: " + issueEvent.author()
                    + "\n" + issueEvent.url();

            telegramSender.sendMessageToChat(botMessage);
            System.out.println("✅ Message sent successfully.");
        } catch (JsonProcessingException e) {
            System.err.println("Json parse error: " + e.getMessage());
        }
    }
}