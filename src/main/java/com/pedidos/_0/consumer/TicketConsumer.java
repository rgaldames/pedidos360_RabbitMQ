package com.pedidos._0.consumer;

import com.pedidos._0.config.RabbitMQConfig;
import com.pedidos._0.dto.TareaMessage;
import com.pedidos._0.service.HistorialTareasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TicketConsumer {

    private static final Logger log = LoggerFactory.getLogger(TicketConsumer.class);
    private final HistorialTareasService historialService;

    public TicketConsumer(HistorialTareasService historialService) {
        this.historialService = historialService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_TICKETS)
    public void recibirTicket(TareaMessage tarea) {
        log.info("[CONSUMER TICKET] Recibida tarea de ticket: {}", tarea);

        if (tarea.isSimularError()) {
            log.warn("[CONSUMER TICKET] ERROR SIMULADO: Rechazando ticket ID '{}' para redirigir a DLQ.", tarea.getId());
            throw new AmqpRejectAndDontRequeueException("Simulación de fallo en generación de ticket. Enviando a DLQ.");
        }

        String detalle = "Ticket generado con código TCK-" + tarea.getId() + " para " + tarea.getDestinatario();
        log.info("[CONSUMER TICKET] ÉXITO: {}", detalle);
        historialService.registrarProcesada("TICKET", tarea, detalle);
    }
}

