package ru.gnivc.webhooknotifier.service.processor.github;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.gnivc.webhooknotifier.service.IEventHandler;
import ru.gnivc.webhooknotifier.util.*;

@Component
public class GithubCreateProcessor implements IEventHandler {
    private static final Logger log =
            LoggerFactory.getLogger(GithubCreateProcessor.class);

    @Override
    public String source() {
        return SourceUtil.GITHUB;
    }

    @Override
    public String eventType() {
        return EventTypeUtil.CREATE;
    }

    @Override
    public String handle(JsonNode payload) {
        try {
            String repositoryUrl = payload.path(JsonPathUtil.GITHUB_REPOSITORY).path(JsonPathUtil.GITHUB_HTML_URL).asText();
            String branchName = payload.path(JsonPathUtil.GITHUB_REF).asText();
            String author = payload.path(JsonPathUtil.GITHUB_SENDER).path(JsonPathUtil.GITHUB_LOGIN).asText();

            log.info(LoggingMessageUtil.GITHUB_CREATE_INFO,
                    repositoryUrl, branchName);

            return String.format(
                    MessageTemplateUtil.NEW_BRANCH_CREATE,
                    branchName,
                    repositoryUrl,
                    author,
                    repositoryUrl,
                    branchName
            );
        } catch (Exception e) {
            log.error(LoggingMessageUtil.GITHUB_CREATE_ERROR, e);
            return "";
        }
    }
}
