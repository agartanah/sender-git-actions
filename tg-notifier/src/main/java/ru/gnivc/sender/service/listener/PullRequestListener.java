package ru.gnivc.sender.service.listener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.gnivc.sender.models.PullRequestEvent;
import ru.gnivc.sender.service.TelegramSender;

@Component
public class PullRequestListener {
    private static final Logger log = LoggerFactory.getLogger(PullRequestListener.class);
    private final TelegramSender telegramSender;
    private static final String KAFKA_TOPIC = "git.pull_request";
    private final ObjectMapper objectMapper;

    public PullRequestListener(TelegramSender telegramSender, ObjectMapper objectMapper) {
        this.telegramSender = telegramSender;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = KAFKA_TOPIC, groupId = "pullRequest-group")
    public void listen(@Payload String event) {
        try {
            PullRequestEvent pullRequestEvent = objectMapper.readValue(event, PullRequestEvent.class);

            String botMessage = "🔥 ПуллРеквест " + pullRequestEvent.repositoryName() + " 🔥"
                    + "\nСостояние: " + pullRequestEvent.action()
                    + "\nАвтор: " + pullRequestEvent.author()
                    + "\n" + pullRequestEvent.url();

            telegramSender.sendMessageToChat(botMessage);
            System.out.println("✅ Message sent successfully.");
        } catch (JsonProcessingException e) {
            System.err.println("Json parse error: " + e.getMessage());
        }
    }
}