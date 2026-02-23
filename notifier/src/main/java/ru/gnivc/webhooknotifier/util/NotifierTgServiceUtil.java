package ru.gnivc.webhooknotifier.util;

public final class NotifierTgServiceUtil {
    public static final String SEND_MESSAGE_URL_TEMPLATE =
            "https://api.telegram.org/bot%s/sendMessage";

    public static final String CHAT_ID_FIELD = "chat_id";
    public static final String TEXT_FIELD = "text";
    public static final String PARSE_MODE_FIELD = "parse_mode";

    public static final String PARSE_MODE_HTML = "HTML";
}
