package ru.gnivc.sender.Handlers.GitHub;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import ru.gnivc.sender.Dispatches.IEventHandler;

@Component
public class GithubPushHandler implements IEventHandler {

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

            return message;
        } catch (Exception e) {
            System.out.println("Error build message for GitHub PUSH: " + e.getMessage());
            return "";
        }
    }
}
