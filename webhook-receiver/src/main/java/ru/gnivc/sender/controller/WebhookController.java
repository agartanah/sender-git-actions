package ru.gnivc.sender.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.gnivc.sender.dto.request.IssueCommentEvent;
import ru.gnivc.sender.dto.request.DeploymentEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import ru.gnivc.sender.dto.request.IssueEvent;
import ru.gnivc.sender.dto.request.PullRequestEvent;
import ru.gnivc.sender.util.ErrHandler;
import ru.gnivc.sender.util.LogHandler;

@RestController
@RequestMapping("/webhook")
public class WebhookController {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String KAFKA_TOPIC_BASE = "git.";
    private static final String EVENT_KEY = "default_notification_key";

    public WebhookController(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    private <T> String sendEvent(String gitEventType, String payloadJson, Class<T> eventClass){
        try{
            T event = objectMapper.readValue(payloadJson, eventClass);
            String topicName = KAFKA_TOPIC_BASE + gitEventType;
            kafkaTemplate.send(topicName, EVENT_KEY, event);
            System.out.println(LogHandler.EVENT_SEND_TO_KAFKA);
            return LogHandler.EVENT_ACCEPTED;
        } catch (JsonProcessingException e) {
            System.err.println(ErrHandler.JSON_PARSE_ERROR + e.getMessage());
            return ErrHandler.JSON_PARSE_ERROR;
        }
    }

    @PostMapping("/cicd")
    public String handleGitEvent(@RequestBody String payloadJson,
                                 @RequestHeader("CI-Event-Type") String gitEventType){
        System.out.println(LogHandler.NEW_EVENT_WITH_TYPE + gitEventType);

        return switch (gitEventType){
            case "deployment" -> sendEvent(gitEventType, payloadJson, DeploymentEvent.class);
            case "pull_request" -> sendEvent(gitEventType, payloadJson, PullRequestEvent.class);
            case "issue_comment" -> sendEvent(gitEventType, payloadJson, IssueCommentEvent.class);
            case "issue" -> sendEvent(gitEventType, payloadJson, IssueEvent.class);
            default -> "Unsupported event type: " + gitEventType;
        };
    }
}
