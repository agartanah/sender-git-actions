package ru.gnivc.webhook.receiver.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IssueDto(
        @JsonProperty("repositoryName")
        @NotBlank(message = "Repository cannot be blank")
        String repositoryName,
        @JsonProperty ("author") String author,
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