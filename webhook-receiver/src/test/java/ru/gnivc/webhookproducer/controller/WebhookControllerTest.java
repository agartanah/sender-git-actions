package ru.gnivc.webhookproducer.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.gnivc.webhookproducer.service.ProducerService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebhookController.class)
class WebhookControllerTest {
    private final String URI_TEMPLATE_WEBHOOK = "/webhook";
    private final String URI_TEMPLATE_CICD = URI_TEMPLATE_WEBHOOK + "/cicd";
    private final String URI_TEMPLATE_GITHUB = URI_TEMPLATE_WEBHOOK + "/github";

    private final String SOURCE_CICD = "cicd";
    private final String SOURCE_GITHUB = "github";

    private final String EVENT_TYPE_PUSH = "push";

    private final String HEADER_CI = "CI-Event-Type";
    private final String HEADER_GITHUB = "X-GitHub-Event";

    private final String JSON_CICD = """
        {
          "repositoryName": "test-repo",
          "eventUrl": "https://github.com/test/test-repo",
          "author": "Vladislav",
          "eventMessage": "[{\\"sha\\":\\"abc123456789\\",\\"message\\":\\"Initial commit\\"},{\\"sha\\":\\"def987654321\\",\\"message\\":\\"Fix bug\\"}]"
        }
        """;


    private final String JSON_GITHUB = """
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

    private final String INVALID_JSON = """
        {
          invalid json
        }
        """;

    private final String RUNTIME_EXCEPTION_BROKER_ERROR = "Broker error";

    private final String EXPECTED_CONTENT_EVENT_ACCEPT_CICD = "Event accepted";
    private final String EXPECTED_CONTENT_EVENT_ERROR_CICD = "Event error";
    private final String EXPECTED_CONTENT_EVENT_ACCEPT_GITHUB_PUSH = "Event " + EVENT_TYPE_PUSH + " accepted";
    private final String EXPECTED_CONTENT_EVENT_ERROR_GITHUB_PUSH = "Event " + EVENT_TYPE_PUSH + " error";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProducerService producerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void cicdWebhook_success() throws Exception {
        mockMvc.perform(post(URI_TEMPLATE_CICD)
                        .header(HEADER_CI, EVENT_TYPE_PUSH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CICD))
                .andExpect(status().isOk())
                .andExpect(content().string(EXPECTED_CONTENT_EVENT_ACCEPT_CICD));

        verify(producerService)
                .sendAction(eq(SOURCE_CICD), eq(EVENT_TYPE_PUSH), any(JsonNode.class));
    }

    @Test
    void cicdWebhook_missingHeader() throws Exception {
        mockMvc.perform(post(URI_TEMPLATE_CICD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CICD))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cicdWebhook_serviceThrowsException() throws Exception {
        doThrow(new RuntimeException(RUNTIME_EXCEPTION_BROKER_ERROR))
                .when(producerService)
                .sendAction(eq(SOURCE_CICD), eq(EVENT_TYPE_PUSH), any(JsonNode.class));


        mockMvc.perform(post(URI_TEMPLATE_CICD)
                        .header(HEADER_CI, EVENT_TYPE_PUSH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_CICD))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(EXPECTED_CONTENT_EVENT_ERROR_CICD));
    }

    @Test
    void cicdWebhook_invalidJson() throws Exception {
        mockMvc.perform(post(URI_TEMPLATE_CICD)
                        .header(HEADER_CI, EVENT_TYPE_PUSH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void githubWebhook_success() throws Exception {
        mockMvc.perform(post(URI_TEMPLATE_GITHUB)
                        .header(HEADER_GITHUB, EVENT_TYPE_PUSH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_GITHUB))
                .andExpect(status().isOk())
                .andExpect(content().string(EXPECTED_CONTENT_EVENT_ACCEPT_GITHUB_PUSH));

        verify(producerService)
                .sendAction(eq(SOURCE_GITHUB), eq(EVENT_TYPE_PUSH), any(JsonNode.class));
    }

    @Test
    void githubWebhook_missingHeader() throws Exception {
        mockMvc.perform(post(URI_TEMPLATE_GITHUB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_GITHUB))
                .andExpect(status().isBadRequest());
    }

    @Test
    void githubWebhook_serviceThrowsException() throws Exception {

        doThrow(new RuntimeException(RUNTIME_EXCEPTION_BROKER_ERROR))
                .when(producerService)
                .sendAction(eq(SOURCE_GITHUB), eq(EVENT_TYPE_PUSH), any(JsonNode.class));

        mockMvc.perform(post(URI_TEMPLATE_GITHUB)
                        .header(HEADER_GITHUB, EVENT_TYPE_PUSH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_GITHUB))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(EXPECTED_CONTENT_EVENT_ERROR_GITHUB_PUSH));

        verify(producerService)
                .sendAction(eq(SOURCE_GITHUB), eq(EVENT_TYPE_PUSH), any(JsonNode.class));
    }

    @Test
    void githubWebhook_invalidJson() throws Exception {
        mockMvc.perform(post(URI_TEMPLATE_GITHUB)
                        .header(HEADER_GITHUB, EVENT_TYPE_PUSH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_JSON))
                .andExpect(status().isBadRequest());
    }
}
