package com.example.weatherapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
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
    public ResponseEntity<String> getWeather(@PathVariable String city) {
        try {
            // Ersetze die Platzhalter im URL mit den tatsächlichen Werten
            String url = apiUrl.replace("{city}", city).replace("{key}", apiKey);
            String response = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(response);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            // Fehlerbehandlung für HTTP 4xx oder 5xx Fehler
            return ResponseEntity.status(ex.getStatusCode()).body("Fehler bei der Wetterabfrage: " + ex.getMessage());
        } catch (Exception ex) {
            // Allgemeine Fehlerbehandlung
            return ResponseEntity.status(500).body("Unerwarteter Fehler: " + ex.getMessage());
        }
    }
}
