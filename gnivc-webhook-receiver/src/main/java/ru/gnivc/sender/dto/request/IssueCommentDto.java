package ru.gnivc.sender.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IssueCommentDto(
        @JsonProperty("repositoryName")
        @NotBlank(message = "Repository cannot be blank")
        String repositoryName,
        @JsonProperty("author") String commentAuthor,
        @JsonProperty("eventMessage") String message,
        @JsonProperty("eventUrl") String url
) { }