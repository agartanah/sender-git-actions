package ru.gnivc.webhooknotifier.service;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class EventRegistryService {

    private final Map<String, IEventHandler> handlers;

    public EventRegistryService(List<IEventHandler> handlers) {
        this.handlers = handlers.stream()
                .collect(Collectors.toMap(
                        h -> h.source() + ":" + h.eventType(),
                        Function.identity()
                ));
    }

    public Optional<IEventHandler> get(String source, String type) {
        return Optional.ofNullable(handlers.get(source + ":" + type));
    }
}
