package com.pedidos._0.admin;

import com.pedidos._0.dto.AdminBindingRequest;
import com.pedidos._0.dto.AdminExchangeRequest;
import com.pedidos._0.dto.AdminQueueRequest;
import com.pedidos._0.dto.AdminResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class RabbitAdminController {

    private final RabbitAdminService adminService;

    public RabbitAdminController(RabbitAdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("modulo", "Microservicio Administrador RabbitMQ - Pedidos360");
        status.put("estado", "ACTIVO");
        status.put("acciones_disponibles", new String[]{
                "POST /api/admin/queues (crear cola)",
                "DELETE /api/admin/queues/{name} (eliminar cola)",
                "POST /api/admin/exchanges (crear exchange)",
                "DELETE /api/admin/exchanges/{name} (eliminar exchange)",
                "POST /api/admin/bindings (crear binding)",
                "DELETE /api/admin/bindings (eliminar binding)"
        });
        return ResponseEntity.ok(status);
    }

    // ==========================================
    // COLAS (QUEUES)
    // ==========================================
    @PostMapping("/queues")
    public ResponseEntity<AdminResponse> crearCola(@RequestBody AdminQueueRequest request) {
        AdminResponse response = adminService.crearCola(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/queues/{name}")
    public ResponseEntity<AdminResponse> eliminarCola(@PathVariable("name") String name) {
        AdminResponse response = adminService.eliminarCola(name);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    // ==========================================
    // EXCHANGES
    // ==========================================
    @PostMapping("/exchanges")
    public ResponseEntity<AdminResponse> crearExchange(@RequestBody AdminExchangeRequest request) {
        AdminResponse response = adminService.crearExchange(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/exchanges/{name}")
    public ResponseEntity<AdminResponse> eliminarExchange(@PathVariable("name") String name) {
        AdminResponse response = adminService.eliminarExchange(name);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    // ==========================================
    // BINDINGS
    // ==========================================
    @PostMapping("/bindings")
    public ResponseEntity<AdminResponse> crearBinding(@RequestBody AdminBindingRequest request) {
        AdminResponse response = adminService.crearBinding(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/bindings")
    public ResponseEntity<AdminResponse> eliminarBinding(@RequestBody AdminBindingRequest request) {
        AdminResponse response = adminService.eliminarBinding(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
}

