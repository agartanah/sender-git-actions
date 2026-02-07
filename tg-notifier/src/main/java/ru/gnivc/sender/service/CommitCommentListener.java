package ru.gnivc.sender.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.gnivc.sender.controllers.TelegramSender;
import ru.gnivc.sender.models.CommitCommentEvent;

@Component
public class CommitCommentListener {
    private static final Logger log = LoggerFactory.getLogger(CommitCommentListener.class);
    private final TelegramSender telegramSender;
    private static final String KAFKA_TOPIC = "git.commit_comment";
    private final ObjectMapper objectMapper;

    public CommitCommentListener(TelegramSender telegramSender, ObjectMapper objectMapper) {
        this.telegramSender = telegramSender;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = KAFKA_TOPIC, groupId = "commitComment-group")
    public void listen(@Payload String event) {
        try{
            CommitCommentEvent commitCommentEvent = objectMapper.readValue(event, CommitCommentEvent.class);

            String botMessage = "🔥 Комментарий в " + commitCommentEvent.getRepositoryName() + " 🔥"
                    + "\nВетка: " + commitCommentEvent.getBranchName()
                    + "\nКомментарий от: " + commitCommentEvent.getCommentAuthor()
                    + "\n«" + commitCommentEvent.getCommentText() + "»"
                    + "\n" + commitCommentEvent.getCommentHtmlUrl();

            telegramSender.sendMessageToChat(botMessage);
            System.out.println("✅ Message sent successfully.");
        } catch (JsonProcessingException e) {
            System.err.println("Json parse error: " + e.getMessage());
        }
    }
}