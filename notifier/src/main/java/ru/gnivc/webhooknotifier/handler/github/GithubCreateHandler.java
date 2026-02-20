package ru.gnivc.webhooknotifier.handler.github;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.gnivc.webhooknotifier.dispatch.IEventHandler;

@Component
public class GithubCreateHandler implements IEventHandler {
    private static final Logger log =
            LoggerFactory.getLogger(GithubCreateHandler.class);

    @Override
    public String source() {
        return "github";
    }

    @Override
    public String eventType() {
        return "create";
    }

    @Override
    public String handle(JsonNode payload) {
        try {
            String repositoryUrl = payload.path("repository").path("html_url").asText();
            String branchName = payload.path("ref").asText();
            String author = payload.path("sender").path("login").asText();

            log.info("GitHub CREATE. repo={}, branch={}",
                    repositoryUrl, branchName);

            return "Новая ветка <b>" + branchName + "</b> в репозитории:\n" +
                    repositoryUrl + "\n\nОТ: " + author + "\n\nССЫЛКА: \n" +
                    repositoryUrl + "/tree/" + branchName;
        } catch (Exception e) {
            log.error("Error building GitHub CREATE message", e);
            return "";
        }
    }
}
