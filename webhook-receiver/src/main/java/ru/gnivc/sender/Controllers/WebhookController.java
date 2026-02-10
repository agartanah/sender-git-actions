package ru.gnivc.sender.Controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.*;
import ru.gnivc.sender.Services.ProducerService;

@RestController
@RequestMapping("/webhook")
public class WebhookController {
    private final ProducerService producerService;

    public WebhookController(ProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping("/github")
    public String handleActionGithub(@RequestHeader("X-GitHub-Event") String event,
                                     @RequestBody JsonNode body) {
        try{
            producerService.sendAction("github", event, body);
            return "Event " + event + "!!!";
        } catch (Exception e) {
            return "Event Error";
        }
    }

    @PostMapping("/gitlab")
    public String handleActionGitlab(@RequestHeader("X-GitLab-Event") String event,
                                     @RequestBody JsonNode body) {
        try{
//            producerService.sendAction("gitlab", event, body);
            return "In dev";
        } catch (Exception e) {
            return "Event Error";
        }
    }

    @PostMapping("/gitflic")
    public String handleActionGitFlic(@RequestHeader("X-GitFlic-Event") String event,
                                     @RequestBody JsonNode body) {
        try{
//            producerService.sendAction("gitflic", event, body);
            return "In dev";
        } catch (Exception e) {
            return "Event Error";
        }
    }
}
