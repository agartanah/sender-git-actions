package ru.gnivc.webhooknotifier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class NotifierTgService {
    private static final Logger log =
            LoggerFactory.getLogger(NotifierTgService.class);

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

            log.info("Telegram message sent successfully");
        } catch (Exception e) {
            log.error("Failed to send Telegram message", e);
        }
    }
}

