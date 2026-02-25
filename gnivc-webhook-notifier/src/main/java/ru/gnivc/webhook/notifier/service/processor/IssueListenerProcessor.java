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
import ru.gnivc.webhook.notifier.dto.response.IssueDto;
import ru.gnivc.webhook.notifier.service.TelegramSenderService;
import ru.gnivc.webhook.notifier.util.ErrHandlerUtil;
import ru.gnivc.webhook.notifier.util.LogHandlerUtil;
import ru.gnivc.webhook.notifier.util.TgMessageUtil;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IssueListenerProcessor {
    private static final Logger log = LoggerFactory.getLogger(IssueListenerProcessor.class);
    private final TelegramSenderService telegramSenderService;
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
                log.error(ErrHandlerUtil.VALIDATION_ERROR + "{}", errorMessage);
                return;
            }

            String botMessage = TgMessageUtil.createIssueMessage(issueDto);

            telegramSenderService.sendMessageToChat(botMessage);
            log.info(LogHandlerUtil.MESSAGE_SEND_SUCCESS);
        } catch (JsonProcessingException e) {
            log.error(ErrHandlerUtil.JSON_PARSE_ERROR + "{}", e.getMessage());
        }
    }
}