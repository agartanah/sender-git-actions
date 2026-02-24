package ru.gnivc.webhook.notifier.service.processor.github;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.gnivc.webhook.notifier.service.processor.EventProcessor;
import ru.gnivc.webhook.notifier.util.JsonPathUtil;
import ru.gnivc.webhook.notifier.util.LoggingMessageUtil;
import ru.gnivc.webhook.notifier.util.MessageTemplateUtil;

@Component
public class GithubPushProcessor implements EventProcessor {
    private static final Logger log =
            LoggerFactory.getLogger(GithubPushProcessor.class);

    @Override
    public String source() {
        return "github";
    }

    @Override
    public String eventType() {
        return "push";
    }

    @Override
    public String handle(JsonNode payload) {
        try {
            String author = payload.path(JsonPathUtil.GITHUB_SENDER).path(JsonPathUtil.GITHUB_LOGIN).asText();
            String repositoryName = payload.path(JsonPathUtil.GITHUB_REPOSITORY).path(JsonPathUtil.GITHUB_REPOSITORY_NAME).asText();
            String repositoryUrl = payload.path(JsonPathUtil.GITHUB_REPOSITORY).path(JsonPathUtil.GITHUB_HTML_URL).asText();

            JsonNode commitsNode = payload.path(JsonPathUtil.GITHUB_COMMITS);

            StringBuilder message = new StringBuilder(
                    String.format(
                            MessageTemplateUtil.COMMITS_HEADER,
                            repositoryName,
                            author,
                            repositoryUrl
                    )
            );

            if (!commitsNode.isArray() || commitsNode.isEmpty()) {
                return "";
            }

            for (JsonNode commitNode : commitsNode) {
                String sha = commitNode.path(JsonPathUtil.GITHUB_COMMITS_ID).asText();
                String commitMessage = commitNode.path(JsonPathUtil.GITHUB_COMMITS_MESSAGE).asText();
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

            log.info(LoggingMessageUtil.GITHUB_PUSH_INFO, repositoryUrl);

            return message.toString();
        } catch (Exception e) {
            log.error(LoggingMessageUtil.GITHUB_PUSH_ERROR, e);
            return "";
        }
    }
}
