package com.example.TelegramBot.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class NacBank{
    private static final String NAC_BANK_API_URL_ExchangeRate =
            "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
    public String getExchangeRates() {
        RestTemplate restTemplate = new RestTemplate();
        String response=null;
        String json=json=restTemplate.getForObject(
                NAC_BANK_API_URL_ExchangeRate,String.class);
       response=view(json);
        return response;
    }

    private String view(String jsonString) {
        StringBuilder result = new StringBuilder();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(jsonString);
            root.forEach(node -> {
                String currencyCode = node.get("cc").asText();
                if (    currencyCode.equals("USD") ||
                        currencyCode.equals("EUR") ||
                        currencyCode.equals("PLN") ||
                        currencyCode.equals("XAU") ||
                        currencyCode.equals("XAG") ||
                        currencyCode.equals("XPT") ||
                        currencyCode.equals("XPD")) {
                    String currencyName = node.get("txt").asText();
                    double exchangeRate = node.get("rate").asDouble();
                    String exchangeDate = node.get("exchangedate").asText();

                    result.append("Currency Code: ").append(currencyCode).append("\n");
                    result.append("Currency Name: ").append(currencyName).append("\n");
                    result.append("Exchange Rate: ").append(exchangeRate).append("\n");
                    result.append("Exchange Date: ").append(exchangeDate).append("\n\n");
                }
            });
        }  catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return result.toString();
    }
}
