package com.example.TelegramBot.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PrivatBankService {
    private static final String PRIVAT_BANK_API_URL_GOTIVKA =
            "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";
    private static final String PRIVAT_BANK_API_URL_BEZGOTIVKA =
            "https://api.privatbank.ua/p24api/pubinfo?exchange&coursid=11";

    public String getExchangeRates(String value) {
        RestTemplate restTemplate = new RestTemplate();
        String responce=null;
        switch (value){
            case "БЕЗГОТІВКОВИЙ":
                responce=restTemplate.getForObject(PRIVAT_BANK_API_URL_BEZGOTIVKA, String.class);break;
            case "ГОТІВКОВИЙ":
                responce=restTemplate.getForObject(PRIVAT_BANK_API_URL_GOTIVKA, String.class);break;
        }
        return responce;
    }
}