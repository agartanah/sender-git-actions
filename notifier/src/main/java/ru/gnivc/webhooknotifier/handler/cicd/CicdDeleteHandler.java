package ru.gnivc.webhooknotifier.handler.cicd;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.gnivc.webhooknotifier.dispatch.IEventHandler;

@Component
public class CicdDeleteHandler implements IEventHandler {
    private static final Logger log =
            LoggerFactory.getLogger(CicdDeleteHandler.class);

    @Override
    public String source() {
        return "cicd";
    }

    @Override
    public String eventType() {
        return "delete";
    }

    @Override
    public String handle(JsonNode payload) {
        try {
            String repositoryName = payload.path("repositoryName").asText();
            String branchName = payload.path("eventMessage").asText();
            String author = payload.path("author").asText();
            String repositoryUrl = payload.path("eventUrl").asText();

            log.info("CICD DELETE. repo={}, branch={}",
                    repositoryName, branchName);

            return "Ветка <b>" + branchName + "</b> была удалена в репозитории:\n" +
                    repositoryName + "\n\nОТ: " + author +
                    "\n\nРепозиторий:\n" +
                    repositoryUrl;

        } catch (Exception e) {
            log.error("Error building CICD DELETE message", e);
            return "";
        }
    }
}
