package ru.gnivc.sender.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeploymentEvent(
        @JsonProperty("repositoryName") String repositoryName,
        @JsonProperty("author") String deployer,
        @JsonProperty("eventMessage") String environment,
        @JsonProperty("eventUrl") String url
) { }