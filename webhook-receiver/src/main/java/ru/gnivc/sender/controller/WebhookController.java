package ru.gnivc.sender.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.gnivc.sender.dto.request.IssueCommentEvent;
import ru.gnivc.sender.dto.request.DeploymentEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import ru.gnivc.sender.dto.request.IssueEvent;
import ru.gnivc.sender.util.ErrHandler;
import ru.gnivc.sender.util.LogHandler;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/webhook")
public class WebhookController {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Validator validator;
    private static final String KAFKA_TOPIC_BASE = "git.";
    private static final String EVENT_KEY = "default_notification_key";

    public WebhookController(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper, Validator validator) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

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
                        .body(ErrHandler.VALIDATION_ERROR + errorMessage);
            }

            String topicName = KAFKA_TOPIC_BASE + gitEventType;
            kafkaTemplate.send(topicName, EVENT_KEY, event);

            System.out.println(LogHandler.EVENT_SEND_TO_KAFKA);
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(LogHandler.EVENT_ACCEPTED);
        } catch (JsonProcessingException e) {
            System.err.println(ErrHandler.JSON_PARSE_ERROR + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrHandler.JSON_PARSE_ERROR + "Invalid payload structure");
        }
    }

    @PostMapping("/cicd")
    public ResponseEntity<String> handleGitEvent(@RequestBody String payloadJson,
                                         @RequestHeader("CI-Event-Type") String gitEventType){
        System.out.println(LogHandler.NEW_EVENT_WITH_TYPE + gitEventType);

        return switch (gitEventType){
            case "deployment" -> sendEvent(gitEventType, payloadJson, DeploymentEvent.class);
            case "issue_comment" -> sendEvent(gitEventType, payloadJson, IssueCommentEvent.class);
            case "issue" -> sendEvent(gitEventType, payloadJson, IssueEvent.class);
            default -> ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(ErrHandler.UNSUPPORTED_EVENT_TYPE + gitEventType);
        };
    }
}