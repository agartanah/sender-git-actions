package ru.gnivc.sender.Services;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import ru.gnivc.sender.Dispatches.EventHandlerRegistry;
import ru.gnivc.sender.Models.EventMessage;

@Service
public class ConsumerService {

    private final NotifierTgService notifierTgService;
    private final EventHandlerRegistry eventHandlerRegistry;

    public ConsumerService(NotifierTgService notifierTgService, EventHandlerRegistry eventHandlerRegistry) {
        this.notifierTgService = notifierTgService;
        this.eventHandlerRegistry = eventHandlerRegistry;
    }

    @JmsListener(destination = "git-actions-notify", concurrency = "3-10")
    public void receiveAction(EventMessage event) {
        try {
            System.out.println(event.type() + " " + event.source());

            eventHandlerRegistry.get(event.source(), event.type()).ifPresent(
                    h -> notifierTgService.sendMessage(h.handle(event.payload()))
            );
        } catch (Exception e) {
            System.out.println("Expected error: " + e.getMessage());
        }
    }
}
