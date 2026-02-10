package ru.gnivc.sender.Services;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.jms.Queue;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.gnivc.sender.Models.EventMessage;

@Service
public class ProducerService {

    private final JmsTemplate jmsTemplate;
    private final Queue queue;

    public ProducerService(JmsTemplate jmsTemplate, Queue queue) {
        this.jmsTemplate = jmsTemplate;
        this.queue = queue;
    }

    public void sendAction(String source, String type, JsonNode payload) {
        try {
            jmsTemplate.convertAndSend(queue, new EventMessage(source, type, payload));
            System.out.println("Sent: \n\tsource: " + source + "\n\ttype: " + type + "\n\tpayload:\n" + payload);
        } catch (Exception e) {
            System.out.println("Error send action " + source + " " + type + ": " + e.getMessage());
        }
    }
}
