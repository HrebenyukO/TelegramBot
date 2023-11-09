package com.example.TelegramBot.Service;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

@Service
public class ChartService  {
    public InputFile sendChart() {
        DefaultCategoryDataset dataset = getDefaultCategoryDataset();
        JFreeChart lineChart=createJFreeChart(dataset);
        try {
            File imageFile = new File("chart.png");
            ChartUtils.saveChartAsPNG(imageFile, lineChart, 600, 400);
            InputFile inputFile = new InputFile(imageFile);
            return inputFile;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Добавление данных
    private DefaultCategoryDataset getDefaultCategoryDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(38.2, "USD", LocalDate.of(2023, 11, 1));
        dataset.addValue(38, "USD", LocalDate.of(2023, 11, 2));
        dataset.addValue(37.4, "USD", LocalDate.of(2023, 11, 3));
        dataset.addValue(38.2, "USD", LocalDate.of(2023, 11, 4));
        dataset.addValue(37, "USD", LocalDate.of(2023, 11, 5));
        dataset.addValue(37.4, "USD", LocalDate.of(2023, 11, 6));
        dataset.addValue(37.2, "USD", LocalDate.of(2023, 11, 7));
        dataset.addValue(38, "USD", LocalDate.of(2023, 11, 8));
        dataset.addValue(37.8, "USD", LocalDate.of(2023, 11, 9));

        dataset.addValue(40.2, "EURO", LocalDate.of(2023, 11, 1));
        dataset.addValue(40, "EURO", LocalDate.of(2023, 11, 2));
        dataset.addValue(40.4, "EURO", LocalDate.of(2023, 11, 3));
        dataset.addValue(40.2, "EURO", LocalDate.of(2023, 11, 4));
        dataset.addValue(39.8, "EURO", LocalDate.of(2023, 11, 5));
        dataset.addValue(39.4, "EURO", LocalDate.of(2023, 11, 6));
        dataset.addValue(41.2, "EURO", LocalDate.of(2023, 11, 7));
        dataset.addValue(40, "EURO", LocalDate.of(2023, 11, 8));
        dataset.addValue(40.8, "EURO", LocalDate.of(2023, 11, 9));
        return dataset;
    }


    private JFreeChart createJFreeChart(DefaultCategoryDataset dataset) {
        JFreeChart lineChart = ChartFactory.createLineChart(
                "Currency Exchange Rate",
                "Date",
                "Exchange Rate",
                dataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                true, true, false
        );

        CategoryPlot plot = lineChart.getCategoryPlot();

        // Настраиваем стиль линии
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesShapesVisible(0, true);

        // Настраиваем цвет фона и сетку
        plot.setBackgroundPaint(Color.lightGray);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.white);

        // Настраиваем оси
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);


        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLowerBound(35); // Начальное значение оси Y
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); // Настройка делений оси Y

        // Настраиваем заголовок
        lineChart.getTitle().setFont(new Font("Arial", Font.BOLD, 20));

        // Добавляем легенду
        lineChart.getLegend().setVisible(true);

        // Настраиваем цвета
        plot.getRenderer().setSeriesPaint(0, Color.BLUE);
        return lineChart;
    }

}
