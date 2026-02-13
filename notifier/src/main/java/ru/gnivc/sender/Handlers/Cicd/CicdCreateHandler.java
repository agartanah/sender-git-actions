package ru.gnivc.sender.Handlers.Cicd;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import ru.gnivc.sender.Dispatches.IEventHandler;

@Component
public class CicdCreateHandler implements IEventHandler {
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
            System.out.println("LOG: " + eventType() + " " + source());

            String repositoryName = payload.path("repositoryName").asText();
            String branchName = payload.path("eventMessage").asText();
            String author = payload.path("author").asText();
            String repositoryUrl = payload.path("eventUrl").asText();

            return "Новая ветка <b>" + branchName + "</b> создана в репозитории:\n" +
                    repositoryName + "\n\nОТ: " + author +
                    "\n\nССЫЛКА:\n" +
                    repositoryUrl + "/tree/" + branchName;

        } catch (Exception e) {
            System.out.println("Error build message for CICD CREATE: " + e.getMessage());
            return "";
        }
    }
}
