package ru.gnivc.webhooknotifier.util;

public final class LoggingMessageUtil {

    //SERVICES

    public static final String CONSUMER_SERVICE_INFO = "Received event. source={}, type={}";
    public static final String CONSUMER_SERVICE_ERROR = "Error processing event. source={}, type={}";

    public static final String NOTIFIER_TG_SERVICE_INFO = "Telegram message sent successfully";
    public static final String NOTIFIER_TG_SERVICE_ERROR = "Failed to send Telegram message";

    // PROCESSORS

    public static final String CICD_CREATE_INFO = "CICD CREATE. repo={}, branch={}, author={}";
    public static final String CICD_CREATE_ERROR = "Error building CICD CREATE message";

    public static final String CICD_DELETE_INFO = "CICD DELETE. repo={}, branch={}";
    public static final String CICD_DELETE_ERROR = "Error building CICD DELETE message";

    public static final String CICD_PUSH_INFO = "CICD PUSH. repo={}";
    public static final String CICD_PUSH_ERROR = "Error building CICD PUSH message";

    public static final String GITHUB_CREATE_INFO = "GitHub CREATE. repo={}, branch={}";
    public static final String GITHUB_CREATE_ERROR = "Error building GitHub CREATE message";

    public static final String GITHUB_DELETE_INFO = "GitHub DELETE. repo={}";
    public static final String GITHUB_DELETE_ERROR = "Error building CICD CREATE message";

    public static final String GITHUB_PUSH_INFO = "GitHub PUSH. repo={}";
    public static final String GITHUB_PUSH_ERROR = "Error building GitHub PUSH message";
}
