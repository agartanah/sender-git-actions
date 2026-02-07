package ru.gnivc.sender.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PullRequestEvent(
        @JsonProperty("action") String action,
        @JsonProperty("number") int number,
        @JsonProperty("pull_request") PullRequestInfo pullRequest,
        @JsonProperty("repository") RepositoryInfo repository
) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record PullRequestInfo(
                @JsonProperty("html_url") String htmlUrl,
                @JsonProperty("title") String title,
                @JsonProperty("state") String state,
                @JsonProperty("merged_at") String mergedAt,
                @JsonProperty("user") UserInfo author,
                @JsonProperty("additions") int additions,
                @JsonProperty("deletions") int deletions,
                @JsonProperty("commits") int commits
        ) {}

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record RepositoryInfo(
                @JsonProperty("full_name") String fullName,
                @JsonProperty("html_url") String htmlUrl
        ) {}

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record UserInfo(
                @JsonProperty("login") String login
        ) {}

        public String getRepositoryName() {
                return repository != null ? repository.fullName() : "";
        }

        public String getPullRequestTitle() {
                return pullRequest != null ? pullRequest.title() : "";
        }

        public String getPullRequestUrl() {
                return pullRequest != null ? pullRequest.htmlUrl() : "";
        }

        public String getAuthor() {
                return pullRequest != null && pullRequest.author() != null ? pullRequest.author().login() : "";
        }
}