package com.pedidos._0.consumer;

import com.pedidos._0.config.RabbitMQConfig;
import com.pedidos._0.dto.TareaMessage;
import com.pedidos._0.service.HistorialTareasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DlqAuditConsumer {

    private static final Logger log = LoggerFactory.getLogger(DlqAuditConsumer.class);
    private final HistorialTareasService historialService;

    public DlqAuditConsumer(HistorialTareasService historialService) {
        this.historialService = historialService;
    }

    @RabbitListener(queues = RabbitMQConfig.DLQ_NOTIFICACIONES)
    public void recibirDlqNotificaciones(TareaMessage tarea, Message amqpMessage) {
        log.error("[DLQ AUDIT - NOTIFICACIONES] Mensaje recibido en DEAD LETTER QUEUE '{}': ID='{}', Contenido='{}'",
                RabbitMQConfig.DLQ_NOTIFICACIONES, tarea.getId(), tarea.getContenido());
        historialService.registrarDlq(RabbitMQConfig.DLQ_NOTIFICACIONES, tarea, "Rechazado en cola principal por excepción simulada.");
    }

    @RabbitListener(queues = RabbitMQConfig.DLQ_TICKETS)
    public void recibirDlqTickets(TareaMessage tarea, Message amqpMessage) {
        log.error("[DLQ AUDIT - TICKETS] Mensaje recibido en DEAD LETTER QUEUE '{}': ID='{}', Contenido='{}'",
                RabbitMQConfig.DLQ_TICKETS, tarea.getId(), tarea.getContenido());
        historialService.registrarDlq(RabbitMQConfig.DLQ_TICKETS, tarea, "Rechazado en cola principal por excepción simulada.");
    }

    @RabbitListener(queues = RabbitMQConfig.DLQ_DOCUMENTOS)
    public void recibirDlqDocumentos(TareaMessage tarea, Message amqpMessage) {
        log.error("[DLQ AUDIT - DOCUMENTOS] Mensaje recibido en DEAD LETTER QUEUE '{}': ID='{}', Contenido='{}'",
                RabbitMQConfig.DLQ_DOCUMENTOS, tarea.getId(), tarea.getContenido());
        historialService.registrarDlq(RabbitMQConfig.DLQ_DOCUMENTOS, tarea, "Rechazado en cola principal por excepción simulada.");
    }
}

