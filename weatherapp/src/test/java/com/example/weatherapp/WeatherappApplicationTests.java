package com.example.weatherapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

@WebMvcTest
class WeatherappApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void contextLoads() {
        // Prüft, ob der Anwendungskontext erfolgreich geladen wird
    }

    @Test
    void testGetWeather() throws Exception {
        Map<String, Object> mockResponse = Map.of(
            "current", Map.of(
                "temp_c", 20.5,
                "humidity", 60,
                "wind_kph", 15.0,
                "condition", Map.of("text", "Clear")
            )
        );

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/weather/London"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("London")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("20.5")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("Clear")));
    }

    @Test
    void testGetWeatherHistory() throws Exception {
        Map<String, Object> mockResponse = Map.of(
            "forecast", Map.of(
                "forecastday", List.of(
                    Map.of(
                        "date", "2023-07-01",
                        "day", Map.of(
                            "avgtemp_c", 22.0,
                            "avghumidity", 65,
                            "maxwind_kph", 20.0,
                            "condition", Map.of("text", "Sunny")
                        )
                    )
                )
            )
        );

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/weather/London/history/2023-07-01"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("London")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("2023-07-01")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("22.0")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("Sunny")));
    }
    @Test
    void testGetWeatherInvalidCity() throws Exception {
        // Simuliert einen Fehler bei der Wetterabfrage (z.B. 404 Fehler)
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get("/weather/InvalidCity"))
                .andExpect(MockMvcResultMatchers.status().isOk())  // Wir erwarten, dass die Seite trotzdem geladen wird
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("Fehler bei der Wetterabfrage")));
    }

    @Test
    void testGetWeatherHistoryInvalidCity() throws Exception {
        // Simuliert einen Fehler bei der Wetterabfrage für die History (z.B. 404 Fehler)
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get("/weather/InvalidCity/history/2023-07-01"))
                .andExpect(MockMvcResultMatchers.status().isOk())  // Wir erwarten, dass die Seite trotzdem geladen wird
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("Fehler bei der Wetterabfrage")));
    }

    @Test
    void testGetWeatherWithEmptyResponse() throws Exception {
        // Simuliert eine leere Antwort vom Wetter-API
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(Map.of());  // Leere Antwort

        mockMvc.perform(MockMvcRequestBuilders.get("/weather/London"))
                .andExpect(MockMvcResultMatchers.status().isOk())  // Seite sollte geladen werden
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("Unerwarteter Fehler")));
    }

    @Test
    void testGetWeatherHistoryWithEmptyResponse() throws Exception {
        // Simuliert eine leere Antwort für das History-API
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(Map.of());  // Leere Antwort

        mockMvc.perform(MockMvcRequestBuilders.get("/weather/London/history/2023-07-01"))
                .andExpect(MockMvcResultMatchers.status().isOk())  // Seite sollte geladen werden
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("Unerwarteter Fehler")));
    }

    @Test
    void testGetWeatherHistoryFutureDate() throws Exception {
        // Simuliert eine Antwort für ein zukünftiges Datum (Datum könnte in einer echten API ungültig sein)
        Map<String, Object> mockResponse = Map.of(
            "forecast", Map.of(
                "forecastday", List.of(
                    Map.of(
                        "date", "2025-12-01",
                        "day", Map.of(
                            "avgtemp_c", 18.0,
                            "avghumidity", 50,
                            "maxwind_kph", 15.0,
                            "condition", Map.of("text", "Partly Cloudy")
                        )
                    )
                )
            )
        );

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/weather/London/history/2025-12-01"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("London")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("2025-12-01")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("Partly Cloudy")));
    }
}
