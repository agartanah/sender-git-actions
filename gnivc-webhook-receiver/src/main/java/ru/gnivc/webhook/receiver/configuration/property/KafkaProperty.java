package ru.gnivc.webhook.receiver.configuration.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka")
public record KafkaProperty(
    String topicPrefix,
    String defaultKey
) {}