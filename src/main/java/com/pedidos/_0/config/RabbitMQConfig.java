package com.pedidos._0.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    // Exchanges
    public static final String EXCHANGE_PEDIDOS = "pedidos.exchange";
    public static final String EXCHANGE_DLX = "pedidos.dlx";

    // Colas Principales
    public static final String QUEUE_NOTIFICACIONES = "pedidos.notificaciones.queue";
    public static final String QUEUE_TICKETS = "pedidos.tickets.queue";
    public static final String QUEUE_DOCUMENTOS = "pedidos.documentos.queue";

    // Colas DLQ (Dead Letter Queues)
    public static final String DLQ_NOTIFICACIONES = "pedidos.notificaciones.dlq";
    public static final String DLQ_TICKETS = "pedidos.tickets.dlq";
    public static final String DLQ_DOCUMENTOS = "pedidos.documentos.dlq";

    // Routing Keys Principales
    public static final String ROUTING_KEY_NOTIFICACION = "pedidos.notificacion.#";
    public static final String ROUTING_KEY_TICKET = "pedidos.ticket.#";
    public static final String ROUTING_KEY_DOCUMENTO = "pedidos.documento.#";

    // Routing Keys DLQ
    public static final String ROUTING_KEY_NOTIFICACION_DLQ = "pedidos.notificacion.dlq";
    public static final String ROUTING_KEY_TICKET_DLQ = "pedidos.ticket.dlq";
    public static final String ROUTING_KEY_DOCUMENTO_DLQ = "pedidos.documento.dlq";

    // ==========================================
    // EXCHANGES
    // ==========================================
    @Bean
    public TopicExchange pedidosExchange() {
        return new TopicExchange(EXCHANGE_PEDIDOS, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(EXCHANGE_DLX, true, false);
    }

    // ==========================================
    // COLAS PRINCIPALES (Con DLX configurado)
    // ==========================================
    @Bean
    public Queue notificacionesQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", EXCHANGE_DLX);
        args.put("x-dead-letter-routing-key", ROUTING_KEY_NOTIFICACION_DLQ);
        return new Queue(QUEUE_NOTIFICACIONES, true, false, false, args);
    }

    @Bean
    public Queue ticketsQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", EXCHANGE_DLX);
        args.put("x-dead-letter-routing-key", ROUTING_KEY_TICKET_DLQ);
        return new Queue(QUEUE_TICKETS, true, false, false, args);
    }

    @Bean
    public Queue documentosQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", EXCHANGE_DLX);
        args.put("x-dead-letter-routing-key", ROUTING_KEY_DOCUMENTO_DLQ);
        return new Queue(QUEUE_DOCUMENTOS, true, false, false, args);
    }

    // ==========================================
    // DEAD LETTER QUEUES (DLQs)
    // ==========================================
    @Bean
    public Queue notificacionesDlq() {
        return new Queue(DLQ_NOTIFICACIONES, true, false, false);
    }

    @Bean
    public Queue ticketsDlq() {
        return new Queue(DLQ_TICKETS, true, false, false);
    }

    @Bean
    public Queue documentosDlq() {
        return new Queue(DLQ_DOCUMENTOS, true, false, false);
    }

    // ==========================================
    // BINDINGS PRINCIPALES
    // ==========================================
    @Bean
    public Binding bindingNotificaciones(Queue notificacionesQueue, TopicExchange pedidosExchange) {
        return BindingBuilder.bind(notificacionesQueue).to(pedidosExchange).with(ROUTING_KEY_NOTIFICACION);
    }

    @Bean
    public Binding bindingTickets(Queue ticketsQueue, TopicExchange pedidosExchange) {
        return BindingBuilder.bind(ticketsQueue).to(pedidosExchange).with(ROUTING_KEY_TICKET);
    }

    @Bean
    public Binding bindingDocumentos(Queue documentosQueue, TopicExchange pedidosExchange) {
        return BindingBuilder.bind(documentosQueue).to(pedidosExchange).with(ROUTING_KEY_DOCUMENTO);
    }

    // ==========================================
    // BINDINGS DLQ
    // ==========================================
    @Bean
    public Binding bindingNotificacionesDlq(Queue notificacionesDlq, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(notificacionesDlq).to(deadLetterExchange).with(ROUTING_KEY_NOTIFICACION_DLQ);
    }

    @Bean
    public Binding bindingTicketsDlq(Queue ticketsDlq, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(ticketsDlq).to(deadLetterExchange).with(ROUTING_KEY_TICKET_DLQ);
    }

    @Bean
    public Binding bindingDocumentosDlq(Queue documentosDlq, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(documentosDlq).to(deadLetterExchange).with(ROUTING_KEY_DOCUMENTO_DLQ);
    }

    // ==========================================
    // CONVERTIDOR DE MENSAJES Y ADMIN
    // ==========================================
    @Bean
    public MessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}

