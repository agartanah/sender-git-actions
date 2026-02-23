package ru.gnivc.webhooknotifier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.gnivc.webhooknotifier.util.LoggingMessageUtil;
import ru.gnivc.webhooknotifier.util.NotifierTgServiceUtil;

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
            String url = String.format(NotifierTgServiceUtil.SEND_MESSAGE_URL_TEMPLATE, botToken);

            Map<String, Object> body = Map.of(
                    NotifierTgServiceUtil.CHAT_ID_FIELD, chatId,
                    NotifierTgServiceUtil.TEXT_FIELD, text,
                    NotifierTgServiceUtil.PARSE_MODE_FIELD, NotifierTgServiceUtil.PARSE_MODE_HTML
            );

            restTemplate.postForObject(url, body, String.class);

            log.info(LoggingMessageUtil.NOTIFIER_TG_SERVICE_INFO);
        } catch (Exception e) {
            log.error(LoggingMessageUtil.NOTIFIER_TG_SERVICE_ERROR, e);
        }
    }
}

