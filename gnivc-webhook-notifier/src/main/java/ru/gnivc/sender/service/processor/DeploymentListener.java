package ru.gnivc.sender.service.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.gnivc.sender.dto.response.DeploymentDto;
import ru.gnivc.sender.service.TelegramSender;
import ru.gnivc.sender.util.ErrHandlerUtil;
import ru.gnivc.sender.util.LogHandlerUtil;
import jakarta.validation.Validator;
import ru.gnivc.sender.util.TgMessageUtil;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DeploymentListener {
    private final TelegramSender telegramSender;
    private final Validator validator;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topic.deployment}", groupId = "${kafka.group.deployment}")
    public void listen(@Payload String event) {
        try{
            DeploymentDto deploymentDto = objectMapper.readValue(event, DeploymentDto.class);

            Set<ConstraintViolation<DeploymentDto>> violations = validator.validate(deploymentDto);
            if (!violations.isEmpty()) {
                String errorMessage = violations.stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .collect(Collectors.joining(", "));
                System.err.println(ErrHandlerUtil.VALIDATION_ERROR + errorMessage);
                return;
            }

            String botMessage = TgMessageUtil.createDeploymentMessage(deploymentDto);

            telegramSender.sendMessageToChat(botMessage);
            System.out.println(LogHandlerUtil.MESSAGE_SEND_SUCCESS);
        } catch (JsonProcessingException e) {
            System.err.println(ErrHandlerUtil.JSON_PARSE_ERROR + e.getMessage());
        }
    }
}