package ru.gnivc.webhookproducer.service;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.jms.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.gnivc.webhookproducer.dto.EventMessageDto;
import ru.gnivc.webhookproducer.util.EndpointsUtil;
import ru.gnivc.webhookproducer.util.LoggingMessageUtil;

@Service
public class ProducerService {
    private static final Logger log = LoggerFactory.getLogger(ProducerService.class);

    private final JmsTemplate jmsTemplate;
    private final Queue queue;

    public ProducerService(JmsTemplate jmsTemplate, Queue queue) {
        this.jmsTemplate = jmsTemplate;
        this.queue = queue;
    }

    public ResponseEntity<String> sendAction(String source, String type, JsonNode payload) {
        try {
            jmsTemplate.convertAndSend(queue, new EventMessageDto(source, type, payload));

            log.info(LoggingMessageUtil.SEND_ACTION_INFO, source, type, payload);

            return ResponseEntity.ok(EndpointsUtil.RESPONSE_OK);
        } catch (Exception e) {
            log.error(LoggingMessageUtil.SEND_ACTION_ERROR, source, type, e);

            return ResponseEntity.internalServerError()
                    .body(EndpointsUtil.RESPONSE_SERVER_ERROR);
        }
    }
}
