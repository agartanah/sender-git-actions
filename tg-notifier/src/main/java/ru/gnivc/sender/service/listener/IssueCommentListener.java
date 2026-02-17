package ru.gnivc.sender.service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.gnivc.sender.dto.response.IssueCommentEvent;
import ru.gnivc.sender.service.TelegramSender;
import ru.gnivc.sender.util.ErrHandler;
import ru.gnivc.sender.util.LogHandler;
import ru.gnivc.sender.util.TgMessageHandler;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class IssueCommentListener {
    private final TelegramSender telegramSender;
    private final Validator validator;
    private final ObjectMapper objectMapper;
    private static final String KAFKA_TOPIC = "git.issue_comment";

    public IssueCommentListener(TelegramSender telegramSender, ObjectMapper objectMapper, Validator validator) {
        this.telegramSender = telegramSender;
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    @KafkaListener(topics = KAFKA_TOPIC, groupId = "issueComment-group")
    public void listen(@Payload String event) {
        try{
            IssueCommentEvent issueCommentEvent = objectMapper.readValue(event, IssueCommentEvent.class);

            Set<ConstraintViolation<IssueCommentEvent>> violations = validator.validate(issueCommentEvent);
            if (!violations.isEmpty()) {
                String errorMessage = violations.stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .collect(Collectors.joining(", "));
                System.err.println(ErrHandler.VALIDATION_ERROR + errorMessage);
                return;
            }

            String botMessage = TgMessageHandler.createIssueCommentMessage(issueCommentEvent);

            telegramSender.sendMessageToChat(botMessage);
            System.out.println(LogHandler.MESSAGE_SEND_SUCCESS);
        } catch (JsonProcessingException e) {
            System.err.println(ErrHandler.JSON_PARSE_ERROR + e.getMessage());
        }
    }
}