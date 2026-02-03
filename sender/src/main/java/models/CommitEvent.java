package models;

public record CommitEvent(
        String repositoryName,
        String branch,
        String author,
        String commitMessage,
        String commitUrl
) { }
