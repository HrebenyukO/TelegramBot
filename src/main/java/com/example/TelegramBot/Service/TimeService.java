package com.example.TelegramBot.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
@Slf4j
@Service
public class TimeService {

    private static final List<String> TIMEZONES = List.of(
            "Europe/London",
            "Europe/Kiev",
            "America/New_York",
            "Asia/Tokyo"
    );

    private final WebClient webClient;

    public TimeService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://www.worldtimeapi.org/api/timezone").build();
    }

    public String getTime() {
        log.info("GET TIME BEGIN");
        StringBuilder stringBuilder = new StringBuilder();

        TIMEZONES.forEach(timezone -> {
            String json = getTimeJson(timezone);
            stringBuilder.append(view(json)).append("\n\n");
        });

        return stringBuilder.toString();
    }

    private String getTimeJson(String timezone) {
        return webClient.get()
                .uri("/{timezone}", timezone)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private String view(String json) {
        StringBuilder result = new StringBuilder();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(json);

            if (root.isObject()) {
                processNode(result, root);
            } else if (root.isArray()) {
                for (JsonNode node : root) {
                    processNode(result, node);
                }
            } else {
                result.append("Error: The root node is neither an object nor an array.\n");
            }

        } catch (Exception e) {
            result.append("Error: ").append(e.getMessage()).append("\n");
            e.printStackTrace();
        }
        return result.toString();
    }

    private void processNode(StringBuilder result, JsonNode node) {
        JsonNode timezoneNode = node.get("timezone");
        if (timezoneNode != null && !timezoneNode.isNull()) {
            String timezone = timezoneNode.asText();
            if (TIMEZONES.contains(timezone)) {
                JsonNode datetimeNode = node.get("datetime");
                if (datetimeNode != null && !datetimeNode.isNull()) {
                    String exchangeDate = datetimeNode.asText();
                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(exchangeDate);
                    ZonedDateTime localTime = zonedDateTime.withZoneSameInstant(ZoneId.of(timezone));
                    DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM);
                    String formattedTime = localTime.format(formatter);
                    result.append("Time in ").append(timezone).append(":  ").append(formattedTime).append("\n\n");
                } else {
                    result.append("Error: 'datetime' field is missing or null.\n");
                }
            } else {
                result.append("Error: 'timezone' field is missing or null.\n");
            }
        }
    }
}