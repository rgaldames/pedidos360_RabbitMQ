package com.pedidos._0.controller;

import com.pedidos._0.dto.TareaMessage;
import com.pedidos._0.producer.TareaProducerService;
import com.pedidos._0.service.HistorialTareasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TareaController {

    private final TareaProducerService producerService;
    private final HistorialTareasService historialService;

    public TareaController(TareaProducerService producerService, HistorialTareasService historialService) {
        this.producerService = producerService;
        this.historialService = historialService;
    }

    /**
     * Envía una notificación de forma asíncrona mediante RabbitMQ.
     */
    @PostMapping("/tareas/notificacion")
    public ResponseEntity<Map<String, Object>> enviarNotificacion(@RequestBody(required = false) Map<String, Object> body) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        String destinatario = body != null && body.containsKey("destinatario") ? (String) body.get("destinatario") : "cliente@ejemplo.com";
        String contenido = body != null && body.containsKey("contenido") ? (String) body.get("contenido") : "Su pedido ha sido recibido con éxito.";

        TareaMessage tarea = new TareaMessage(id, "NOTIFICACION", destinatario, contenido, body, false);
        producerService.enviarNotificacion(tarea);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "ENCOLADO");
        response.put("mensaje", "Tarea de notificación enviada a RabbitMQ de manera asíncrona.");
        response.put("tareaId", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Envía una generación de ticket de forma asíncrona mediante RabbitMQ.
     */
    @PostMapping("/tareas/ticket")
    public ResponseEntity<Map<String, Object>> enviarTicket(@RequestBody(required = false) Map<String, Object> body) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        String destinatario = body != null && body.containsKey("destinatario") ? (String) body.get("destinatario") : "cajero@pedidos360.com";
        String contenido = body != null && body.containsKey("contenido") ? (String) body.get("contenido") : "Ticket de compra por $15.990";

        TareaMessage tarea = new TareaMessage(id, "TICKET", destinatario, contenido, body, false);
        producerService.enviarTicket(tarea);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "ENCOLADO");
        response.put("mensaje", "Tarea de generación de ticket enviada a RabbitMQ de manera asíncrona.");
        response.put("tareaId", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Envía una generación de documento (factura/remito) de forma asíncrona mediante RabbitMQ.
     */
    @PostMapping("/tareas/documento")
    public ResponseEntity<Map<String, Object>> enviarDocumento(@RequestBody(required = false) Map<String, Object> body) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        String destinatario = body != null && body.containsKey("destinatario") ? (String) body.get("destinatario") : "contabilidad@pedidos360.com";
        String contenido = body != null && body.containsKey("contenido") ? (String) body.get("contenido") : "Factura electrónica N° 10842";

        TareaMessage tarea = new TareaMessage(id, "DOCUMENTO", destinatario, contenido, body, false);
        producerService.enviarDocumento(tarea);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "ENCOLADO");
        response.put("mensaje", "Tarea de generación de documento enviada a RabbitMQ de manera asíncrona.");
        response.put("tareaId", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Demostración de desacoplamiento completo: Simula la creación de un pedido en el sistema,
     * el cual delega de inmediato tareas asíncronas a RabbitMQ (Notificación, Ticket y Documento)
     * retornando respuesta inmediata al cliente sin bloquear el flujo principal.
     */
    @PostMapping("/pedidos/procesar")
    public ResponseEntity<Map<String, Object>> procesarPedido(@RequestBody(required = false) Map<String, Object> body) {
        String pedidoId = "PED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String cliente = body != null && body.containsKey("cliente") ? (String) body.get("cliente") : "Juan Perez (juan@pedidos.cl)";
        Double total = body != null && body.containsKey("total") ? Double.valueOf(body.get("total").toString()) : 24990.0;

        Map<String, Object> detalles = new HashMap<>();
        detalles.put("pedidoId", pedidoId);
        detalles.put("cliente", cliente);
        detalles.put("total", total);

        // 1. Desacoplamiento asíncrono: Publicar Notificación
        TareaMessage notificacion = new TareaMessage(
                UUID.randomUUID().toString().substring(0, 8),
                "NOTIFICACION",
                cliente,
                "Confirmación de Pedido " + pedidoId + " por un total de $" + total,
                detalles,
                false
        );
        producerService.enviarNotificacion(notificacion);

        // 2. Desacoplamiento asíncrono: Publicar Ticket
        TareaMessage ticket = new TareaMessage(
                UUID.randomUUID().toString().substring(0, 8),
                "TICKET",
                "Cocina/Despacho",
                "Ticket de preparación para pedido " + pedidoId,
                detalles,
                false
        );
        producerService.enviarTicket(ticket);

        // 3. Desacoplamiento asíncrono: Publicar Documento Factura
        TareaMessage documento = new TareaMessage(
                UUID.randomUUID().toString().substring(0, 8),
                "DOCUMENTO",
                cliente,
                "Boleta/Factura electrónica para pedido " + pedidoId,
                detalles,
                false
        );
        producerService.enviarDocumento(documento);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "PEDIDO_CREADO_EXITOSAMENTE");
        response.put("pedidoId", pedidoId);
        response.put("mensaje", "El pedido fue registrado y las tareas (Notificación, Ticket y Documento) fueron encoladas asíncronamente en RabbitMQ.");
        response.put("detalles", detalles);
        return ResponseEntity.ok(response);
    }

    /**
     * Prueba específica para demostrar Dead Letter Queue (DLQ):
     * Envía una tarea con simularError = true. El consumidor la rechazará y RabbitMQ la enviará a la DLQ.
     */
    @PostMapping("/tareas/probar-dlq")
    public ResponseEntity<Map<String, Object>> probarDlq(@RequestParam(defaultValue = "notificacion") String tipo) {
        String id = "ERR-" + UUID.randomUUID().toString().substring(0, 6);
        TareaMessage tarea = new TareaMessage(
                id,
                tipo.toUpperCase(),
                "test-error@pedidos.cl",
                "Mensaje de prueba con simulación de error forzado",
                Map.of("simulacion", true, "tipo", tipo),
                true // simularError = true
        );

        switch (tipo.toLowerCase()) {
            case "ticket":
                producerService.enviarTicket(tarea);
                break;
            case "documento":
                producerService.enviarDocumento(tarea);
                break;
            case "notificacion":
            default:
                producerService.enviarNotificacion(tarea);
                break;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "ENVIADO_CON_ERROR_SIMULADO");
        response.put("tareaId", id);
        response.put("tipo", tipo);
        response.put("mensaje", "El mensaje fue publicado con bandera 'simularError=true'. El consumidor lo rechazará y RabbitMQ lo moverá automáticamente a su Dead Letter Queue (DLQ).");
        return ResponseEntity.ok(response);
    }

    /**
     * Consulta el historial en memoria de tareas procesadas exitosamente y de mensajes recibidos en DLQ.
     */
    @GetMapping("/tareas/historial")
    public ResponseEntity<Map<String, Object>> verHistorial() {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("tareasProcesadas", historialService.getTareasProcesadas());
        resultado.put("tareasDlq", historialService.getTareasDlq());
        return ResponseEntity.ok(resultado);
    }
}

