package ru.gnivc.sender.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CommitCommentEvent(
        @JsonProperty("comment") Comment comment,
        @JsonProperty("repository") Repository repository,
        @JsonProperty("sender") Sender sender
) {
        public String getCommentHtmlUrl() {
                return comment != null ? comment.htmlUrl() : "";
        }

        public String getRepositoryName() {
                return repository != null ? repository.fullName() : "";
        }

        public String getBranchName() {
                return repository != null ? repository.defaultBranch() : "";
        }

        public String getRepositoryUrl() {
                return repository != null ? repository.htmlUrl() : "";
        }

        public String getCommentAuthor() {
                return comment != null ? comment.getUserLogin() : "";
        }

        public String getCommentText() {
                return comment != null ? comment.body() : "";
        }

        public String getCommitId() {
                return comment != null ? comment.commitId() : "";
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Repository(
                @JsonProperty("full_name") String fullName,
                @JsonProperty("html_url") String htmlUrl,
                @JsonProperty("default_branch") String defaultBranch
        ) {}

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Comment(
                @JsonProperty("html_url") String htmlUrl,
                @JsonProperty("body") String body,
                @JsonProperty("commit_id") String commitId,
                @JsonProperty("user") User user
        ) {
                public String getUserLogin() {
                        return user != null ? user.login() : "";
                }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record User(
                @JsonProperty("login") String login
        ) { }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Sender(
                @JsonProperty("login") String login
        ) {}
}