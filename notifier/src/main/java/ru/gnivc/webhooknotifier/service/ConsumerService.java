package ru.gnivc.webhooknotifier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import ru.gnivc.webhooknotifier.configuration.property.ArtemisConfigurationProperty;
import ru.gnivc.webhooknotifier.dto.EventMessageDto;
import ru.gnivc.webhooknotifier.util.LoggingMessageUtil;

@Service
public class ConsumerService {
    private final NotifierTgService notifierTgService;
    private final EventRegistryService eventRegistryService;

    private static final Logger log =
            LoggerFactory.getLogger(ConsumerService.class);

    public ConsumerService(NotifierTgService notifierTgService, EventRegistryService eventRegistryService) {
        this.notifierTgService = notifierTgService;
        this.eventRegistryService = eventRegistryService;
    }

    @JmsListener(destination = ArtemisConfigurationProperty.QUEUE_ADDRESS,
            concurrency = ArtemisConfigurationProperty.CONCURRENCY)
    public void receiveAction(EventMessageDto event) {
        try {
            log.info(LoggingMessageUtil.CONSUMER_SERVICE_INFO,
                    event.source(), event.type());

            eventRegistryService.get(event.source(), event.type()).ifPresent(
                    h -> notifierTgService.sendMessage(h.handle(event.payload()))
            );
        } catch (Exception e) {
            log.error(LoggingMessageUtil.CONSUMER_SERVICE_ERROR,
                    event.source(), event.type(), e);
        }
    }
}
