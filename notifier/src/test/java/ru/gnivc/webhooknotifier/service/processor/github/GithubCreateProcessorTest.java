package ru.gnivc.webhooknotifier.service.processor.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GithubCreateProcessorTest {
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

    private static final String RESULT_CONTAINS_BRANCH = "branch";

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final GithubCreateProcessor processor =
            new GithubCreateProcessor();

    @Test
    void processor_success() throws Exception {
        JsonNode payload = mapper.readTree(JSON);

        String result =
                processor.handle(payload);

        assertTrue(result.contains(RESULT_CONTAINS_BRANCH));
    }
}
