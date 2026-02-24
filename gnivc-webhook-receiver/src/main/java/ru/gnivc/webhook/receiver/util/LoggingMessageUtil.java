package ru.gnivc.webhook.receiver.util;

public class LoggingMessageUtil {
    public static final String SEND_ACTION_INFO = """
        Message sent:
            source: {}
            type: {}
            payload: {}
        """;

    public static final String SEND_ACTION_ERROR = "Error sending action. source={}, type={}";
}
