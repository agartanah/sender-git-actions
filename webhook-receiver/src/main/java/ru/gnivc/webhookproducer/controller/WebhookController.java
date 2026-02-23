package ru.gnivc.webhookproducer.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gnivc.webhookproducer.service.ProducerService;
import ru.gnivc.webhookproducer.util.EndpointsUtil;
import ru.gnivc.webhookproducer.util.HeaderUtil;
import ru.gnivc.webhookproducer.util.SourceUtil;

@RestController
@RequestMapping(EndpointsUtil.ENDPOINT_WEBHOOK)
public class WebhookController {
    private final ProducerService producerService;
    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    public WebhookController(ProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping(EndpointsUtil.ENDPOINT_WEBHOOK_CICD)
    public ResponseEntity<String> cicd(
            @RequestHeader(HeaderUtil.CICD) String event,
            @RequestBody JsonNode body
    ) {
        return producerService.sendAction(SourceUtil.CICD, event, body);
    }


    @PostMapping(EndpointsUtil.ENDPOINT_WEBHOOK_GITHUB)
    public ResponseEntity<String> webhookGithub(
            @RequestHeader(HeaderUtil.GITHUB) String event,
            @RequestBody JsonNode body
    ) {
        return producerService.sendAction(SourceUtil.GITHUB, event, body);
    }


    @PostMapping(EndpointsUtil.ENDPOINT_WEBHOOK_GITLAB)
    public ResponseEntity<String> webhookGitlab(
            @RequestHeader(HeaderUtil.GITLAB) String event,
            @RequestBody JsonNode body
    ) {
        return producerService.sendAction(SourceUtil.GITLAB, event, body);
    }


    @PostMapping(EndpointsUtil.ENDPOINT_WEBHOOK_GITFLIC)
    public ResponseEntity<String> webhookGitflic(
            @RequestHeader(HeaderUtil.GITFLIC) String event,
            @RequestBody JsonNode body
    ) {
        return producerService.sendAction(SourceUtil.GITFLIC, event, body);
    }

}
