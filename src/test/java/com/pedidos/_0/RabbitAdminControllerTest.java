package com.pedidos._0;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedidos._0.admin.RabbitAdminController;
import com.pedidos._0.admin.RabbitAdminService;
import com.pedidos._0.dto.AdminBindingRequest;
import com.pedidos._0.dto.AdminExchangeRequest;
import com.pedidos._0.dto.AdminQueueRequest;
import com.pedidos._0.dto.AdminResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RabbitAdminControllerTest {

    private RabbitAdminService adminService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        adminService = Mockito.mock(RabbitAdminService.class);
        RabbitAdminController controller = new RabbitAdminController(adminService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetStatus() throws Exception {
        mockMvc.perform(get("/api/admin/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modulo").exists())
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    public void testCrearCola() throws Exception {
        AdminQueueRequest req = new AdminQueueRequest("cola.nueva");
        when(adminService.crearCola(any(AdminQueueRequest.class)))
                .thenReturn(AdminResponse.ok("Cola 'cola.nueva' creada exitosamente."));

        mockMvc.perform(post("/api/admin/queues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cola 'cola.nueva' creada exitosamente."));
    }

    @Test
    public void testEliminarCola() throws Exception {
        when(adminService.eliminarCola(eq("cola.nueva")))
                .thenReturn(AdminResponse.ok("Cola 'cola.nueva' eliminada exitosamente."));

        mockMvc.perform(delete("/api/admin/queues/cola.nueva"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testCrearExchange() throws Exception {
        AdminExchangeRequest req = new AdminExchangeRequest("ex.nuevo", "topic");
        when(adminService.crearExchange(any(AdminExchangeRequest.class)))
                .thenReturn(AdminResponse.ok("Exchange 'ex.nuevo' creado exitosamente."));

        mockMvc.perform(post("/api/admin/exchanges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testEliminarExchange() throws Exception {
        when(adminService.eliminarExchange(eq("ex.nuevo")))
                .thenReturn(AdminResponse.ok("Exchange 'ex.nuevo' eliminado exitosamente."));

        mockMvc.perform(delete("/api/admin/exchanges/ex.nuevo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testCrearBinding() throws Exception {
        AdminBindingRequest req = new AdminBindingRequest("cola.nueva", "ex.nuevo", "routing.test");
        when(adminService.crearBinding(any(AdminBindingRequest.class)))
                .thenReturn(AdminResponse.ok("Binding creado exitosamente."));

        mockMvc.perform(post("/api/admin/bindings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testEliminarBinding() throws Exception {
        AdminBindingRequest req = new AdminBindingRequest("cola.nueva", "ex.nuevo", "routing.test");
        when(adminService.eliminarBinding(any(AdminBindingRequest.class)))
                .thenReturn(AdminResponse.ok("Binding eliminado exitosamente."));

        mockMvc.perform(delete("/api/admin/bindings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}

