package com.example.TelegramBot.Service;

import org.springframework.stereotype.Service;

@Service
public class PrivatBankService {
    private static final String PRIVAT_BANK_API_URL =
            "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";

    public String getExchangeRates() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(PRIVAT_BANK_API_URL, String.class);
    }
}