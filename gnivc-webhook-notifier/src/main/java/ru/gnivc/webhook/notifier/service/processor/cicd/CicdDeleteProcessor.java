package ru.gnivc.webhook.notifier.service.processor.cicd;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.gnivc.webhook.notifier.service.processor.EventProcessor;
import ru.gnivc.webhook.notifier.util.*;
import ru.gnivc.webhooknotifier.util.*;

@Component
public class CicdDeleteProcessor implements EventProcessor {
    private static final Logger log =
            LoggerFactory.getLogger(CicdDeleteProcessor.class);

    @Override
    public String source() {
        return SourceUtil.CICD;
    }

    @Override
    public String eventType() {
        return EventTypeUtil.DELETE;
    }

    @Override
    public String handle(JsonNode payload) {
        try {
            String repositoryName = payload.path(JsonPathUtil.CICD_REPOSITORY_NAME).asText();
            String branchName = payload.path(JsonPathUtil.CICD_EVENT_MESSAGE).asText();
            String author = payload.path(JsonPathUtil.CICD_AUTHOR).asText();
            String repositoryUrl = payload.path(JsonPathUtil.CICD_EVENT_URL).asText();

            log.info(LoggingMessageUtil.CICD_DELETE_INFO,
                    repositoryName, branchName);

            return String.format(MessageTemplateUtil.BRANCH_DELETE,
                    branchName,
                    repositoryName,
                    author,
                    repositoryUrl
            );

        } catch (Exception e) {
            log.error(LoggingMessageUtil.CICD_DELETE_ERROR, e);
            return "";
        }
    }
}
