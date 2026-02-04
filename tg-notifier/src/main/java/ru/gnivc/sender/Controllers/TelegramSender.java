package Controllers;

import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramSender extends TelegramLongPollingBot {
    private final String BOT_TOKEN;
    private final String CHAT_ID;

    public TelegramSender(@Value("${telegram.bot.token}") String token,
                          @Value("${telegram.chat.id}") String chatId){
        super(token);
        this.BOT_TOKEN=token;
        this.CHAT_ID=chatId;

        System.out.println("=== Telegram Bot Config ===");
        System.out.println("Token: " + (token != null ? "SET" : "NULL"));
        System.out.println("Chat ID: " + chatId);
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
            System.out.println("✅ Message sent successfully!");
        } catch (TelegramApiException e){
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }
}
