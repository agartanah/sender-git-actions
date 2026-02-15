package ru.gnivc.sender.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PullRequestEvent(
        @JsonProperty("repositoryName") String repositoryName,
        @JsonProperty("author") String author,
        @JsonProperty("eventMessage") String action,
        @JsonProperty("eventUrl") String url
) { }