package ru.gnivc.webhookproducer.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gnivc.webhookproducer.service.ProducerService;

@RestController
@RequestMapping("/webhook")
public class WebhookController {
    private final ProducerService producerService;
    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    public WebhookController(ProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping("/cicd")
    public ResponseEntity<String> cicd(
            @RequestHeader("CI-Event-Type") String event,
            @RequestBody JsonNode body
    ) {
        log.info("Received CICD webhook. Event: {}", event);

        try {
            producerService.sendAction("cicd", event, body);

            log.info("CICD event '{}' successfully sent to broker", event);

            return ResponseEntity.ok("Event accepted");
        } catch (Exception e) {
            log.error("Failed to process CICD event '{}'", event, e);

            return ResponseEntity.internalServerError()
                    .body("Event error");
        }
    }


    @PostMapping("/github")
    public ResponseEntity<String> webhookGithub(
            @RequestHeader("X-GitHub-Event") String event,
            @RequestBody JsonNode body
    ) {
        log.info("Received GitHub webhook. Event: {}", event);

        try {
            producerService.sendAction("github", event, body);

            log.info("GitHub event '{}' successfully sent to broker", event);

            return ResponseEntity.ok("Event " + event + " accepted");
        } catch (Exception e) {
            log.error("Failed to process GitHub event '{}'", event, e);

            return ResponseEntity.internalServerError()
                    .body("Event " + event + " error");
        }
    }


    @PostMapping("/gitlab")
    public ResponseEntity<String> webhookGitlab(
            @RequestHeader("X-GitLab-Event") String event,
            @RequestBody JsonNode body
    ) {
        log.info("Received GitLab webhook. Event: {}", event);

        try {
            producerService.sendAction("gitlab", event, body);

            log.info("GitLab event '{}' successfully sent to broker", event);

            return ResponseEntity.ok("Event " + event + " accepted");
        } catch (Exception e) {
            log.error("Failed to process GitLab event '{}'", event, e);

            return ResponseEntity.internalServerError()
                    .body("Event " + event + " error");
        }
    }


    @PostMapping("/gitflic")
    public ResponseEntity<String> webhookGitflic(
            @RequestHeader("X-GitFlic-Event") String event,
            @RequestBody JsonNode body
    ) {
        log.info("Received GitFlic webhook. Event: {}", event);

        try {
            producerService.sendAction("gitflic", event, body);

            log.info("GitFlic event '{}' successfully sent to broker", event);

            return ResponseEntity.ok("Event " + event + " accepted");
        } catch (Exception e) {
            log.error("Failed to process GitFlic event '{}'", event, e);

            return ResponseEntity.internalServerError()
                    .body("Event " + event + " error");
        }
    }

}
