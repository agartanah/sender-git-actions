package ru.gnivc.webhook.receiver.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka")
public record KafkaConfig (
    String topicPrefix,
    String defaultKey
) {}