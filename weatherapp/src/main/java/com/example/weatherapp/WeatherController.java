package com.example.weatherapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Map;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WeatherController {

    @Value("${weather.api.url}")
    private String apiUrl;

    @Value("${weather.api.historyurl}")
    private String historyUrl;

    private final RestTemplate restTemplate;

    public WeatherController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/weather/{city}")
    public String getWeather(@PathVariable String city, Model model) {
        System.out.println(System.getenv("WEATHER_API_KEY"));
        try {
            // Ersetze die Platzhalter in der URL mit den aktuellen Werten
            String url = apiUrl.replace("{city}", city).replace("{key}", System.getenv("WEATHER_API_KEY"));
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

    @GetMapping("/weather/{city}/history/{date}")
    public String getWeatherHistory(@PathVariable String city, @PathVariable String date, Model model) {
        try {
            // Ersetze die Platzhalter in der URL mit den aktuellen Werten
            String url = historyUrl.replace("{city}", city).replace("{key}", System.getenv("WEATHER_API_KEY")).replace("{date}", date);
            Map response = restTemplate.getForObject(url, Map.class);

            // Wetterdaten extrahieren (keine zusätzliche JSON-Bibliothek nötig)
            Map forecast = (Map) response.get("forecast");
            List<Map> forecastday = (List<Map>) forecast.get("forecastday");
            Map firstDay = forecastday.get(0);
            Map day = (Map) firstDay.get("day");
            Map condition = (Map) day.get("condition");

            String description = (String) condition.get("text");
            double temperature = (double) day.get("avgtemp_c");
            int humidity = (int) day.get("avghumidity");
            double windSpeed = (double) day.get("maxwind_kph");

            // Daten in das Model legen
            model.addAttribute("city", city);
            model.addAttribute("description", description); // Wetterbeschreibung
            model.addAttribute("temperature", temperature); // Durchschnittstemperatur
            model.addAttribute("humidity", humidity); // Durchschnittsluftfeuchtigkeit
            model.addAttribute("windSpeed", windSpeed); // Durchschnittsgeschwindigkeit

            return "history"; // Thymeleaf-Seite wird geladen
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            model.addAttribute("error", "Fehler bei der Wetterabfrage: " + ex.getMessage());
            return "history";
        } catch (Exception ex) {
            model.addAttribute("error", "Unerwarteter Fehler: " + ex.getMessage());
            return "history";
        }
    }
    @GetMapping("/weather/{city}/history/last7days")
    public String getLast7DaysWeather(@PathVariable String city, Model model) {
        try {
            // In getLast7DaysWeather method
            LocalDate currentDate = LocalDate.now().minusDays(1);  // Start from yesterday
            List<String> last7Days = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                last7Days.add(currentDate.minusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE));
            }

            // Erstelle eine Liste, um die Temperaturen der letzten 7 Tage zu speichern
            List<Double> temperatures = new ArrayList<>();

            // Abrufen der Wetterdaten für die letzten 7 Tage
            for (String date : last7Days) {
                String url = historyUrl.replace("{city}", city).replace("{key}", System.getenv("WEATHER_API_KEY")).replace("{date}", date);
                Map response = restTemplate.getForObject(url, Map.class);

                Map forecast = (Map) response.get("forecast");
                List<Map> forecastday = (List<Map>) forecast.get("forecastday");
                Map firstDay = forecastday.get(0);
                Map day = (Map) firstDay.get("day");

                Number tempNumber = (Number) day.get("avgtemp_c");
                Double temperature = tempNumber != null ? tempNumber.doubleValue() : 0.0;                temperatures.add(temperature);
                }

            // Die letzten 7 Tage und die entsprechenden Temperaturen an das Model übergeben
            model.addAttribute("city", city);
            model.addAttribute("dates", last7Days);
            model.addAttribute("temperatures", temperatures);

            return "weatherHistoryGraph";  // Die Thymeleaf-Seite mit dem Graphen
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            model.addAttribute("error", "Fehler bei der Wetterabfrage: " + ex.getMessage());
            return "weather";
        } catch (Exception ex) {
            model.addAttribute("error", "Unerwarteter Fehler: " + ex.getMessage());
            return "weather";
        }
    }
}
