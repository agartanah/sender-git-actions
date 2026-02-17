package ru.gnivc.sender.util;

import ru.gnivc.sender.dto.response.DeploymentEvent;
import ru.gnivc.sender.dto.response.IssueCommentEvent;
import ru.gnivc.sender.dto.response.IssueEvent;

public class TgMessageHandler {
    public static String createDeploymentMessage(DeploymentEvent deploymentEvent){
        return "🔥 Деплой 🔥"
                + "\nРепозиторий: " + deploymentEvent.repositoryName()
                + "\nСостояние: " + deploymentEvent.environment()
                + "\nСделал деплой: " + deploymentEvent.deployer()
                + "\n" + deploymentEvent.url();
    }

    public static String createIssueCommentMessage(IssueCommentEvent issueCommentEvent){
        return "🔥 Комментарий в " + issueCommentEvent.repositoryName() + " 🔥"
                + "\nКомментарий от: " + issueCommentEvent.commentAuthor()
                + "\n«" + issueCommentEvent.message() + "»"
                + "\n" + issueCommentEvent.url();
    }

    public static String createIssueMessage(IssueEvent issueEvent){
        String issueActionText = switch (issueEvent.issueMessage().action()) {
            case "opened" -> "Открыта";
            case "closed" -> "Закрыта";
            case "reopened" -> "Открыта повторно";
            default -> issueEvent.issueMessage().action();
        };

        return "🔥 Проблема " + issueEvent.issueMessage().issueTitle() + " #" + issueEvent.issueMessage().issueNumber() + " : " + issueActionText + " 🔥"
                + "\nРепозиторий: " + issueEvent.repositoryName()
                + "\nСовершил действие: " + issueEvent.author()
                + "\n" + issueEvent.url();
    }
}
