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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;
    private final BotConfig botConfig;
    public static final String HELP_MESSGE="HELLO THIS IS MEN\n" +
            "Type /start get a welcome message\n"+
            "Type /myBirthday get your data stored\n"+
            "Type /deletedata delete my data\n"+
            "Type /settings set your preferences";


    public TelegramBot(BotConfig botConfig){
        this.botConfig=botConfig;
        addCommandsForMainMenu();
    }

    private void addCommandsForMainMenu(){
        List<BotCommand> listOfCommands=new ArrayList<>();
        listOfCommands.add(new BotCommand("/start","get a welcome message"));
        listOfCommands.add(new BotCommand("/myBirthday","get your day of birthday"));
        listOfCommands.add(new BotCommand("/deletedata","delete my data"));
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
            String messageText=update.getMessage().getText();
            long chatID=update.getMessage().getChatId();
            switch (messageText){
                case "/start":
                    registredUser(update.getMessage());
                    startCommandReceived(chatID,update.
                        getMessage().
                        getChat().
                        getFirstName());break;
                case "/myBirthday": myData(update.getMessage());break;
                case "/help": sendMessage(chatID,HELP_MESSGE);break;

                case " $ ": sendMessage(chatID,"https://api.privatbank.ua/p24api/pubinfo?exchange&coursid=5");break;

                default:sendMessage(chatID,"Sorry,command was not recognized ");
            }
        }
    }

    private void registredUser(Message message) {
        if(userRepository.findById(message.getChatId()).isEmpty()){
            var chat=message.getChat();
            var chatID=message.getChatId();
            User user=new User();
            user.setChatId(chatID);
            user.setUsername(chat.getUserName());
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
            userRepository.save(user);
            log.info("User is created "+user.getUsername());
        }

    }

    //https://emojipedia.org/smileys
    private void startCommandReceived(long chatID, String firstName){
      String answer= EmojiParser.
              parseToUnicode("Hello, " +firstName+ " , nice to meet you! :star_struck:");
    log.info("Replied to user "+firstName);
    sendMessage(chatID,answer);
    }

    private void myData(Message message){
       String name= message.getChat().getFirstName();
       String surname=message.getChat().getLastName();

       switch (name+" "+surname){
           case "Олексій Гребенюк": sendMessage(message.getChatId(),"23.03.92");
               log.info("Sending birthday for " +message.getChat().getUserName());break;
           case "Юлія Гребенюк": sendMessage(message.getChatId(),"27.08.92");
               log.info("Sending birthday for " +message.getChat().getUserName());break;
       }
    }

    private void sendMessage(long chatID,String textToSend){
        SendMessage message=new SendMessage();
        message.setChatId(chatID);
        message.setText(textToSend);
        ReplyKeyboardMarkup keyboard = getReplyKeyboardMarkup();
        message.setReplyMarkup(keyboard);
        try {
            execute(message);
        } catch (TelegramApiException e) {
           log.error("Error occured: "+e.getMessage());
        }

    }

    private static ReplyKeyboardMarkup getReplyKeyboardMarkup() {
        ReplyKeyboardMarkup keyboard=new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows=new ArrayList<>();
        KeyboardRow keyboardRow=new KeyboardRow();
        keyboardRow.add("weather");
        keyboardRow.add("news");
        keyboardRow.add("time");
        keyboardRows.add(keyboardRow);
        keyboardRow=new KeyboardRow();
        keyboardRow.add(" $ ");
        keyboardRow.add(" ₴ ");
        keyboardRow.add(" € ");
        keyboardRows.add(keyboardRow);

        keyboard.setKeyboard(keyboardRows);
        return keyboard;
    }
}
