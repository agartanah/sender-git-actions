package Controllers;

import models.CommitEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
public class WebhookController {
    private final KafkaTemplate<String, CommitEvent> kafkaTemplate;

    public WebhookController(KafkaTemplate<String, CommitEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/commit")
    public String handleCommit(@RequestBody CommitEvent event){
        System.out.println("Commit event for: " + event.repositoryName());
        kafkaTemplate.send("KAFKA_TOPIC", event.repositoryName(), event);
        return "Event accepted";
    }
}
