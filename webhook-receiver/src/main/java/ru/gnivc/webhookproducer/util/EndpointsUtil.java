package ru.gnivc.webhookproducer.util;

public final class EndpointsUtil {
    public static final String ENDPOINT_WEBHOOK = "/webhook";

    public static final String ENDPOINT_WEBHOOK_CICD = "/cicd";
    public static final String ENDPOINT_WEBHOOK_GITHUB = "/github";
    public static final String ENDPOINT_WEBHOOK_GITLAB = "/gitlab";
    public static final String ENDPOINT_WEBHOOK_GITFLIC = "/gitflic";

    public static final String RESPONSE_OK = "Event accepted";
    public static final String RESPONSE_SERVER_ERROR = "Event error";
}
