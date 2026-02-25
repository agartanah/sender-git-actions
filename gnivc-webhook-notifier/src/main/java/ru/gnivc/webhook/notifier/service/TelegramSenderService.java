package ru.gnivc.webhook.notifier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.gnivc.webhook.notifier.util.ErrHandlerUtil;
import ru.gnivc.webhook.notifier.util.LogHandlerUtil;

public class TelegramSenderService extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(TelegramSenderService.class);
    @Value("${telegram.bot.token}")
    private String BOT_TOKEN;
    @Value("${telegram.chat.id}")
    private String CHAT_ID;

    @Override
    public String getBotUsername() {
        return "MyGitBot";
    }

    @Override
    public void onUpdateReceived(Update update) { }

    public void sendMessageToChat(String text){
        SendMessage message = new SendMessage();
        message.setChatId(CHAT_ID);
        message.setText(text);
        try{
            execute(message);
            log.info(LogHandlerUtil.TRY_SEND_MESSAGE);
        } catch (TelegramApiException e){
            log.error(ErrHandlerUtil.FAILED_SEND_MESSAGE + "{}", e.getMessage());
        }
    }
}
