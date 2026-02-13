package ru.gnivc.sender.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IssueCommentEvent(
        @JsonProperty("repositoryName") String repositoryName,
        @JsonProperty("author") String commentAuthor,
        @JsonProperty("eventMessage") String message,
        @JsonProperty("eventUrl") String url
) { }