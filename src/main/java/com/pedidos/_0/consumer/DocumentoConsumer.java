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
public class DocumentoConsumer {

    private static final Logger log = LoggerFactory.getLogger(DocumentoConsumer.class);
    private final HistorialTareasService historialService;

    public DocumentoConsumer(HistorialTareasService historialService) {
        this.historialService = historialService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_DOCUMENTOS)
    public void recibirDocumento(TareaMessage tarea) {
        log.info("[CONSUMER DOCUMENTO] Recibida tarea de documento: {}", tarea);

        if (tarea.isSimularError()) {
            log.warn("[CONSUMER DOCUMENTO] ERROR SIMULADO: Rechazando documento ID '{}' para redirigir a DLQ.", tarea.getId());
            throw new AmqpRejectAndDontRequeueException("Simulación de fallo en generación de documento. Enviando a DLQ.");
        }

        String detalle = "Documento/Factura generado: DOC-" + tarea.getId() + ".pdf para " + tarea.getDestinatario();
        log.info("[CONSUMER DOCUMENTO] ÉXITO: {}", detalle);
        historialService.registrarProcesada("DOCUMENTO", tarea, detalle);
    }
}

