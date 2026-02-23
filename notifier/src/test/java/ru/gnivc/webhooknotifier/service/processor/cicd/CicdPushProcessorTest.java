package ru.gnivc.webhooknotifier.service.processor.cicd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CicdPushProcessorTest {
    private static final String JSON_WITH_COMMITS = """
        {
          "repositoryName": "repo",
          "author": "user",
          "eventUrl": "http://git",
          "eventMessage": "[{\\"sha\\":\\"1234567\\",\\"message\\":\\"commit message\\"}]"
        }
        """;

    private static final String JSON_WITHOUT_COMMITS = """
        {
          "eventMessage": ""
        }
        """;

    private static final String RESULT_CONTAINS_MSG = "commit message";
    private static final String RESULT_CONTAINS_EMPTY = "";

    private static final CicdPushProcessor processor =
            new CicdPushProcessor();

    private static final ObjectMapper mapper =
            new ObjectMapper();

    @Test
    void processor_success() throws Exception {
        JsonNode payload = mapper.readTree(JSON_WITH_COMMITS);

        String result =
                processor.handle(payload);

        assertTrue(result.contains(RESULT_CONTAINS_MSG));
    }

    @Test
    void processor_emptyCommits() throws Exception {
        JsonNode payload = mapper.readTree(JSON_WITHOUT_COMMITS);

        String result =
                processor.handle(payload);

        assertEquals(RESULT_CONTAINS_EMPTY, result);
    }
}
