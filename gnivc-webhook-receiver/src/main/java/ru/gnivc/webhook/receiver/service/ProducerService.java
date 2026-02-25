package ru.gnivc.webhook.receiver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import ru.gnivc.webhook.receiver.util.ErrHandlerUtil;
import ru.gnivc.webhook.receiver.util.LogHandlerUtil;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProducerService {
    private static final Logger log = LoggerFactory.getLogger(ProducerService.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    @Value("${kafka.topic.base-prefix}")
    private String KAFKA_TOPIC_BASE;
    @Value("${kafka.default-key}")
    private String EVENT_KEY;

    public <T> ResponseEntity<String> sendEvent(String eventType, String payloadJson, Class<T> eventClass) {
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

            String topicName = KAFKA_TOPIC_BASE + eventType;
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
}
