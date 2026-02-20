package ru.gnivc.webhooknotifier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import ru.gnivc.webhooknotifier.dispatch.EventHandlerRegistry;
import ru.gnivc.webhooknotifier.dto.EventMessageDto;

@Service
public class ConsumerService {
    private final NotifierTgService notifierTgService;
    private final EventHandlerRegistry eventHandlerRegistry;

    private static final Logger log =
            LoggerFactory.getLogger(ConsumerService.class);

    public ConsumerService(NotifierTgService notifierTgService, EventHandlerRegistry eventHandlerRegistry) {
        this.notifierTgService = notifierTgService;
        this.eventHandlerRegistry = eventHandlerRegistry;
    }

    @JmsListener(destination = "git-actions-notify", concurrency = "3-10")
    public void receiveAction(EventMessageDto event) {
        try {
            log.info("Received event. source={}, type={}",
                    event.source(), event.type());

            eventHandlerRegistry.get(event.source(), event.type()).ifPresent(
                    h -> notifierTgService.sendMessage(h.handle(event.payload()))
            );
        } catch (Exception e) {
            log.error("Error processing event. source={}, type={}",
                    event.source(), event.type(), e);
        }
    }
}
