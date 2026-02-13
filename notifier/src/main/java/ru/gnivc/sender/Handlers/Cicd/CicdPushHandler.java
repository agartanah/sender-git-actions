package ru.gnivc.sender.Handlers.Cicd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import ru.gnivc.sender.Dispatches.IEventHandler;

@Component
public class CicdPushHandler implements IEventHandler {
    @Override
    public String source() {
        return "cicd";
    }

    @Override
    public String eventType() {
        return "push";
    }

    @Override
    public String handle(JsonNode payload) {
        try {
            System.out.println("LOG: " + eventType() + " " + source());

            String repositoryName = payload.path("repositoryName").asText();
            String repositoryUrl = payload.path("eventUrl").asText();
            String author = payload.path("author").asText();

            String commitsRaw = payload.path("eventMessage").asText();

            if (commitsRaw == null || commitsRaw.isBlank()) {
                return "";
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode commitsNode = mapper.readTree(commitsRaw);

            if (!commitsNode.isArray() || commitsNode.isEmpty()) {
                return "";
            }

            StringBuilder message = new StringBuilder();
            message.append("Новые коммиты в репозитории:\n")
                    .append(repositoryName)
                    .append("\n\nОТ: ").append(author)
                    .append("\n\nСсылка на репозиторий:\n")
                    .append(repositoryUrl)
                    .append("\n");

            for (JsonNode commitNode : commitsNode) {
                String sha = commitNode.path("sha").asText();
                String commitMessage = commitNode.path("message").asText();

                message.append("\n— <b>")
                        .append(sha.length() >= 7 ? sha.substring(0, 7) : sha)
                        .append("</b>:\n")
                        .append("<i>")
                        .append(commitMessage)
                        .append("</i>\n")
                        .append(repositoryUrl)
                        .append("/commit/")
                        .append(sha)
                        .append("\n");
            }

            return message.toString();

        } catch (Exception e) {
            System.out.println("Error build message for CICD PUSH: " + e.getMessage());
            return "";
        }
    }
}
