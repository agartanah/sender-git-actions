package ru.gnivc.webhook.receiver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import ru.gnivc.webhook.receiver.configuration.KafkaConfig;
import ru.gnivc.webhook.receiver.dto.request.DeploymentDto;
import ru.gnivc.webhook.receiver.dto.request.IssueCommentDto;
import ru.gnivc.webhook.receiver.dto.request.IssueDto;
import ru.gnivc.webhook.receiver.util.ErrHandlerUtil;
import ru.gnivc.webhook.receiver.util.LogHandlerUtil;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProducerService {
    private final KafkaConfig kafkaConfig;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    private  <T> ResponseEntity<String> sendEvent(String eventType, String payloadJson, Class<T> eventClass) {
        try {
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

            String topicName = kafkaConfig.topicPrefix() + eventType;
            kafkaTemplate.send(topicName, kafkaConfig.defaultKey(), event);

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

    public ResponseEntity<String> publishEvent(String eventType, String payloadJson){
        return switch (eventType){
            case "deployment" -> sendEvent(eventType, payloadJson, DeploymentDto.class);
            case "issue_comment" -> sendEvent(eventType, payloadJson, IssueCommentDto.class);
            case "issue" -> sendEvent(eventType, payloadJson, IssueDto.class);
            default -> ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(ErrHandlerUtil.UNSUPPORTED_EVENT_TYPE + eventType);
        };
    }
}
