package com.example.TelegramBot.Service;

import com.example.TelegramBot.Config.BotConfig;
import com.example.TelegramBot.Model.User;
import com.example.TelegramBot.Model.UserRepository;
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
            "Type /mydata get your data stored\n"+
            "Type /deletedata delete my data\n"+
            "Type /settings set your preferences";


    public TelegramBot(BotConfig botConfig){
        this.botConfig=botConfig;
        addCommandsForMainMenu();
    }

    private void addCommandsForMainMenu(){
        List<BotCommand> listOfCommands=new ArrayList<>();
        listOfCommands.add(new BotCommand("/start","get a welcome message"));
        listOfCommands.add(new BotCommand("/mydata","get your data stored"));
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
                case "/mydata": myData(chatID);break;
                case "/help": sendMessage(chatID,HELP_MESSGE);break;

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

    private void startCommandReceived(long chatID, String firstName){
    String answer="Hello, " +firstName+ " , nice to meet you!";
    log.info("Replied to user "+firstName);
    sendMessage(chatID,answer);
    }

    private void myData(long chatID){
      String birtDay="Your birtday 23.03.92";
      log.info("Sending data");
      sendMessage(chatID,birtDay);
    }

    private void sendMessage(long chatID,String textToSend){
        SendMessage message=new SendMessage();
        message.setChatId(chatID);
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
           log.error("Error occured: "+e.getMessage());
        }

    }
}
