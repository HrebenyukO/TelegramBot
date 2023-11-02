package com.example.TelegramBot.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.TelegramBot.Model.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class PrivatBankService {
    private static final String PRIVAT_BANK_API_URL_GOTIVKA =
            "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";
    private static final String PRIVAT_BANK_API_URL_BEZGOTIVKA =
            "https://api.privatbank.ua/p24api/pubinfo?exchange&coursid=11";

    public String getExchangeRates(String value) {
        RestTemplate restTemplate = new RestTemplate();
        String responce=null;
        String json=null;
        switch (value){
            case "БЕЗГОТІВКОВИЙ":
             json=restTemplate.getForObject(PRIVAT_BANK_API_URL_BEZGOTIVKA, String.class);
               responce=view(parseExchangeRates(json));
                break;
            case "ГОТІВКОВИЙ":
                json=restTemplate.getForObject(PRIVAT_BANK_API_URL_GOTIVKA, String.class);
                responce=view(parseExchangeRates(json));break;
        }
        return responce;
    }

    private List<ExchangeRate> parseExchangeRates(String json)  {
        ObjectMapper objectMapper = new ObjectMapper();
        ExchangeRate[] exchangeRatesArray = new ExchangeRate[0];
        try {
            exchangeRatesArray = objectMapper.readValue(json, ExchangeRate[].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return Arrays.asList(exchangeRatesArray);
    }

    private String view(List<ExchangeRate> exchangeRates) {
        StringBuilder stringBuilder = new StringBuilder();
        exchangeRates.forEach(rate -> {
            stringBuilder.append("Currency: ").append(rate.getCcy()).append("\n");
            stringBuilder.append("Base Currency: ").append(rate.getBase_ccy()).append("\n");
            stringBuilder.append("Buy Rate: ").append(rate.getBuy()).append("\n");
            stringBuilder.append("Sale Rate: ").append(rate.getSale()).append("\n\n");
        });
        return stringBuilder.toString();}
}