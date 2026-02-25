package ru.gnivc.webhook.receiver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.gnivc.webhook.receiver.dto.request.IssueCommentDto;
import ru.gnivc.webhook.receiver.dto.request.DeploymentDto;
import org.springframework.web.bind.annotation.*;
import ru.gnivc.webhook.receiver.dto.request.IssueDto;
import ru.gnivc.webhook.receiver.service.ProducerService;
import ru.gnivc.webhook.receiver.util.EndpointsUtil;
import ru.gnivc.webhook.receiver.util.ErrHandlerUtil;

@RestController
@RequestMapping(EndpointsUtil.ENDPOINT_WEBHOOK)
@RequiredArgsConstructor
public class WebhookController {
    private final ProducerService eventProcessor;
    @PostMapping(EndpointsUtil.ENDPOINT_CICD)
    public ResponseEntity<String> handleEvent(@RequestBody String payloadJson,
                                         @RequestHeader("CI-Event-Type") String gitEventType){
        return switch (gitEventType){
            case "deployment" -> eventProcessor.sendEvent(gitEventType, payloadJson, DeploymentDto.class);
            case "issue_comment" -> eventProcessor.sendEvent(gitEventType, payloadJson, IssueCommentDto.class);
            case "issue" -> eventProcessor.sendEvent(gitEventType, payloadJson, IssueDto.class);
            default -> ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(ErrHandlerUtil.UNSUPPORTED_EVENT_TYPE + gitEventType);
        };
    }
}