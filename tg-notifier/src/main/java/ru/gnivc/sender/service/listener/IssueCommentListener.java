package ru.gnivc.sender.service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.gnivc.sender.dto.response.IssueCommentEvent;
import ru.gnivc.sender.service.TelegramSender;

@Component
public class IssueCommentListener {
    private final TelegramSender telegramSender;
    private static final String KAFKA_TOPIC = "git.issue_comment";
    private final ObjectMapper objectMapper;

    public IssueCommentListener(TelegramSender telegramSender, ObjectMapper objectMapper) {
        this.telegramSender = telegramSender;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = KAFKA_TOPIC, groupId = "issueComment-group")
    public void listen(@Payload String event) {
        try{
            IssueCommentEvent issueCommentEvent = objectMapper.readValue(event, IssueCommentEvent.class);

            String botMessage = "🔥 Комментарий в " + issueCommentEvent.repositoryName() + " 🔥"
                    + "\nКомментарий от: " + issueCommentEvent.commentAuthor()
                    + "\n«" + issueCommentEvent.message() + "»"
                    + "\n" + issueCommentEvent.url();

            telegramSender.sendMessageToChat(botMessage);
            System.out.println("✅ Message sent successfully.");
        } catch (JsonProcessingException e) {
            System.err.println("Json parse error: " + e.getMessage());
        }
    }
}