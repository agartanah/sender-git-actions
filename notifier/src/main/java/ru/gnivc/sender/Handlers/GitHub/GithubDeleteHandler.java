package ru.gnivc.sender.Handlers.GitHub;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import ru.gnivc.sender.Dispatches.IEventHandler;

@Component
public class GithubDeleteHandler implements IEventHandler {
    @Override
    public String source() {
        return "github";
    }

    @Override
    public String eventType() {
        return "delete";
    }

    @Override
    public String handle(JsonNode payload) {
        try {
            System.out.println("LOG: " + eventType() + " " + source());

            String repositoryUrl = payload.path("repository").path("html_url").asText();
            String branchName = payload.path("ref").asText();
            String author = payload.path("sender").path("login").asText();

            String message = "Ветка <b>" + branchName + "</b> была удалена в репозитории:\n" +
                    repositoryUrl + "\n\nОТ: " + author;

            return message;
        } catch (Exception e) {
            System.out.println("Error build message for GitHub CREATE: " + e.getMessage());
            return "";
        }
    }
}
