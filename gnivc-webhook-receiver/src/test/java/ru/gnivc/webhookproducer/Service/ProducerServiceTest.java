package ru.gnivc.webhookproducer.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.Queue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import ru.gnivc.webhook.receiver.service.ProducerService;
import ru.gnivc.webhook.receiver.util.EndpointsUtil;
import ru.gnivc.webhook.receiver.util.EventTypeUtil;
import ru.gnivc.webhook.receiver.util.SourceUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProducerServiceTest {
    private static final String JSON = """
        {
          "repository": {
            "html_url": "https://github.com/test/repo"
          },
          "commits": [
            {
              "message": "Initial commit",
              "author": {
                "name": "Vladislav"
              },
              "url": "https://github.com/test/repo/commit/123"
            },
            {
              "message": "Fix bug",
              "author": {
                "name": "Ivan"
              },
              "url": "https://github.com/test/repo/commit/456"
            }
          ]
        }
        """;

    private static final String BROKER_ERROR = "Broker error";

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private Queue queue;

    @InjectMocks
    private ProducerService producerService;

    private static final ObjectMapper mapper = new ObjectMapper();;

    private static JsonNode payload;

    @Test
    void sendAction_success() throws Exception {
        JsonNode payload = mapper.readTree(JSON);

        ResponseEntity<String> response =
                producerService.sendAction(SourceUtil.GITHUB, EventTypeUtil.PUSH, payload);

        assertEquals(200, response.getStatusCode().value());

        assertEquals(EndpointsUtil.RESPONSE_OK, response.getBody());

        verify(jmsTemplate)
                .convertAndSend(any(Queue.class), any(Object.class));
    }

    @Test
    void sendAction_brokerThrowsException() throws Exception {
        JsonNode payload = mapper.readTree(JSON);

        doThrow(new RuntimeException(BROKER_ERROR))
                .when(jmsTemplate)
                .convertAndSend(any(Queue.class), any(Object.class));

        ResponseEntity<String> response =
                producerService.sendAction(SourceUtil.GITHUB, EventTypeUtil.PUSH, payload);

        assertEquals(500, response.getStatusCode().value());

        assertEquals(EndpointsUtil.RESPONSE_SERVER_ERROR, response.getBody());
    }
}
