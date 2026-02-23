package ru.gnivc.webhookproducer.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.gnivc.webhookproducer.service.ProducerService;
import ru.gnivc.webhookproducer.util.EndpointsUtil;
import ru.gnivc.webhookproducer.util.EventTypeUtil;
import ru.gnivc.webhookproducer.util.HeaderUtil;
import ru.gnivc.webhookproducer.util.SourceUtil;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebhookController.class)
class WebhookControllerTest {
    private static final String JSON_CICD = """
        {
          "repositoryName": "test-repo",
          "eventUrl": "https://github.com/test/test-repo",
          "author": "Vladislav",
          "eventMessage": "[{\\"sha\\":\\"abc123456789\\",\\"message\\":\\"Initial commit\\"}]"
        }
        """;

    private static final String JSON_GITHUB = """
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
            }
          ]
        }
        """;

    private static final String INVALID_JSON = """
        { invalid json }
        """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProducerService producerService;

    @MockitoBean
    private JmsTemplate jmsTemplate;

    @Test
    void cicdWebhook_success() throws Exception {
        when(producerService.sendAction(
                eq(SourceUtil.CICD),
                eq(EventTypeUtil.PUSH),
                any(JsonNode.class)
        )).thenReturn(ResponseEntity.ok(EndpointsUtil.RESPONSE_OK));

        mockMvc.perform(post(EndpointsUtil.ENDPOINT_WEBHOOK + EndpointsUtil.ENDPOINT_WEBHOOK_CICD)
                        .header(HeaderUtil.CICD, EventTypeUtil.PUSH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CICD))
                .andExpect(status().isOk())
                .andExpect(content().string(EndpointsUtil.RESPONSE_OK));
    }

    @Test
    void cicdWebhook_jmsException_returns500() throws Exception {
        when(producerService.sendAction(
                eq(SourceUtil.CICD),
                eq(EventTypeUtil.PUSH),
                any(JsonNode.class)
        )).thenReturn(
                ResponseEntity.internalServerError()
                        .body(EndpointsUtil.RESPONSE_SERVER_ERROR)
        );

        mockMvc.perform(post(EndpointsUtil.ENDPOINT_WEBHOOK + EndpointsUtil.ENDPOINT_WEBHOOK_CICD)
                        .header(HeaderUtil.CICD, EventTypeUtil.PUSH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CICD))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(EndpointsUtil.RESPONSE_SERVER_ERROR));
    }

    @Test
    void cicdWebhook_missingHeader() throws Exception {
        mockMvc.perform(post(EndpointsUtil.ENDPOINT_WEBHOOK + EndpointsUtil.ENDPOINT_WEBHOOK_CICD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CICD))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cicdWebhook_invalidJson() throws Exception {

        mockMvc.perform(post(EndpointsUtil.ENDPOINT_WEBHOOK + EndpointsUtil.ENDPOINT_WEBHOOK_CICD)
                        .header(HeaderUtil.CICD, EventTypeUtil.PUSH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void githubWebhook_success() throws Exception {
        when(producerService.sendAction(
                eq(SourceUtil.GITHUB),
                eq(EventTypeUtil.PUSH),
                any(JsonNode.class)
        )).thenReturn(ResponseEntity.ok(EndpointsUtil.RESPONSE_OK));

        mockMvc.perform(post(EndpointsUtil.ENDPOINT_WEBHOOK + EndpointsUtil.ENDPOINT_WEBHOOK_GITHUB)
                        .header(HeaderUtil.GITHUB, EventTypeUtil.PUSH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_GITHUB))
                .andExpect(status().isOk())
                .andExpect(content().string(EndpointsUtil.RESPONSE_OK));
    }

    @Test
    void githubWebhook_jmsException_returns500() throws Exception {
        when(producerService.sendAction(
                eq(SourceUtil.GITHUB),
                eq(EventTypeUtil.PUSH),
                any(JsonNode.class)
        )).thenReturn(
                ResponseEntity.internalServerError()
                        .body(EndpointsUtil.RESPONSE_SERVER_ERROR)
        );

        mockMvc.perform(post(EndpointsUtil.ENDPOINT_WEBHOOK + EndpointsUtil.ENDPOINT_WEBHOOK_GITHUB)
                        .header(HeaderUtil.GITHUB, EventTypeUtil.PUSH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_GITHUB))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(EndpointsUtil.RESPONSE_SERVER_ERROR));
    }

    @Test
    void githubWebhook_missingHeader() throws Exception {
        mockMvc.perform(post(EndpointsUtil.ENDPOINT_WEBHOOK + EndpointsUtil.ENDPOINT_WEBHOOK_GITHUB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_GITHUB))
                .andExpect(status().isBadRequest());
    }

    @Test
    void githubWebhook_invalidJson() throws Exception {
        mockMvc.perform(post(EndpointsUtil.ENDPOINT_WEBHOOK + EndpointsUtil.ENDPOINT_WEBHOOK_GITHUB)
                        .header(HeaderUtil.GITHUB, EventTypeUtil.PUSH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_JSON))
                .andExpect(status().isBadRequest());
    }
}