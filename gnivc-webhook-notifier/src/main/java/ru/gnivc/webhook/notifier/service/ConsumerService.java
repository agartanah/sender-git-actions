package ru.gnivc.webhook.notifier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import ru.gnivc.webhook.notifier.dto.EventMessageDto;
import ru.gnivc.webhook.notifier.service.processor.EventRegistryProcessor;
import ru.gnivc.webhook.notifier.util.LoggingMessageUtil;

@Service
public class ConsumerService {
    private final NotifierTgService notifierTgService;
    private final EventRegistryProcessor eventRegistryProcessor;

    private static final Logger log =
            LoggerFactory.getLogger(ConsumerService.class);

    public ConsumerService(NotifierTgService notifierTgService, EventRegistryProcessor eventRegistryProcessor) {
        this.notifierTgService = notifierTgService;
        this.eventRegistryProcessor = eventRegistryProcessor;
    }

    @JmsListener(destination = "${ru.gnivc.webhooknotifier.artemis.queue-address}",
            concurrency = "${ru.gnivc.webhooknotifier.artemis.concurrency}")
    public void receiveAction(EventMessageDto event) {
        try {
            log.info(LoggingMessageUtil.CONSUMER_SERVICE_INFO,
                    event.source(), event.type());

            eventRegistryProcessor.get(event.source(), event.type()).ifPresent(
                    h -> notifierTgService.sendMessage(h.handle(event.payload()))
            );
        } catch (Exception e) {
            log.error(LoggingMessageUtil.CONSUMER_SERVICE_ERROR,
                    event.source(), event.type(), e);
        }
    }
}
