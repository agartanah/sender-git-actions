package ru.gnivc.sender.Handlers.GitHub;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import ru.gnivc.sender.Dispatches.IEventHandler;

@Component
public class GithubCreateHandler implements IEventHandler {
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
            System.out.println("LOG: " + eventType() + " " + source());

            String repositoryUrl = payload.path("repository").path("html_url").asText();
            String branchName = payload.path("ref").asText();
            String author = payload.path("sender").path("login").asText();

            return "Новая ветка <b>" + branchName + "</b> в репозитории:\n" +
                    repositoryUrl + "\n\nОТ: " + author + "\n\nССЫЛКА: \n" +
                    repositoryUrl + "/tree/" + branchName;
        } catch (Exception e) {
            System.out.println("Error build message for GitHub DELETE: " + e.getMessage());
            return "";
        }
    }
}
