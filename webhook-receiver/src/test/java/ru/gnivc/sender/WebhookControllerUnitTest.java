package ru.gnivc.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.gnivc.sender.controller.WebhookController;
import ru.gnivc.sender.dto.request.IssueEvent;
import ru.gnivc.sender.util.LogHandler;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebhookController.class)
public class WebhookControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private KafkaTemplate<String, Object> kafkaTemplate;
    @MockitoBean
    private ObjectMapper objectMapper;

    private static final String TOPIC_BASE = "git.";
    private static final String EVENT_KEY = "default_notification_key";

    @Test
    void positiveDataTest_AllFields() throws Exception {
        String eventType = "issue";
        String payloadJson = "{\"repositoryName\":\"test name\",\"author\":\"test author\",\"eventMessage\":{\"action\":\"opened\",\"issueTitle\":\"test title\",\"issueNumber\":1},\"eventUrl\":\"http://test.com\"}";

        IssueEvent mockEvent = new IssueEvent(
                "test name",
                "test author",
                new IssueEvent.IssueMessage("opened", "test title", 1),
                "http://test.com"
        );

        when(objectMapper.readValue(payloadJson, IssueEvent.class)).thenReturn(mockEvent);

        mockMvc.perform(post("/webhook/cicd")
                        .header("CI-Event-Type", eventType)
                        .content(payloadJson))
                .andExpect(status().isAccepted())
                .andExpect(content().string(LogHandler.EVENT_ACCEPTED));

        verify(kafkaTemplate, times(1)).send(
                eq(TOPIC_BASE + eventType),
                eq(EVENT_KEY),
                eq(mockEvent)
        );
    }

    @Test
    void negativeDataTest_NotJson() throws Exception {
        String eventType = "issue";
        String payloadJson = "{not json}";

        when(objectMapper.readValue(payloadJson, IssueEvent.class))
                .thenThrow(new JsonProcessingException("Bad JSON") {});

        mockMvc.perform(post("/webhook/cicd")
                        .header("CI-Event-Type", eventType)
                        .content(payloadJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Json parse Error: Invalid payload structure")));

        verify(kafkaTemplate, never()).send(any(), any(), any());
    }

    @Test
    void unexpectedDataTest_MissingRepName() throws Exception {
        String eventType = "issue";

        String payloadJson = """
            {
                "author": "test author",
                "eventMessage": {
                    "action": "opened",
                    "issueTitle": "test title",
                    "issueNumber": 1
                },
                "eventUrl": "http://test.com"
            }
            """;

        IssueEvent invalidEvent = new IssueEvent(
                null,
                "test author",
                new IssueEvent.IssueMessage("opened", "test title", 1),
                "http://test.com"
        );

        when(objectMapper.readValue(payloadJson, IssueEvent.class)).thenReturn(invalidEvent);

        mockMvc.perform(post("/webhook/cicd")
                        .header("CI-Event-Type", eventType)
                        .content(payloadJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Validation Error: Repository cannot be blank")));

        verify(kafkaTemplate, never()).send(any(), any(), any());
    }
}
