package ru.gnivc.webhook.notifier.service.processor.cicd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CicdCreateProcessorTest {
    private static final String JSON = """
        {
          "repositoryName": "repo",
          "eventMessage": "branch",
          "author": "user",
          "eventUrl": "http://git"
        }
        """;

    private static final String RESULT_CONTAINS_REPOSITORY = "repo";
    private static final String RESULT_CONTAINS_BRANCH = "branch";

    private static final CicdCreateProcessor processor =
            new CicdCreateProcessor();

    private static final ObjectMapper mapper =
            new ObjectMapper();

    @Test
    void processor_success() throws Exception {
        JsonNode payload = mapper.readTree(JSON);

        String result =
                processor.handle(payload);

        assertTrue(result.contains(RESULT_CONTAINS_BRANCH));
        assertTrue(result.contains(RESULT_CONTAINS_REPOSITORY));
    }
}
