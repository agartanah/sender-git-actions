package ru.gnivc.webhook.notifier.service.processor;

import com.fasterxml.jackson.databind.JsonNode;

public interface EventProcessor {
    String source();
    String eventType();
    String handle(JsonNode payload);
}
