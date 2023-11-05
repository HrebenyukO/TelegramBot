package com.example.TelegramBot.Service;

import com.example.TelegramBot.Config.BotConfig;
import com.example.TelegramBot.Model.User;
import com.example.TelegramBot.Model.UserRepository;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.grizzly.http.util.TimeStamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserService userService;

    @Autowired
    PrivatBankService privatBankService;
    private final BotConfig botConfig;
    public static final String HELP_MESSGE="HELLO THIS IS MEN\n" +
            "Type /start get a welcome message\n"+
            "Type /myBirthday get your data stored";


    public TelegramBot(BotConfig botConfig){
        this.botConfig=botConfig;
        addCommandsForMainMenu();
    }

    private void addCommandsForMainMenu(){
        List<BotCommand> listOfCommands=new ArrayList<>();
        listOfCommands.add(new BotCommand("/start","get a welcome message"));
        listOfCommands.add(new BotCommand("/myBirthday","get your day of birthday"));
        listOfCommands.add(new BotCommand("/help","info how to use this bot"));
        listOfCommands.add(new BotCommand("/settings","set your preferences"));
        try {
            this.execute(new SetMyCommands(listOfCommands,
                    new BotCommandScopeDefault(),null));
        } catch (TelegramApiException e) {
            log.error("Error settings commands list "+e.getMessage());
        }
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
            log.info("HAS MESSAGE START");
            hasMessage(update);
            }
        else if(update.hasCallbackQuery()) {
            log.info("CALL BACK QUERY START");
            hasQuery(update);
        }
    }

    private void registredUser(Message message) {
            userService.registredUser(message);
        }


    //https://emojipedia.org/smileys
    private void startCommandReceived(long chatID, String firstName){
      String answer= EmojiParser.
              parseToUnicode("Hello, " +firstName+ " , nice to meet you! :star_struck:");
    log.info("Replied to user "+firstName);
    sendMessage(chatID,answer);
    }



    private void sendMessage(long chatID,String textToSend){
        SendMessage message=new SendMessage();
        message.setChatId(chatID);
        message.setText(textToSend);
        ReplyKeyboardMarkup keyboard = mainMenu();
        message.setReplyMarkup(keyboard);
        try {
            execute(message);
        } catch (TelegramApiException e) {
           log.error("Error occured: "+e.getMessage());
        }
    }

    private static ReplyKeyboardMarkup mainMenu() {
        ReplyKeyboardMarkup keyboard=new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows=new ArrayList<>();
        KeyboardRow keyboardRow=new KeyboardRow();
        keyboardRow.add("weather");
        keyboardRow.add("СПИСОК ПОКУПОК");
        keyboardRow.add("time");
        keyboardRows.add(keyboardRow);
        keyboardRow=new KeyboardRow();
        keyboardRow.add("КУРСИ ВАЛЮТ");
        keyboardRows.add(keyboardRow);
        keyboard.setKeyboard(keyboardRows);
        return keyboard;
    }

    public void exchangeRatesIntoPB(long chatID) {
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText("Курси валют у ПриватБанку ");

        message.setReplyMarkup(privatBankService.exchangeRatesIntoPB());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void exchangeRates(long chatID) {
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText("$$$$$$$$--КУРСИ ВАЛЮТ--$$$$$$$$$");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var buttonPB = new InlineKeyboardButton();
        var buttonNacBank=new InlineKeyboardButton();
        buttonPB.setText("PRIVATBANK");
        buttonPB.setCallbackData("PRIVATBANK");
        buttonNacBank.setText("НАЦБАНК");
        buttonNacBank.setCallbackData("НАЦБАНК");
        rowInLine.add(buttonPB);
        rowInLine.add(buttonNacBank);
        rowsInLine.add(rowInLine);
        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        message.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void hasMessage(Update update){
        String messageText=update.getMessage().getText();
        long chatID=update.getMessage().getChatId();
        switch (messageText){
            case "/start":
                registredUser(update.getMessage());
                startCommandReceived(chatID,update.
                        getMessage().
                        getChat().
                        getFirstName());break;
            case "/help": sendMessage(chatID,HELP_MESSGE);break;
            case "КУРСИ ВАЛЮТ": exchangeRates(chatID);break;

            default:sendMessage(chatID,"Sorry,command was not recognized ");

        }
    }

    private void hasQuery(Update update){
        String data=update.getCallbackQuery().getData();
        long chatID=update.getCallbackQuery().getMessage().getChatId();
        switch (data){
            case "PRIVATBANK":  exchangeRatesIntoPB(chatID);break;
            case "ГОТІВКОВИЙ":
            case "БЕЗГОТІВКОВИЙ":
            sendMessage(chatID,privatBankService.getExchangeRates(data));break;
        }
    }
}