package com.example.weatherapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

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
}
