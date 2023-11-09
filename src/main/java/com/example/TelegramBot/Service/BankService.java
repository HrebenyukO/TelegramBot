package com.example.TelegramBot.Service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class BankService {

    public InlineKeyboardMarkup menuExchangeRates(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var buttonPB = new InlineKeyboardButton();
        var buttonNacBank=new InlineKeyboardButton();
        var buttonAnalyzeER=new InlineKeyboardButton();
        buttonPB.setText("PRIVATBANK");
        buttonPB.setCallbackData("PRIVATBANK");
        buttonNacBank.setText("НАЦБАНК");
        buttonNacBank.setCallbackData("НАЦБАНК");
        buttonAnalyzeER.setText("AНАЛІТИКА КУРСУ ВАЛЮТ");
        buttonAnalyzeER.setCallbackData("AНАЛІТИКА");
        rowInLine.add(buttonPB);
        rowInLine.add(buttonNacBank);
        rowInLine.add(buttonAnalyzeER);
        rowsInLine.add(rowInLine);
        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        return inlineKeyboardMarkup;
    }
}
