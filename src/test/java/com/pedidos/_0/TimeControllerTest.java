package com.pedidos._0;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TimeControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        TimeController timeController = new TimeController();
        mockMvc = MockMvcBuilders.standaloneSetup(timeController).build();
    }

    @Test
    public void testGetHoraNoAfectado() throws Exception {
        // Demuestra que la lógica existente del sistema sigue funcionando intacta
        mockMvc.perform(get("/api/hora"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").exists())
                .andExpect(jsonPath("$.mensaje").value(startsWith("El servidor marca las : ")));
    }
}
