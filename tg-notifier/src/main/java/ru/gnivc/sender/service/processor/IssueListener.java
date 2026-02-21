package ru.gnivc.sender.service.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.gnivc.sender.dto.response.IssueDto;
import ru.gnivc.sender.service.TelegramSender;
import ru.gnivc.sender.util.ErrHandlerUtil;
import ru.gnivc.sender.util.LogHandlerUtil;
import ru.gnivc.sender.util.TgMessageUtil;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IssueListener {
    private final TelegramSender telegramSender;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    @KafkaListener(topics = "${kafka.topic.issue}", groupId = "${kafka.group.issue}")
    public void listen(@Payload String event) {
        try{
            IssueDto issueDto = objectMapper.readValue(event, IssueDto.class);

            Set<ConstraintViolation<IssueDto>> violations = validator.validate(issueDto);
            if (!violations.isEmpty()) {
                String errorMessage = violations.stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .collect(Collectors.joining(", "));
                System.err.println(ErrHandlerUtil.VALIDATION_ERROR + errorMessage);
                return;
            }

            String botMessage = TgMessageUtil.createIssueMessage(issueDto);

            telegramSender.sendMessageToChat(botMessage);
            System.out.println(LogHandlerUtil.MESSAGE_SEND_SUCCESS);
        } catch (JsonProcessingException e) {
            System.err.println(ErrHandlerUtil.JSON_PARSE_ERROR + e.getMessage());
        }
    }
}