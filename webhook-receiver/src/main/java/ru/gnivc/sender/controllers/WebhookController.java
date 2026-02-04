package ru.gnivc.sender.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.gnivc.sender.models.CommitEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class WebhookController {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String KAFKA_TOPIC = "git-notifications";

    public WebhookController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/commit")
    public String handleCommit(@RequestBody CommitEvent event){
        System.out.println("Commit event for: " + event.repositoryName());

        try{
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(KAFKA_TOPIC, event.repositoryName(), eventJson);
        } catch (JsonProcessingException e) {
            return "Event Error";
        }
        return "Event accepted";
    }
}
