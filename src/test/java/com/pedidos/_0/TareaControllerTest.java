package com.pedidos._0;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedidos._0.controller.TareaController;
import com.pedidos._0.dto.TareaMessage;
import com.pedidos._0.producer.TareaProducerService;
import com.pedidos._0.service.HistorialTareasService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TareaControllerTest {

    private TareaProducerService producerService;
    private HistorialTareasService historialService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        producerService = Mockito.mock(TareaProducerService.class);
        historialService = Mockito.mock(HistorialTareasService.class);
        TareaController controller = new TareaController(producerService, historialService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testEnviarNotificacion() throws Exception {
        mockMvc.perform(post("/api/tareas/notificacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"destinatario\":\"pedro@correo.cl\",\"contenido\":\"Pedido Listo\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENCOLADO"))
                .andExpect(jsonPath("$.tareaId").exists());

        verify(producerService, times(1)).enviarNotificacion(any(TareaMessage.class));
    }

    @Test
    public void testEnviarTicket() throws Exception {
        mockMvc.perform(post("/api/tareas/ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"destinatario\":\"cocina@pedidos.cl\",\"contenido\":\"Ticket mesa 4\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENCOLADO"))
                .andExpect(jsonPath("$.tareaId").exists());

        verify(producerService, times(1)).enviarTicket(any(TareaMessage.class));
    }

    @Test
    public void testEnviarDocumento() throws Exception {
        mockMvc.perform(post("/api/tareas/documento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"destinatario\":\"facturacion@pedidos.cl\",\"contenido\":\"Factura 554\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENCOLADO"))
                .andExpect(jsonPath("$.tareaId").exists());

        verify(producerService, times(1)).enviarDocumento(any(TareaMessage.class));
    }

    @Test
    public void testProcesarPedidoDesacoplado() throws Exception {
        // Demuestra que procesar un pedido desencadena en paralelo y asíncronamente
        // la notificación, el ticket y el documento
        mockMvc.perform(post("/api/pedidos/procesar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cliente\":\"Maria Lopez\",\"total\":18500}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PEDIDO_CREADO_EXITOSAMENTE"))
                .andExpect(jsonPath("$.pedidoId").exists());

        verify(producerService, times(1)).enviarNotificacion(any(TareaMessage.class));
        verify(producerService, times(1)).enviarTicket(any(TareaMessage.class));
        verify(producerService, times(1)).enviarDocumento(any(TareaMessage.class));
    }

    @Test
    public void testProbarDlq() throws Exception {
        mockMvc.perform(post("/api/tareas/probar-dlq?tipo=notificacion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENVIADO_CON_ERROR_SIMULADO"))
                .andExpect(jsonPath("$.tipo").value("notificacion"));

        verify(producerService, times(1)).enviarNotificacion(any(TareaMessage.class));
    }

    @Test
    public void testVerHistorial() throws Exception {
        mockMvc.perform(get("/api/tareas/historial"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tareasProcesadas").exists())
                .andExpect(jsonPath("$.tareasDlq").exists());
    }
}

