package ru.gnivc.webhooknotifier.dispatch;

import com.fasterxml.jackson.databind.JsonNode;

public interface IEventHandler {
    String source();
    String eventType();
    String handle(JsonNode payload);
}
