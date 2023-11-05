package com.example.TelegramBot.Service;

import com.example.TelegramBot.Model.User;
import com.example.TelegramBot.Model.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.sql.Timestamp;

@Service
@Slf4j
public class UserService {
    @Autowired
    UserRepository userRepository;

    public void registredUser(Message message) {
        if (userRepository.findById(message.getChatId()).isEmpty()) {
            var chat = message.getChat();
            var chatID = message.getChatId();
            User user = new User();
            user.setChatId(chatID);
            user.setUsername(chat.getUserName());
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
            userRepository.save(user);
            log.info("User is created "+user.getUsername());
        }
    }


}
