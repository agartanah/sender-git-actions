package ru.gnivc.webhook.notifier.configuration.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram")
public record TelegramProperty(
        String botToken,
        String chatId
) { }