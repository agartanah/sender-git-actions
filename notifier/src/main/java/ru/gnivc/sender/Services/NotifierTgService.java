package ru.gnivc.sender.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class NotifierTgService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String botToken;
    private final String chatId;

    public NotifierTgService(
            @Value("${telegram.bot-token}") String botToken,
            @Value("${telegram.chat-id}") String chatId
    ) {
        this.botToken = botToken;
        this.chatId = chatId;
    }

    public void sendMessage(String text) {
        try {
            String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
            Map<String, Object> body = Map.of(
                    "chat_id", chatId,
                    "text", text,
                    "parse_mode", "HTML"
            );
            restTemplate.postForObject(url, body, String.class);
        } catch (Exception e) {
            System.out.println("Error of send tg message: " + e.getMessage());
        }
    }
}

