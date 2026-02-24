package ru.gnivc.webhook.notifier.service.processor.cicd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.gnivc.webhook.notifier.service.processor.EventProcessor;
import ru.gnivc.webhook.notifier.util.*;
import ru.gnivc.webhooknotifier.util.*;

@Component
public class CicdPushProcessor implements EventProcessor {
    private final Logger log = LoggerFactory.getLogger(CicdPushProcessor.class);

    @Override
    public String source() {
        return SourceUtil.CICD;
    }

    @Override
    public String eventType() {
        return EventTypeUtil.PUSH;
    }

    @Override
    public String handle(JsonNode payload) {
        try {
            String repositoryName = payload.path(JsonPathUtil.CICD_REPOSITORY_NAME).asText();
            String repositoryUrl = payload.path(JsonPathUtil.CICD_EVENT_URL).asText();
            String author = payload.path(JsonPathUtil.CICD_AUTHOR).asText();
            String commitsRaw = payload.path(JsonPathUtil.CICD_EVENT_MESSAGE).asText();

            if (commitsRaw == null || commitsRaw.isBlank()) {
                return "";
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode commitsNode = mapper.readTree(commitsRaw);

            if (!commitsNode.isArray() || commitsNode.isEmpty()) {
                return "";
            }

            StringBuilder message = new StringBuilder(
                    String.format(
                            MessageTemplateUtil.COMMITS_HEADER,
                            repositoryName,
                            author,
                            repositoryUrl
                    )
            );

            for (JsonNode commitNode : commitsNode) {
                String sha = commitNode.path(JsonPathUtil.CICD_COMMIT_SHA).asText();
                String commitMessage = commitNode.path(JsonPathUtil.CICD_COMMIT_MESSAGE).asText();
                String shortSha =
                        sha.length() >= 7 ? sha.substring(0, 7) : sha;

                message.append(
                        String.format(
                            MessageTemplateUtil.COMMIT_ITEM,
                            shortSha,
                            commitMessage,
                            repositoryUrl,
                            sha
                        )
                );
            }

            log.info(LoggingMessageUtil.CICD_PUSH_INFO, repositoryName);

            return message.toString();

        } catch (Exception e) {
            log.error(LoggingMessageUtil.CICD_PUSH_ERROR, e);
            return "";
        }
    }
}
