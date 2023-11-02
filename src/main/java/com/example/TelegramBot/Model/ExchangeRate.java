package com.example.TelegramBot.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeRate {

    private String ccy;
    private String base_ccy;
    private String buy;
    private String sale;

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "ccy='" + ccy + '\'' +
                ", base_ccy='" + base_ccy + '\'' +
                ", buy='" + buy + '\'' +
                ", sale='" + sale + '\'' +
                '}';
    }
}
