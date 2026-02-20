package ru.gnivc.webhookproducer.service;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.jms.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.gnivc.webhookproducer.dto.EventMessageDto;

@Service
public class ProducerService {
    private static final Logger log = LoggerFactory.getLogger(ProducerService.class);

    private final JmsTemplate jmsTemplate;
    private final Queue queue;

    public ProducerService(JmsTemplate jmsTemplate, Queue queue) {
        this.jmsTemplate = jmsTemplate;
        this.queue = queue;
    }

    public void sendAction(String source, String type, JsonNode payload) {
        try {
            jmsTemplate.convertAndSend(queue, new EventMessageDto(source, type, payload));

            log.info("""
                    Message sent:
                        source: {}
                        type: {}
                        payload: {}
                    """, source, type, payload);
        } catch (Exception e) {
            log.error("Error sending action. source={}, type={}", source, type, e);

            throw e;
        }
    }
}
