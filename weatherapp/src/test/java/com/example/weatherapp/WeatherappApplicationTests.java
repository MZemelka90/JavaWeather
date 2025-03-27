package com.example.weatherapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;
import static org.mockito.Mockito.*;

@SpringBootTest
class WeatherappApplicationTests {

    @Autowired
    private MockMvc mockMvc;  // MockMvc für Controller-Tests

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void contextLoads() {
        // Sicherstellen, dass der WeatherController korrekt geladen wird
    }

    @Test
    void testGetWeather() throws Exception {
        // Beispielantwort von der Wetter-API
        String mockResponse = "{"
                + "\"current\": {"
                + "\"temp_c\": 20.5,"
                + "\"humidity\": 60,"
                + "\"wind_kph\": 15,"
                + "\"condition\": {"
                + "\"text\": \"Clear\""
                + "}}}";

        // Simuliere die Antwort des RestTemplate
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

        // Teste, ob die Seite korrekt gerendert wird und die Daten angezeigt werden
        mockMvc.perform(MockMvcRequestBuilders.get("/weather/London"))
                .andExpect(MockMvcResultMatchers.status().isOk())  // Statuscode OK erwarten
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("Wetter in London")))  // Sicherstellen, dass die Antwort den Text "Wetter in London" enthält
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("Clear")))  // Sicherstellen, dass die Antwort die Wetterbeschreibung enthält
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("20.5")));  // Überprüfe, ob die Temperatur in der Antwort enthalten ist
    }
}
