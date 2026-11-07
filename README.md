# Pedidos360 - Arquitectura Cloud Native con RabbitMQ

Este proyecto incorpora **RabbitMQ** para desacoplar el procesamiento asíncrono de tareas y administrar colas, exchanges y Dead Letter Queues (DLQ) sin afectar la lógica existente del sistema.

---

## 🏗️ Arquitectura Implementada

### 1. Lógica Existente (Intacta)
- Endpoint: `GET /api/hora` (implementado en `TimeController`).
- Mantiene su contrato y funcionamiento original.

### 2. Mensajería Asíncrona Desacoplada
- **Exchange Principal**: `pedidos.exchange` (tipo `TopicExchange`).
- **Colas Asíncronas**:
  - `pedidos.notificaciones.queue` (para emails/SMS/push a clientes).
  - `pedidos.tickets.queue` (para tickets de comandas/cocina).
  - `pedidos.documentos.queue` (para generación de facturas/boletas PDF).
- **Dead Letter Exchange (DLX)**: `pedidos.dlx` (tipo `DirectExchange`).
- **Dead Letter Queues (DLQ)**:
  - `pedidos.notificaciones.dlq`
  - `pedidos.tickets.dlq`
  - `pedidos.documentos.dlq`

### 3. Microservicio Administrador de RabbitMQ
Controlador REST (`/api/admin`) que permite:
- **Colas**: `POST /api/admin/queues`, `DELETE /api/admin/queues/{name}`
- **Exchanges**: `POST /api/admin/exchanges`, `DELETE /api/admin/exchanges/{name}`
- **Bindings**: `POST /api/admin/bindings`, `DELETE /api/admin/bindings`

---

## 🚀 Pasos para Hacerlo Funcionar

### Paso 1: Iniciar RabbitMQ
Tienes dos alternativas:

#### Opción A (Recomendada con Docker):
Asegúrate de tener Docker Desktop abierto y ejecuta en la terminal de la raíz del proyecto:
```bash
docker compose up -d
```
> O si prefieres ejecutarlo directamente con Docker CLI:
> ```bash
> docker run -d --name rabbitmq-pedidos360 -p 5672:5672 -p 15672:15672 -e RABBITMQ_DEFAULT_USER=guest -e RABBITMQ_DEFAULT_PASS=guest rabbitmq:3-management
> ```
- **Consola Web de RabbitMQ**: [http://localhost:15672](http://localhost:15672) (Usuario: `guest`, Clave: `guest`).

#### Opción B (RabbitMQ instalado localmente en Windows):
Si tienes RabbitMQ Server instalado como servicio de Windows, solo asegúrate de que el servicio esté iniciado en los puertos `5672` y `15672`.

---

### Paso 2: Ejecutar la Aplicación Spring Boot
Abre una terminal en `pedidos360` y ejecuta:
```powershell
.\mvnw.cmd spring-boot:run
```
O simplemente presiona **Run / Debug** en `Application.java` desde tu IDE (VS Code, IntelliJ IDEA o Eclipse).

---

### Paso 3: Demostración y Pruebas

#### Forma Automática (1 comando):
Ejecuta el script PowerShell incluido:
```powershell
.\probar-todo.ps1
```

#### Forma Manual:
Puedes usar el archivo [requests.http](file:///c:/_RGS_2026/Semestre%206/DESARROLLO%20CLOUD%20NATIVE%20I/Entrega%201%20Software/pedidos360/requests.http) con la extensión REST Client de VS Code o utilizar `curl`:

1. **Verificar que la lógica existente no cambió:**
   ```bash
   curl http://localhost:8080/api/hora
   ```
2. **Crear una cola dinámica:**
   ```bash
   curl -X POST http://localhost:8080/api/admin/queues -H "Content-Type: application/json" -d "{\"name\":\"cola.demostracion\",\"durable\":true}"
   ```
3. **Crear un exchange dinámico:**
   ```bash
   curl -X POST http://localhost:8080/api/admin/exchanges -H "Content-Type: application/json" -d "{\"name\":\"exchange.demostracion\",\"type\":\"topic\"}"
   ```
4. **Crear un binding:**
   ```bash
   curl -X POST http://localhost:8080/api/admin/bindings -H "Content-Type: application/json" -d "{\"queue\":\"cola.demostracion\",\"exchange\":\"exchange.demostracion\",\"routingKey\":\"demo.*\"}"
   ```
5. **Demostrar desacoplamiento procesando un pedido:**
   ```bash
   curl -X POST http://localhost:8080/api/pedidos/procesar -H "Content-Type: application/json" -d "{\"cliente\":\"Juan Perez\",\"total\":25000}"
   ```
   *Observa la consola de Spring Boot: verás cómo se despachan de forma asíncrona la notificación, el ticket y el documento simultáneamente sin bloquear la respuesta HTTP.*

6. **Demostrar Dead Letter Queue (DLQ):**
   ```bash
   curl -X POST "http://localhost:8080/api/tareas/probar-dlq?tipo=notificacion"
   ```
   *El consumidor rechaza intencionalmente el mensaje y RabbitMQ lo enruta a `pedidos.notificaciones.dlq`.*

7. **Consultar historial:**
   ```bash
   curl http://localhost:8080/api/tareas/historial
   ```

