package ru.gnivc.sender.Models;

import com.fasterxml.jackson.databind.JsonNode;

public record EventMessage(
        String source,
        String type,
        JsonNode payload
) { }
