package ru.gnivc.webhooknotifier.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record EventMessageDto(
        String source,
        String type,
        JsonNode payload
) { }
