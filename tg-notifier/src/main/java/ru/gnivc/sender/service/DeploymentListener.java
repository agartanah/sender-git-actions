package ru.gnivc.sender.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.gnivc.sender.controllers.TelegramSender;
import ru.gnivc.sender.models.DeploymentEvent;

@Component
public class DeploymentListener {
    private static final Logger log = LoggerFactory.getLogger(DeploymentListener.class);
    private final TelegramSender telegramSender;
    private static final String KAFKA_TOPIC = "git.deployment";
    private final ObjectMapper objectMapper;

    public DeploymentListener(TelegramSender telegramSender, ObjectMapper objectMapper) {
        this.telegramSender = telegramSender;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = KAFKA_TOPIC, groupId = "deployment-group")
    public void listen(@Payload String event) {
        try{
            DeploymentEvent deploymentEvent = objectMapper.readValue(event, DeploymentEvent.class);

            String botMessage = "🔥 Деплой 🔥"
                    + "\nРепозиторий: " + deploymentEvent.getRepositoryName()
                    + "\nСтатус: " + deploymentEvent.getStatus()
                    + "\nСреда: " + deploymentEvent.getEnvironment()
                    + "\nСделал деплой: " + deploymentEvent.getDeployer()
                    + "\n" + deploymentEvent.getWorkflowRunUrl();

            telegramSender.sendMessageToChat(botMessage);
            System.out.println("✅ Message sent successfully.");
        } catch (JsonProcessingException e) {
            System.err.println("Json parse error: " + e.getMessage());
        }
    }
}