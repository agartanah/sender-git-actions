package ru.gnivc.webhooknotifier.handler.cicd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.gnivc.webhooknotifier.dispatch.IEventHandler;

@Component
public class CicdPushHandler implements IEventHandler {
    private final Logger log = LoggerFactory.getLogger(CicdPushHandler.class);

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

            log.info("CICD PUSH. repo={}", repositoryName);

            return message.toString();

        } catch (Exception e) {
            log.error("Error building CICD PUSH message", e);
            return "";
        }
    }
}
