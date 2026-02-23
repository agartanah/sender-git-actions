package ru.gnivc.webhooknotifier.service.processor.cicd;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.gnivc.webhooknotifier.service.IEventHandler;
import ru.gnivc.webhooknotifier.util.*;

@Component
public class CicdCreateProcessor implements IEventHandler {
    private static final Logger log =
            LoggerFactory.getLogger(CicdCreateProcessor.class);

    @Override
    public String source() {
        return SourceUtil.CICD;
    }

    @Override
    public String eventType() {
        return EventTypeUtil.CREATE;
    }

    @Override
    public String handle(JsonNode payload) {
        try {
            String repositoryName = payload.path(JsonPathUtil.CICD_REPOSITORY_NAME).asText();
            String branchName = payload.path(JsonPathUtil.CICD_EVENT_MESSAGE).asText();
            String author = payload.path(JsonPathUtil.CICD_AUTHOR).asText();
            String repositoryUrl = payload.path(JsonPathUtil.CICD_EVENT_URL).asText();

            log.info(LoggingMessageUtil.CICD_CREATE_INFO,
                    repositoryName, branchName, author);

            return String.format(
                    MessageTemplateUtil.NEW_BRANCH_CREATE,
                    branchName,
                    repositoryName,
                    author,
                    repositoryUrl,
                    branchName
            );

        } catch (Exception e) {
            log.error(LoggingMessageUtil.CICD_CREATE_ERROR, e);
            return "";
        }
    }
}
