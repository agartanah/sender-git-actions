package ru.gnivc.sender.service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.gnivc.sender.dto.response.DeploymentEvent;
import ru.gnivc.sender.service.TelegramSender;
import ru.gnivc.sender.util.ErrHandler;
import ru.gnivc.sender.util.LogHandler;

@Component
public class DeploymentListener {
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
                    + "\nРепозиторий: " + deploymentEvent.repositoryName()
                    + "\nСостояние: " + deploymentEvent.environment()
                    + "\nСделал деплой: " + deploymentEvent.deployer()
                    + "\n" + deploymentEvent.url();

            telegramSender.sendMessageToChat(botMessage);
            System.out.println(LogHandler.MESSAGE_SEND_SUCCESS);
        } catch (JsonProcessingException e) {
            System.err.println(ErrHandler.JSON_PARSE_ERROR + e.getMessage());
        }
    }
}