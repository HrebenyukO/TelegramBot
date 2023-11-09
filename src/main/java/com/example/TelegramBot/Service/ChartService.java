package com.example.TelegramBot.Service;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;

@Service
public class ChartService {
    public InputFile sendChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Category 1", 50);
        dataset.setValue("Category 2", 30);
        dataset.setValue("Category 3", 20);

        JFreeChart chart = ChartFactory.createPieChart(
                "Sample Pie Chart",
                dataset,
                true,
                true,
                false
        );

        try {
            File imageFile = new File("chart.png");
            ChartUtils.saveChartAsPNG(imageFile, chart, 600, 400);
            InputFile inputFile = new InputFile(imageFile);
            return inputFile;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /* public SendMessage sendChart(Message message) {
        // Generate your chart here

        File imageFile = new File("chart.png");
        // Assume you have saved the chart as an image file named chart.png

        SendPhoto sendPhotoRequest = new SendPhoto();
        sendPhotoRequest.setChatId(message.getChatId().toString());
        sendPhotoRequest.setReplyToMessageId(message.getMessageId());
        sendPhotoRequest.setPhoto(new InputFile(imageFile));

        try {
            // Assuming you have a TelegramBot instance named 'bot' set up
            bot.execute(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        // Deleting the temporary image file
        imageFile.delete();

        return null;
    }*/
}
