package ru.gnivc.sender.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.gnivc.sender.models.CommitEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import ru.gnivc.sender.models.PullRequestEvent;

@RestController
@RequestMapping("/api/1")
public class WebhookController {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String KAFKA_TOPIC_BASE = "git.";
    private static final String EVENT_KEY = "default_notification_key";

    public WebhookController(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    private <T> String handleAndSend(String gitEventType, String payloadJson, Class<T> eventClass){
        try{
            T event = objectMapper.readValue(payloadJson, eventClass);
            String topicName = KAFKA_TOPIC_BASE + gitEventType;
            kafkaTemplate.send(topicName, EVENT_KEY, event);
            return "Event accepted";
        } catch (JsonProcessingException e) {
            System.err.println("Json parse error: " + e.getMessage());
            return "Json parse Error";
        }
    }

    @PostMapping("/webhook")
    public String handleGitEvent(@RequestBody String payloadJson,
                                 @RequestHeader("X-GitHub-Event") String gitEventType){
        System.out.println("New event with type: " + gitEventType);

        return switch (gitEventType){
            case "commit" -> handleAndSend(gitEventType, payloadJson, CommitEvent.class);
            case "pull_request" -> handleAndSend(gitEventType, payloadJson, PullRequestEvent.class);
            default -> "Unsupported event type: " + gitEventType;
        };
    }
}
