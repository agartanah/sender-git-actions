package ru.gnivc.sender.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeploymentDto(
        @JsonProperty("repositoryName")
        @NotBlank(message = "Repository cannot be blank")
        String repositoryName,
        @JsonProperty("author") String deployer,
        @JsonProperty("eventMessage") String environment,
        @JsonProperty("eventUrl") String url
) { }