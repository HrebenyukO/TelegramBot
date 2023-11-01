package com.example.TelegramBot.Config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataBaseConfig {

    @Bean
    public DataSource getDataSource() {
        return DataSourceBuilder
                .create()
                .url("jdbc:mysql://localhost:3306/tg_bot")
                .username("bestuser")
                .password("bestuser")
                .build();
    }
}
