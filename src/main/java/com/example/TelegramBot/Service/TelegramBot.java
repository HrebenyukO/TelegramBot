package com.example.TelegramBot.Service;

import com.example.TelegramBot.Config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    public TelegramBot(BotConfig botConfig){
        this.botConfig=botConfig;
    }
    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage()  &&   update.getMessage().hasText()){
            String messageText=update.getMessage().getText();
            long chatID=update.getMessage().getChatId();
            switch (messageText){
                case "/start": startCommandReceived(chatID,update.getMessage().getChat().getFirstName());break;
                default:sendMessage(chatID,"Sorry,command was not recognized ");
            }

        }

    }
    private void startCommandReceived(long chatID, String firstName){
    String answer="Hello, " +firstName+ " , nice to meet you!";
    sendMessage(chatID,answer);
    }

    private void sendMessage(long chatID,String textToSend){
        SendMessage message=new SendMessage();
        message.setChatId(chatID);
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
}
