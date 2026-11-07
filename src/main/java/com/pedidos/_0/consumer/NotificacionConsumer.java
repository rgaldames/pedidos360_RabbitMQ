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
public class NotificacionConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificacionConsumer.class);
    private final HistorialTareasService historialService;

    public NotificacionConsumer(HistorialTareasService historialService) {
        this.historialService = historialService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NOTIFICACIONES)
    public void recibirNotificacion(TareaMessage tarea) {
        log.info("[CONSUMER NOTIFICACIÓN] Recibida tarea de notificación: {}", tarea);

        // Si se solicita simular un fallo en el procesamiento
        if (tarea.isSimularError()) {
            log.warn("[CONSUMER NOTIFICACIÓN] ERROR SIMULADO: Rechazando mensaje ID '{}' para redirigir a DLQ.", tarea.getId());
            throw new AmqpRejectAndDontRequeueException("Simulación de fallo en notificación. Enviando a DLQ.");
        }

        // Simular procesamiento asíncrono
        String detalle = "Notificación enviada a " + tarea.getDestinatario() + " con contenido: '" + tarea.getContenido() + "'";
        log.info("[CONSUMER NOTIFICACIÓN] ÉXITO: {}", detalle);
        historialService.registrarProcesada("NOTIFICACION", tarea, detalle);
    }
}

