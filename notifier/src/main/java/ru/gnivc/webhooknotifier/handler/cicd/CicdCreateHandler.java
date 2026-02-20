package ru.gnivc.webhooknotifier.handler.cicd;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.gnivc.webhooknotifier.dispatch.IEventHandler;

@Component
public class CicdCreateHandler implements IEventHandler {
    private static final Logger log =
            LoggerFactory.getLogger(CicdCreateHandler.class);

    @Override
    public String source() {
        return "cicd";
    }

    @Override
    public String eventType() {
        return "create";
    }

    @Override
    public String handle(JsonNode payload) {
        try {
            String repositoryName = payload.path("repositoryName").asText();
            String branchName = payload.path("eventMessage").asText();
            String author = payload.path("author").asText();
            String repositoryUrl = payload.path("eventUrl").asText();

            log.info("CICD CREATE. repo={}, branch={}, author={}",
                    repositoryName, branchName, author);

            return "Новая ветка <b>" + branchName + "</b> создана в репозитории:\n" +
                    repositoryName + "\n\nОТ: " + author +
                    "\n\nССЫЛКА:\n" +
                    repositoryUrl + "/tree/" + branchName;

        } catch (Exception e) {
            log.error("Error building CICD CREATE message", e);
            return "";
        }
    }
}
