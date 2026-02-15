package ru.gnivc.sender.service;

import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.gnivc.sender.util.ErrHandler;
import ru.gnivc.sender.util.LogHandler;

public class TelegramSender extends TelegramLongPollingBot {
    private final String BOT_TOKEN;
    private final String CHAT_ID;

    public TelegramSender(@Value("${telegram.bot.token}") String token,
                          @Value("${telegram.chat.id}") String chatId){
        super(token);
        this.BOT_TOKEN=token;
        this.CHAT_ID=chatId;
    }

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
            System.out.println(LogHandler.TRY_SEND_MESSAGE);
        } catch (TelegramApiException e){
            System.err.println(ErrHandler.FAILED_SEND_MESSAGE + e.getMessage());
        }
    }
}
