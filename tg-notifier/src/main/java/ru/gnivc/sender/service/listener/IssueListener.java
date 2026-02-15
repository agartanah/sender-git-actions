package ru.gnivc.sender.service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.gnivc.sender.dto.response.IssueEvent;
import ru.gnivc.sender.service.TelegramSender;
import ru.gnivc.sender.util.ErrHandler;
import ru.gnivc.sender.util.LogHandler;

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

            String issueActionText = switch (issueEvent.issueMessage().action()) {
                case "opened" -> "Открыта";
                case "closed" -> "Закрыта";
                case "reopened" -> "Открыта повторно";
                default -> issueEvent.issueMessage().action();
            };

            String botMessage = "🔥 Проблема " + issueEvent.issueMessage().issueTitle() + " #" + issueEvent.issueMessage().issueNumber() + " : " + issueActionText + " 🔥"
                    + "\nРепозиторий: " + issueEvent.repositoryName()
                    + "\nСовершил действие: " + issueEvent.author()
                    + "\n" + issueEvent.url();

            telegramSender.sendMessageToChat(botMessage);
            System.out.println(LogHandler.MESSAGE_SEND_SUCCESS);
        } catch (JsonProcessingException e) {
            System.err.println(ErrHandler.JSON_PARSE_ERROR + e.getMessage());
        }
    }
}