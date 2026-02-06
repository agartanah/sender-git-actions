package ru.gnivc.sender.models;

public record PullRequestEvent(
        String repositoryName,
        String branch,
        String author,
        String commitMessage,
        String url
) { }