package ru.gnivc.webhook.notifier.service.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.gnivc.webhook.notifier.dto.response.IssueCommentDto;
import ru.gnivc.webhook.notifier.service.TelegramSenderService;
import ru.gnivc.webhook.notifier.util.ErrHandlerUtil;
import ru.gnivc.webhook.notifier.util.LogHandlerUtil;
import ru.gnivc.webhook.notifier.util.TgMessageUtil;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IssueCommentListenerProcessor {
    private static final Logger log = LoggerFactory.getLogger(IssueCommentListenerProcessor.class);
    private final TelegramSenderService telegramSenderService;
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
                log.error(ErrHandlerUtil.VALIDATION_ERROR + "{}", errorMessage);
                return;
            }

            String botMessage = TgMessageUtil.createIssueCommentMessage(issueCommentDto);

            telegramSenderService.sendMessageToChat(botMessage);
            log.info(LogHandlerUtil.MESSAGE_SEND_SUCCESS);
        } catch (JsonProcessingException e) {
            log.error(ErrHandlerUtil.JSON_PARSE_ERROR + "{}", e.getMessage());
        }
    }
}