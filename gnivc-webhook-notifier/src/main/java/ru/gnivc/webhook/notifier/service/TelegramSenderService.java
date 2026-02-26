package ru.gnivc.webhook.notifier.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.gnivc.webhook.notifier.configuration.TelegramConfig;
import ru.gnivc.webhook.notifier.util.ErrHandlerUtil;
import ru.gnivc.webhook.notifier.util.LogHandlerUtil;

@Slf4j
@RequiredArgsConstructor
public class TelegramSenderService extends TelegramLongPollingBot {
    private final TelegramConfig telegramConfig;
    @Override
    public String getBotUsername() {
        return "MyGitBot";
    }

    @Override
    public void onUpdateReceived(Update update) { }

    public void sendMessageToChat(String text){
        SendMessage message = new SendMessage();
        message.setChatId(telegramConfig.chatId());
        message.setText(text);
        try{
            execute(message);
            log.info(LogHandlerUtil.TRY_SEND_MESSAGE);
        } catch (TelegramApiException e){
            log.error(ErrHandlerUtil.FAILED_SEND_MESSAGE + "{}", e.getMessage());
        }
    }
}
