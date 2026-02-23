package ru.gnivc.webhooknotifier.util;

public final class MessageTemplateUtil {
    public static final String NEW_BRANCH_CREATE = """
        Новая ветка <b>%s</b> создана в репозитории:
        %s
        
        ОТ: %s
        
        ССЫЛКА:
        %s/tree/%s
        """;

    public static final String BRANCH_DELETE = """
        Ветка <b>%s</b> была удалена в репозитории:
        %s
        
        ОТ: %s
        
        Репозиторий:
        %s
        """;

    public static final String COMMITS_HEADER = """
        Новые коммиты в репозитории:
        %s
        
        ОТ: %s
        
        Ссылка на репозиторий:
        %s
        
        """;

    public static final String COMMIT_ITEM = """
        — <b>%s</b>:
        <i>%s</i>
        %s/commit/%s
        
        """;
}
