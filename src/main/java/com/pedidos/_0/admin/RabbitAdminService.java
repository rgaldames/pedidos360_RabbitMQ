package com.pedidos._0.admin;

import com.pedidos._0.dto.AdminBindingRequest;
import com.pedidos._0.dto.AdminExchangeRequest;
import com.pedidos._0.dto.AdminQueueRequest;
import com.pedidos._0.dto.AdminResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RabbitAdminService {

    private static final Logger log = LoggerFactory.getLogger(RabbitAdminService.class);
    private final AmqpAdmin amqpAdmin;

    public RabbitAdminService(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    /**
     * Crea una nueva cola en RabbitMQ con soporte para configuración de DLQ si se especifica.
     */
    public AdminResponse crearCola(AdminQueueRequest request) {
        try {
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                return AdminResponse.error("El nombre de la cola es obligatorio.");
            }

            Map<String, Object> arguments = request.getArguments() != null ? new HashMap<>(request.getArguments()) : new HashMap<>();
            
            // Si se especificó Dead Letter Exchange
            if (request.getDeadLetterExchange() != null && !request.getDeadLetterExchange().trim().isEmpty()) {
                arguments.put("x-dead-letter-exchange", request.getDeadLetterExchange().trim());
            }
            if (request.getDeadLetterRoutingKey() != null && !request.getDeadLetterRoutingKey().trim().isEmpty()) {
                arguments.put("x-dead-letter-routing-key", request.getDeadLetterRoutingKey().trim());
            }

            Queue queue = new Queue(
                    request.getName().trim(),
                    request.isDurable(),
                    request.isExclusive(),
                    request.isAutoDelete(),
                    arguments
            );

            amqpAdmin.declareQueue(queue);
            log.info("Cola creada exitosamente: {}", request.getName());
            return AdminResponse.ok("Cola '" + request.getName() + "' creada exitosamente.", queue);
        } catch (Exception e) {
            log.error("Error al crear la cola {}: {}", request.getName(), e.getMessage());
            return AdminResponse.error("Error al crear la cola: " + e.getMessage());
        }
    }

    /**
     * Elimina una cola existente en RabbitMQ.
     */
    public AdminResponse eliminarCola(String nombreCola) {
        try {
            if (nombreCola == null || nombreCola.trim().isEmpty()) {
                return AdminResponse.error("El nombre de la cola es obligatorio.");
            }
            boolean eliminada = amqpAdmin.deleteQueue(nombreCola.trim());
            if (eliminada) {
                log.info("Cola eliminada exitosamente: {}", nombreCola);
                return AdminResponse.ok("Cola '" + nombreCola + "' eliminada exitosamente.");
            } else {
                return AdminResponse.error("No se pudo eliminar la cola '" + nombreCola + "' (quizás no existía).");
            }
        } catch (Exception e) {
            log.error("Error al eliminar la cola {}: {}", nombreCola, e.getMessage());
            return AdminResponse.error("Error al eliminar la cola: " + e.getMessage());
        }
    }

    /**
     * Crea un nuevo exchange en RabbitMQ (direct, topic, fanout, headers).
     */
    public AdminResponse crearExchange(AdminExchangeRequest request) {
        try {
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                return AdminResponse.error("El nombre del exchange es obligatorio.");
            }

            Exchange exchange;
            String type = request.getType() != null ? request.getType().toLowerCase().trim() : "topic";
            String name = request.getName().trim();

            switch (type) {
                case "direct":
                    exchange = new DirectExchange(name, request.isDurable(), request.isAutoDelete(), request.getArguments());
                    break;
                case "fanout":
                    exchange = new FanoutExchange(name, request.isDurable(), request.isAutoDelete(), request.getArguments());
                    break;
                case "headers":
                    exchange = new HeadersExchange(name, request.isDurable(), request.isAutoDelete(), request.getArguments());
                    break;
                case "topic":
                default:
                    exchange = new TopicExchange(name, request.isDurable(), request.isAutoDelete(), request.getArguments());
                    break;
            }

            amqpAdmin.declareExchange(exchange);
            log.info("Exchange creado exitosamente: {} (tipo: {})", name, type);
            return AdminResponse.ok("Exchange '" + name + "' de tipo '" + type + "' creado exitosamente.", exchange);
        } catch (Exception e) {
            log.error("Error al crear el exchange {}: {}", request.getName(), e.getMessage());
            return AdminResponse.error("Error al crear el exchange: " + e.getMessage());
        }
    }

    /**
     * Elimina un exchange existente en RabbitMQ.
     */
    public AdminResponse eliminarExchange(String nombreExchange) {
        try {
            if (nombreExchange == null || nombreExchange.trim().isEmpty()) {
                return AdminResponse.error("El nombre del exchange es obligatorio.");
            }
            boolean eliminado = amqpAdmin.deleteExchange(nombreExchange.trim());
            if (eliminado) {
                log.info("Exchange eliminado exitosamente: {}", nombreExchange);
                return AdminResponse.ok("Exchange '" + nombreExchange + "' eliminado exitosamente.");
            } else {
                return AdminResponse.error("No se pudo eliminar el exchange '" + nombreExchange + "' (quizás no existía).");
            }
        } catch (Exception e) {
            log.error("Error al eliminar el exchange {}: {}", nombreExchange, e.getMessage());
            return AdminResponse.error("Error al eliminar el exchange: " + e.getMessage());
        }
    }

    /**
     * Crea un binding entre una cola y un exchange con un routingKey.
     */
    public AdminResponse crearBinding(AdminBindingRequest request) {
        try {
            if (request.getQueue() == null || request.getExchange() == null) {
                return AdminResponse.error("Tanto la cola como el exchange son obligatorios para crear el binding.");
            }

            String routingKey = request.getRoutingKey() != null ? request.getRoutingKey() : "";

            Binding binding = new Binding(
                    request.getQueue().trim(),
                    Binding.DestinationType.QUEUE,
                    request.getExchange().trim(),
                    routingKey.trim(),
                    request.getArguments()
            );

            amqpAdmin.declareBinding(binding);
            log.info("Binding creado: Cola [{}] -> Exchange [{}] con RoutingKey [{}]",
                    request.getQueue(), request.getExchange(), routingKey);
            return AdminResponse.ok("Binding creado exitosamente entre cola '" + request.getQueue() +
                    "' y exchange '" + request.getExchange() + "' con routingKey '" + routingKey + "'.", binding);
        } catch (Exception e) {
            log.error("Error al crear el binding: {}", e.getMessage());
            return AdminResponse.error("Error al crear el binding: " + e.getMessage());
        }
    }

    /**
     * Elimina un binding existente.
     */
    public AdminResponse eliminarBinding(AdminBindingRequest request) {
        try {
            if (request.getQueue() == null || request.getExchange() == null) {
                return AdminResponse.error("Tanto la cola como el exchange son obligatorios para eliminar el binding.");
            }

            String routingKey = request.getRoutingKey() != null ? request.getRoutingKey() : "";

            Binding binding = new Binding(
                    request.getQueue().trim(),
                    Binding.DestinationType.QUEUE,
                    request.getExchange().trim(),
                    routingKey.trim(),
                    request.getArguments()
            );

            amqpAdmin.removeBinding(binding);
            log.info("Binding eliminado: Cola [{}] del Exchange [{}] con RoutingKey [{}]",
                    request.getQueue(), request.getExchange(), routingKey);
            return AdminResponse.ok("Binding eliminado exitosamente.", binding);
        } catch (Exception e) {
            log.error("Error al eliminar el binding: {}", e.getMessage());
            return AdminResponse.error("Error al eliminar el binding: " + e.getMessage());
        }
    }
}

