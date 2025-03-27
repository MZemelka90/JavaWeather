package com.example.weatherapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Map;

@Controller
public class WeatherController {

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public WeatherController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/weather/{city}")
    public String getWeather(@PathVariable String city, Model model) {
        try {
            // Ersetze die Platzhalter in der URL mit den aktuellen Werten
            String url = apiUrl.replace("{city}", city).replace("{key}", apiKey);
            Map response = restTemplate.getForObject(url, Map.class);

            // Wetterdaten extrahieren (keine zusätzliche JSON-Bibliothek nötig)
            Map current = (Map) response.get("current");
            Map condition = (Map) current.get("condition");

            String description = (String) condition.get("text");
            double temperature = (double) current.get("temp_c");
            int humidity = (int) current.get("humidity");
            double windSpeed = (double) current.get("wind_kph");

            // Daten in das Model legen
            model.addAttribute("city", city);
            model.addAttribute("description", description);
            model.addAttribute("temperature", temperature);
            model.addAttribute("humidity", humidity);
            model.addAttribute("windSpeed", windSpeed);

            return "weather"; // Thymeleaf-Seite wird geladen
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            model.addAttribute("error", "Fehler bei der Wetterabfrage: " + ex.getMessage());
            return "weather";
        } catch (Exception ex) {
            model.addAttribute("error", "Unerwarteter Fehler: " + ex.getMessage());
            return "weather";
        }
    }
}
