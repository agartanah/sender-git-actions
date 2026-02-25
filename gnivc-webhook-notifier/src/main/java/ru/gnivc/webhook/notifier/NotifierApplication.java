package ru.gnivc.webhook.notifier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.gnivc.webhook.notifier.service.TelegramSender;

@SpringBootApplication
public class NotifierApplication {
    public static void main(String[] args){
        SpringApplication.run(NotifierApplication.class, args);
    }

    @Bean
    public TelegramSender telegramSender(
            @Value("${telegram.bot.token}") String token,
            @Value("${telegram.chat.id}") String chatId) {
        return new TelegramSender(token, chatId);
    }
}