package com.example.TelegramBot.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.glassfish.grizzly.http.util.TimeStamp;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tg_bot")
public class User {
    @Id
    private Long chatId;

    private String firstName;

    private String lastName;

    private String username;

    private Timestamp registeredAt;


    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", registeredAt=" + registeredAt +
                '}';
    }
}
