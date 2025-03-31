package com.example.weatherapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.List;

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
        Map<String, Object> mockHistoryResponse = Map.of(
            "forecast", Map.of(
                "forecastday", List.of(
                    Map.of(
                        "date", "2023-07-01",
                        "day", Map.of(
                            "avgtemp_c", 18.3,
                            "avghumidity", 70,
                            "maxwind_kph", 20.0,
                            "condition", Map.of("text", "Partly cloudy")
                        )
                    )
                )
            )
        );

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockHistoryResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/weather/London/history"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("London")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("18.3")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("Partly cloudy")));
    }
}
