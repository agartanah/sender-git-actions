package ru.gnivc.sender.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IssueEvent(
        @JsonProperty("repositoryName") String repositoryName,
        @JsonProperty("author") String author,
        @JsonProperty("eventMessage") IssueMessage issueMessage,
        @JsonProperty("eventUrl") String url
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record IssueMessage(
            @JsonProperty("action") String action,
            @JsonProperty("issueTitle") String issueTitle,
            @JsonProperty("issueNumber") Integer issueNumber
    ) { }
}

