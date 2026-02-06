package ru.gnivc.sender.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.gnivc.sender.controllers.TelegramSender;
import ru.gnivc.sender.models.PullRequestEvent;

@Component
public class PullRequestListener {
    private static final Logger log = LoggerFactory.getLogger(PullRequestListener.class);
    private final TelegramSender telegramSender;
    private static final String KAFKA_TOPIC = "git.pull_request";
    private final ObjectMapper objectMapper;

    public PullRequestListener(TelegramSender telegramSender, ObjectMapper objectMapper) {
        this.telegramSender = telegramSender;
        this.objectMapper = objectMapper;
        log.info("PullRequest listener is listening.");
    }

    @KafkaListener(topics = KAFKA_TOPIC, groupId = "pullRequest-group")
    public void listen(@Payload String event) {
        try{
            PullRequestEvent pullRequestEvent = objectMapper.readValue(event, PullRequestEvent.class);

            String botMessage = "🔥 Новый пулреквест в GitHub 🔥"
                    + "\nАвтор: " + pullRequestEvent.author()
                    + "\nРепозиторий: " + pullRequestEvent.repositoryName()
                    + "\nВетка: " + pullRequestEvent.branch()
                    + "\nСообщение: " + pullRequestEvent.commitMessage()
                    + "\nСсылка: " + pullRequestEvent.url();

            telegramSender.sendMessageToChat(botMessage);
            System.out.println("✅ Message sent successfully.");
        } catch (JsonProcessingException e) {
            System.err.println("Json parse error: " + e.getMessage());
        }
    }
}
