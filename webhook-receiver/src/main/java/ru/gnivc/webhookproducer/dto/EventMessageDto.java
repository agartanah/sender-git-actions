package ru.gnivc.webhookproducer.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record EventMessageDto(
        String source,
        String type,
        JsonNode payload
) { }
