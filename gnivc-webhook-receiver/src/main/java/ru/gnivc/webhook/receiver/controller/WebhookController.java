package ru.gnivc.webhook.receiver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.gnivc.webhook.receiver.dto.request.IssueCommentDto;
import ru.gnivc.webhook.receiver.dto.request.DeploymentDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import ru.gnivc.webhook.receiver.dto.request.IssueDto;
import ru.gnivc.webhook.receiver.util.ErrHandlerUtil;
import ru.gnivc.webhook.receiver.util.LogHandlerUtil;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {
    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Validator validator;
    @Value("${kafka.topic.base-prefix}")
    private String KAFKA_TOPIC_BASE;
    @Value("${kafka.default-key}")
    private String EVENT_KEY;

    private <T> ResponseEntity<String> sendEvent(String gitEventType, String payloadJson, Class<T> eventClass){
        try{
            T event = objectMapper.readValue(payloadJson, eventClass);

            Set<ConstraintViolation<T>> violations = validator.validate(event);
            if (!violations.isEmpty()) {
                String errorMessage = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(", "));
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ErrHandlerUtil.VALIDATION_ERROR + errorMessage);
            }

            String topicName = KAFKA_TOPIC_BASE + gitEventType;
            kafkaTemplate.send(topicName, EVENT_KEY, event);

            log.info(LogHandlerUtil.EVENT_SEND_TO_KAFKA);
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(LogHandlerUtil.EVENT_ACCEPTED);
        } catch (JsonProcessingException e) {
            log.error(ErrHandlerUtil.JSON_PARSE_ERROR + "{}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrHandlerUtil.JSON_PARSE_ERROR + "Invalid payload structure");
        }
    }

    @PostMapping("/cicd")
    public ResponseEntity<String> handleEvent(@RequestBody String payloadJson,
                                         @RequestHeader("CI-Event-Type") String gitEventType){
        log.info(LogHandlerUtil.NEW_EVENT_WITH_TYPE + "{}", gitEventType);

        return switch (gitEventType){
            case "deployment" -> sendEvent(gitEventType, payloadJson, DeploymentDto.class);
            case "issue_comment" -> sendEvent(gitEventType, payloadJson, IssueCommentDto.class);
            case "issue" -> sendEvent(gitEventType, payloadJson, IssueDto.class);
            default -> ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(ErrHandlerUtil.UNSUPPORTED_EVENT_TYPE + gitEventType);
        };
    }
}