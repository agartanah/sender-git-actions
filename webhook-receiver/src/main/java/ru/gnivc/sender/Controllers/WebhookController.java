package ru.gnivc.sender.Controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gnivc.sender.Services.ProducerService;

@RestController
@RequestMapping("/webhook")
public class WebhookController {
    private final ProducerService producerService;

    public WebhookController(ProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping("/cicd")
    public ResponseEntity<String> handleCiCdEvent(
            @RequestHeader("CI-Event-Type") String event,
            @RequestBody JsonNode body
    ) {
        try{
            producerService.sendAction("cicd", event, body);
            return new ResponseEntity<>("Event accepted", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Event error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/github")
    public ResponseEntity<String> handleActionGithub(
            @RequestHeader("X-GitHub-Event") String event,
            @RequestBody JsonNode body) {
        try {
            producerService.sendAction("github", event, body);
            return ResponseEntity.ok("Event " + event + " accepted");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Event error: " + e.getMessage());
        }
    }

    @PostMapping("/gitlab")
    public ResponseEntity<String> handleActionGitlab(
            @RequestHeader("X-GitLab-Event") String event,
            @RequestBody JsonNode body) {
        try {
            producerService.sendAction("gitlab", event, body);
            return ResponseEntity.ok("Event " + event + " accepted");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Event error: " + e.getMessage());
        }
    }

    @PostMapping("/gitflic")
    public ResponseEntity<String> handleActionGitFlic(
            @RequestHeader("X-GitFlic-Event") String event,
            @RequestBody JsonNode body) {
        try {
            producerService.sendAction("gitflic", event, body);
            return ResponseEntity.ok("Event " + event + " accepted");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Event error: " + e.getMessage());
        }
    }
}
