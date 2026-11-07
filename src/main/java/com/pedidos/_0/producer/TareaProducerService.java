package com.pedidos._0.producer;

import com.pedidos._0.config.RabbitMQConfig;
import com.pedidos._0.dto.TareaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class TareaProducerService {

    private static final Logger log = LoggerFactory.getLogger(TareaProducerService.class);
    private final RabbitTemplate rabbitTemplate;

    public TareaProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publica una tarea asíncrona de notificación al exchange de pedidos.
     */
    public void enviarNotificacion(TareaMessage tarea) {
        String routingKey = "pedidos.notificacion.email";
        log.info("Publicando tarea de NOTIFICACIÓN al exchange '{}' con routingKey '{}': {}",
                RabbitMQConfig.EXCHANGE_PEDIDOS, routingKey, tarea);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_PEDIDOS, routingKey, tarea);
    }

    /**
     * Publica una tarea asíncrona de generación de ticket al exchange de pedidos.
     */
    public void enviarTicket(TareaMessage tarea) {
        String routingKey = "pedidos.ticket.crear";
        log.info("Publicando tarea de GENERACIÓN DE TICKET al exchange '{}' con routingKey '{}': {}",
                RabbitMQConfig.EXCHANGE_PEDIDOS, routingKey, tarea);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_PEDIDOS, routingKey, tarea);
    }

    /**
     * Publica una tarea asíncrona de generación de documentos (PDF / factura) al exchange de pedidos.
     */
    public void enviarDocumento(TareaMessage tarea) {
        String routingKey = "pedidos.documento.generar";
        log.info("Publicando tarea de GENERACIÓN DE DOCUMENTO al exchange '{}' con routingKey '{}': {}",
                RabbitMQConfig.EXCHANGE_PEDIDOS, routingKey, tarea);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_PEDIDOS, routingKey, tarea);
    }

    /**
     * Publica un mensaje con exchange y routing key arbitrarios.
     */
    public void enviarMensajePersonalizado(String exchange, String routingKey, Object payload) {
        log.info("Publicando mensaje personalizado al exchange '{}' con routingKey '{}': {}",
                exchange, routingKey, payload);
        rabbitTemplate.convertAndSend(exchange, routingKey, payload);
    }
}

