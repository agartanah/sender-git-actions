package ru.gnivc.webhooknotifier.service.processor.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GithubDeleteProcessorTest {
    private static final String JSON = """
        {
          "ref":"branch",
          "repository":{
            "html_url":"url"
          },
          "sender":{
            "login":"user"
          }
        }
        """;

    private static final String RESULT_CONTAINS_DELETE = "удалена";

    private static final GithubDeleteProcessor processor =
            new GithubDeleteProcessor();

    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    void processor_success() throws Exception {
        JsonNode payload = mapper.readTree(JSON);

        String result =
                processor.handle(payload);

        assertTrue(result.contains(RESULT_CONTAINS_DELETE));
    }
}
