package ru.gnivc.webhooknotifier.handler.github;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.gnivc.webhooknotifier.dispatch.IEventHandler;

@Component
public class GithubPushHandler implements IEventHandler {
    private static final Logger log =
            LoggerFactory.getLogger(GithubPushHandler.class);

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
            String repositoryUrl = payload.path("repository").path("html_url").asText();
            JsonNode commitsNode = payload.path("commits");

            String message = "Новые коммиты в репозитории:\n" + repositoryUrl + "\n";

            if (!commitsNode.isArray() || commitsNode.isEmpty()) {
                return "";
            }

            for (JsonNode commitNode : commitsNode) {
                String messageCommit = commitNode.path("message").asText();
                String authorCommit = commitNode.path("author").path("name").asText();
                String urlCommit = commitNode.path("url").asText();

                message += "\nОТ: <b>" + authorCommit + "</b>\nСообщение: <i>" + messageCommit + "</i>\nСсылка: " + urlCommit;
            }

            log.info("GitHub PUSH. repo={}", repositoryUrl);

            return message;
        } catch (Exception e) {
            log.error("Error building GitHub PUSH message", e);
            return "";
        }
    }
}
