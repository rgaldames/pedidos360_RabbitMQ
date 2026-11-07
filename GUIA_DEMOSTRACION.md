# Guía de demostración - Pedidos360 (todo con Docker)

Docker simula el entorno cloud: 2 nodos RabbitMQ en clúster + backend Spring Boot, todo con un solo `docker compose`.

## 1. Levantar todo
Abre Docker Desktop y, en la carpeta del proyecto:
```powershell
docker compose up -d --build
docker compose ps
```
Evidencia: `rabbit1`, `rabbit2` y `pedidos360-backend` en estado Up/healthy
(el primer build tarda unos minutos; el clúster tarda ~40 s en estar listo).

## 2. Dos nodos RabbitMQ en clúster
```powershell
docker exec rabbit1 rabbitmqctl cluster_status
```
Debe listar `rabbit@rabbit1` y `rabbit@rabbit2` en *Running Nodes*.
UI: http://localhost:15672 (guest/guest) → *Overview* → *Nodes* (2 nodos).

## 3. Exchanges direct y topic
UI → *Exchanges* (filtrar "pedidos"):
- `pedidos.exchange` → **topic**
- `pedidos.dlx` → **direct**

## 4. Tres colas con sus DLQ
UI → *Queues*:

| Cola principal | DLQ |
|---|---|
| pedidos.notificaciones.queue | pedidos.notificaciones.dlq |
| pedidos.tickets.queue | pedidos.tickets.dlq |
| pedidos.documentos.queue | pedidos.documentos.dlq |

## 5. Backend integrado y mensajería desacoplada
```powershell
.\probar-todo.ps1
```
Muestra: `/api/hora` intacto, administrador (crear/eliminar colas, exchanges y bindings), pedido que dispara notificación + ticket + documento asíncronos, mensaje con error que va a la DLQ, e historial.
Logs de los consumidores:
```powershell
docker logs pedidos360-backend --tail 50
```
(`requests.http` tiene las mismas peticiones por separado.)

## 6. Tolerancia a fallos del clúster (opcional)
```powershell
docker stop rabbit2
curl http://localhost:8080/api/hora
docker start rabbit2
```

## 7. Apagar
```powershell
docker compose down        # conserva datos
docker compose down -v     # borra volúmenes
```
