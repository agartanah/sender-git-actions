package ru.gnivc.webhooknotifier.service.processor.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GithubPushProcessorTest {
    private static final String JSON_WITH_COMMITS = """
        {
          "repository":{
            "html_url":"url"
          },
          "commits":[
            {
              "message":"msg",
              "url":"commitUrl",
              "author":{
                "name":"user"
              }
            }
          ]
        }
        """;

    private static final String JSON_WITHOUT_COMMITS = """
        {
          "repository":{
            "html_url":"url"
          },
          "commits":[]
        }
        """;

    private static final String RESULT_CONTAINS_MSG = "msg";
    private static final String RESULT_CONTAINS_EMPTY = "";

    private static final GithubPushProcessor processor =
            new GithubPushProcessor();

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
    void handle_emptyCommits() throws Exception {
        JsonNode payload = mapper.readTree(JSON_WITHOUT_COMMITS);

        String result =
                processor.handle(payload);

        assertEquals(RESULT_CONTAINS_EMPTY, result);
    }
}
