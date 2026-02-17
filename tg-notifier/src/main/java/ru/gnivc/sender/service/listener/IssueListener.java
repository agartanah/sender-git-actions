package ru.gnivc.sender.service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.gnivc.sender.dto.response.IssueEvent;
import ru.gnivc.sender.service.TelegramSender;
import ru.gnivc.sender.util.ErrHandler;
import ru.gnivc.sender.util.LogHandler;
import ru.gnivc.sender.util.TgMessageHandler;

import java.util.Set;
import java.util.stream.Collectors;


@Component
public class IssueListener {
    private final TelegramSender telegramSender;
    private final ObjectMapper objectMapper;
    private final Validator validator;
    private static final String KAFKA_TOPIC = "git.issue";

    public IssueListener(TelegramSender telegramSender, ObjectMapper objectMapper, Validator validator) {
        this.telegramSender = telegramSender;
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    @KafkaListener(topics = KAFKA_TOPIC, groupId = "issue-group")
    public void listen(@Payload String event) {
        try{
            IssueEvent issueEvent = objectMapper.readValue(event, IssueEvent.class);

            Set<ConstraintViolation<IssueEvent>> violations = validator.validate(issueEvent);
            if (!violations.isEmpty()) {
                String errorMessage = violations.stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .collect(Collectors.joining(", "));
                System.err.println(ErrHandler.VALIDATION_ERROR + errorMessage);
                return;
            }

            String botMessage = TgMessageHandler.createIssueMessage(issueEvent);

            telegramSender.sendMessageToChat(botMessage);
            System.out.println(LogHandler.MESSAGE_SEND_SUCCESS);
        } catch (JsonProcessingException e) {
            System.err.println(ErrHandler.JSON_PARSE_ERROR + e.getMessage());
        }
    }
}