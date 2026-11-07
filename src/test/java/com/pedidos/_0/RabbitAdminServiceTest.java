package com.pedidos._0;

import com.pedidos._0.admin.RabbitAdminService;
import com.pedidos._0.dto.AdminBindingRequest;
import com.pedidos._0.dto.AdminExchangeRequest;
import com.pedidos._0.dto.AdminQueueRequest;
import com.pedidos._0.dto.AdminResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RabbitAdminServiceTest {

    private AmqpAdmin amqpAdmin;
    private RabbitAdminService adminService;

    @BeforeEach
    public void setup() {
        amqpAdmin = Mockito.mock(AmqpAdmin.class);
        adminService = new RabbitAdminService(amqpAdmin);
    }

    @Test
    public void testCrearCola() {
        AdminQueueRequest req = new AdminQueueRequest("cola.test");
        req.setDeadLetterExchange("pedidos.dlx");
        req.setDeadLetterRoutingKey("cola.test.dlq");

        AdminResponse res = adminService.crearCola(req);

        assertTrue(res.isSuccess());
        verify(amqpAdmin).declareQueue(any(Queue.class));
    }

    @Test
    public void testEliminarCola() {
        when(amqpAdmin.deleteQueue("cola.test")).thenReturn(true);

        AdminResponse res = adminService.eliminarCola("cola.test");

        assertTrue(res.isSuccess());
        verify(amqpAdmin).deleteQueue("cola.test");
    }

    @Test
    public void testCrearExchange() {
        AdminExchangeRequest req = new AdminExchangeRequest("exchange.test", "topic");

        AdminResponse res = adminService.crearExchange(req);

        assertTrue(res.isSuccess());
        verify(amqpAdmin).declareExchange(any(Exchange.class));
    }

    @Test
    public void testEliminarExchange() {
        when(amqpAdmin.deleteExchange("exchange.test")).thenReturn(true);

        AdminResponse res = adminService.eliminarExchange("exchange.test");

        assertTrue(res.isSuccess());
        verify(amqpAdmin).deleteExchange("exchange.test");
    }

    @Test
    public void testCrearBinding() {
        AdminBindingRequest req = new AdminBindingRequest("cola.test", "exchange.test", "routing.key.test");

        AdminResponse res = adminService.crearBinding(req);

        assertTrue(res.isSuccess());
        verify(amqpAdmin).declareBinding(any(Binding.class));
    }

    @Test
    public void testEliminarBinding() {
        AdminBindingRequest req = new AdminBindingRequest("cola.test", "exchange.test", "routing.key.test");

        AdminResponse res = adminService.eliminarBinding(req);

        assertTrue(res.isSuccess());
        verify(amqpAdmin).removeBinding(any(Binding.class));
    }
}

