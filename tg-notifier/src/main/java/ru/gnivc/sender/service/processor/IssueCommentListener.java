package ru.gnivc.sender.service.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.gnivc.sender.dto.response.IssueCommentDto;
import ru.gnivc.sender.service.TelegramSender;
import ru.gnivc.sender.util.ErrHandlerUtil;
import ru.gnivc.sender.util.LogHandlerUtil;
import ru.gnivc.sender.util.TgMessageUtil;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IssueCommentListener {
    private final TelegramSender telegramSender;
    private final Validator validator;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topic.issue-comment}", groupId = "${kafka.group.issue-comment}")
    public void listen(@Payload String event) {
        try{
            IssueCommentDto issueCommentDto = objectMapper.readValue(event, IssueCommentDto.class);

            Set<ConstraintViolation<IssueCommentDto>> violations = validator.validate(issueCommentDto);
            if (!violations.isEmpty()) {
                String errorMessage = violations.stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .collect(Collectors.joining(", "));
                System.err.println(ErrHandlerUtil.VALIDATION_ERROR + errorMessage);
                return;
            }

            String botMessage = TgMessageUtil.createIssueCommentMessage(issueCommentDto);

            telegramSender.sendMessageToChat(botMessage);
            System.out.println(LogHandlerUtil.MESSAGE_SEND_SUCCESS);
        } catch (JsonProcessingException e) {
            System.err.println(ErrHandlerUtil.JSON_PARSE_ERROR + e.getMessage());
        }
    }
}