package ru.gnivc.sender.util;

import ru.gnivc.sender.dto.response.DeploymentDto;
import ru.gnivc.sender.dto.response.IssueCommentDto;
import ru.gnivc.sender.dto.response.IssueDto;

public class TgMessageUtil {
    public static String createDeploymentMessage(DeploymentDto deploymentDto){
        return "🔥 Деплой 🔥"
                + "\nРепозиторий: " + deploymentDto.repositoryName()
                + "\nСостояние: " + deploymentDto.environment()
                + "\nСделал деплой: " + deploymentDto.deployer()
                + "\n" + deploymentDto.url();
    }

    public static String createIssueCommentMessage(IssueCommentDto issueCommentDto){
        return "🔥 Комментарий в " + issueCommentDto.repositoryName() + " 🔥"
                + "\nКомментарий от: " + issueCommentDto.commentAuthor()
                + "\n«" + issueCommentDto.message() + "»"
                + "\n" + issueCommentDto.url();
    }

    public static String createIssueMessage(IssueDto issueDto){
        String issueActionText = switch (issueDto.issueMessage().action()) {
            case "opened" -> "Открыта";
            case "closed" -> "Закрыта";
            case "reopened" -> "Открыта повторно";
            default -> issueDto.issueMessage().action();
        };

        return "🔥 Проблема " + issueDto.issueMessage().issueTitle() + " #" + issueDto.issueMessage().issueNumber() + " : " + issueActionText + " 🔥"
                + "\nРепозиторий: " + issueDto.repositoryName()
                + "\nСовершил действие: " + issueDto.author()
                + "\n" + issueDto.url();
    }
}
