package ru.gnivc.sender.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.gnivc.sender.controllers.TelegramSender;
import ru.gnivc.sender.models.CommitEvent;

@Component
public class CommitListener {
    private static final Logger log = LoggerFactory.getLogger(CommitListener.class);
    private final TelegramSender telegramSender;
    private static final String KAFKA_TOPIC = "git.commit";
    private final ObjectMapper objectMapper;

    public CommitListener(TelegramSender telegramSender, ObjectMapper objectMapper) {
        this.telegramSender = telegramSender;
        this.objectMapper = objectMapper;
        log.info("Commit listener is listening.");
    }

    @KafkaListener(topics = KAFKA_TOPIC, groupId = "commit-group")
    public void listen(@Payload String event) {
        try{
            CommitEvent commitEvent = objectMapper.readValue(event, CommitEvent.class);

            String botMessage = "🔥 Новый коммит в GitHub 🔥"
                    + "\nАвтор: " + commitEvent.author()
                    + "\nРепозиторий: " + commitEvent.repositoryName()
                    + "\nВетка: " + commitEvent.branch()
                    + "\nСообщение: " + commitEvent.commitMessage()
                    + "\nСсылка: " + commitEvent.url();

            telegramSender.sendMessageToChat(botMessage);
            System.out.println("✅ Message sent successfully.");
        } catch (JsonProcessingException e) {
            System.err.println("Json parse error: " + e.getMessage());
        }
    }
}