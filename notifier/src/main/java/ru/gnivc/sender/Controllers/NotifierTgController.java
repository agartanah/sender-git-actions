package ru.gnivc.sender.Controllers;

import org.springframework.web.bind.annotation.*;
import ru.gnivc.sender.Services.NotifierTgService;

import java.util.Map;

@RestController
@RequestMapping("/notify")
public class NotifierTgController {

    private final NotifierTgService notifierTgService;

    public NotifierTgController(NotifierTgService notifierTgService) {
        this.notifierTgService = notifierTgService;
    }

    @GetMapping
    public String sendMessage(@RequestParam String message) {
        notifierTgService.sendMessage(message);
        return "Message sent to Telegram!";
    }

    @PostMapping
    public String sendMessagePost(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        notifierTgService.sendMessage(message);
        return "Message sent to Telegram!";
    }
}
