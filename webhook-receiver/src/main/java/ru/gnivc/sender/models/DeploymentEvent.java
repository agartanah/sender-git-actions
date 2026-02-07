package ru.gnivc.sender.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeploymentEvent(
        @JsonProperty("deployment") DeploymentInfo deployment,
        @JsonProperty("repository") RepositoryInfo repository,
        @JsonProperty("workflow_run") WorkflowRunInfo workflowRun,
        @JsonProperty("action") String action
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DeploymentInfo(
            @JsonProperty("environment") String environment,
            @JsonProperty("description") String description,
            @JsonProperty("creator") UserInfo creator
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RepositoryInfo(
            @JsonProperty("full_name") String fullName,
            @JsonProperty("html_url") String htmlUrl
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WorkflowRunInfo(
            @JsonProperty("html_url") String htmlUrl,
            @JsonProperty("status") String status,
            @JsonProperty("conclusion") String conclusion
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record UserInfo(
            @JsonProperty("login") String login
    ) {}

    public String getRepositoryName() {
        return repository != null ? repository.fullName() : "";
    }

    public String getRepositoryUrl() {
        return repository != null ? repository.htmlUrl() : "";
    }

    public String getEnvironment() {
        return deployment != null ? deployment.environment() : "";
    }

    public String getDeployer() {
        return deployment != null && deployment.creator() != null ?
                deployment.creator().login() : null;
    }

    public String getWorkflowRunUrl() {
        return workflowRun != null ? workflowRun.htmlUrl() : null;
    }

    public String getStatus() {
        return workflowRun != null ? workflowRun.status() : "unknown";
    }
}