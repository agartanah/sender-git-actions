package ru.gnivc.webhook.receiver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gnivc.webhook.receiver.service.ProducerService;
import ru.gnivc.webhook.receiver.util.EndpointsUtil;

@RestController
@RequestMapping(EndpointsUtil.ENDPOINT_WEBHOOK)
@RequiredArgsConstructor
public class WebhookController {
    private final ProducerService producerService;
    @PostMapping(EndpointsUtil.ENDPOINT_CICD)
    public ResponseEntity<String> handleEvent(@RequestBody String payloadJson, @RequestHeader("CI-Event-Type") String eventType){
        return producerService.publishEvent(eventType, payloadJson);
    }
}