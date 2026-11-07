# ========================================================
# Script de Demostración Integral - RabbitMQ en Pedidos360
# ========================================================

$baseUrl = "http://localhost:8080"

Write-Host "`n========================================================" -ForegroundColor Cyan
Write-Host " 1. VERIFICAR QUE LA LÓGICA EXISTENTE NO FUE AFECTADA" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan
try {
    $horaRes = Invoke-RestMethod -Uri "$baseUrl/api/hora" -Method Get
    Write-Host "OK -> GET /api/hora:" -ForegroundColor Green
    $horaRes | ConvertTo-Json
} catch {
    Write-Host "Error al llamar /api/hora: $_" -ForegroundColor Red
}

Write-Host "`n========================================================" -ForegroundColor Cyan
Write-Host " 2. MICROSERVICIO ADMINISTRADOR DE RABBITMQ" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan

# 2.1 Crear una Cola Dinámica
Write-Host "`n[2.1] Creando Cola personalizada 'cola.facturacion.express'..." -ForegroundColor Yellow
$crearColaBody = @{
    name = "cola.facturacion.express"
    durable = $true
    deadLetterExchange = "pedidos.dlx"
    deadLetterRoutingKey = "facturacion.express.dlq"
} | ConvertTo-Json
$resCola = Invoke-RestMethod -Uri "$baseUrl/api/admin/queues" -Method Post -Body $crearColaBody -ContentType "application/json"
$resCola | ConvertTo-Json

# 2.2 Crear un Exchange Dinámico
Write-Host "`n[2.2] Creando Exchange personalizado 'exchange.facturacion' (tipo: direct)..." -ForegroundColor Yellow
$crearExBody = @{
    name = "exchange.facturacion"
    type = "direct"
    durable = $true
} | ConvertTo-Json
$resEx = Invoke-RestMethod -Uri "$baseUrl/api/admin/exchanges" -Method Post -Body $crearExBody -ContentType "application/json"
$resEx | ConvertTo-Json

# 2.3 Crear Binding Dinámico
Write-Host "`n[2.3] Creando Binding entre 'cola.facturacion.express' y 'exchange.facturacion'..." -ForegroundColor Yellow
$crearBindingBody = @{
    queue = "cola.facturacion.express"
    exchange = "exchange.facturacion"
    routingKey = "factura.rapida"
} | ConvertTo-Json
$resBinding = Invoke-RestMethod -Uri "$baseUrl/api/admin/bindings" -Method Post -Body $crearBindingBody -ContentType "application/json"
$resBinding | ConvertTo-Json

# 2.4 Eliminar Binding
Write-Host "`n[2.4] Eliminando Binding..." -ForegroundColor Yellow
$resDelBinding = Invoke-RestMethod -Uri "$baseUrl/api/admin/bindings" -Method Delete -Body $crearBindingBody -ContentType "application/json"
$resDelBinding | ConvertTo-Json

# 2.5 Eliminar Exchange
Write-Host "`n[2.5] Eliminando Exchange..." -ForegroundColor Yellow
$resDelEx = Invoke-RestMethod -Uri "$baseUrl/api/admin/exchanges/exchange.facturacion" -Method Delete
$resDelEx | ConvertTo-Json

# 2.6 Eliminar Cola
Write-Host "`n[2.6] Eliminando Cola..." -ForegroundColor Yellow
$resDelCola = Invoke-RestMethod -Uri "$baseUrl/api/admin/queues/cola.facturacion.express" -Method Delete
$resDelCola | ConvertTo-Json

Write-Host "`n========================================================" -ForegroundColor Cyan
Write-Host " 3. MENSAJERÍA DESACOPLADA (ASÍNCRONA)" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan

# 3.1 Procesar Pedido (Dispara Notificación + Ticket + Documento en paralelo)
Write-Host "`n[3.1] Simulando Pedido Desacoplado (dispara 3 tareas en segundo plano)..." -ForegroundColor Yellow
$pedidoBody = @{
    cliente = "Carlos Soto (carlos@correo.cl)"
    total = 32990
} | ConvertTo-Json
$resPedido = Invoke-RestMethod -Uri "$baseUrl/api/pedidos/procesar" -Method Post -Body $pedidoBody -ContentType "application/json"
$resPedido | ConvertTo-Json

Start-Sleep -Seconds 1

Write-Host "`n========================================================" -ForegroundColor Cyan
Write-Host " 4. DEMOSTRACIÓN DE DEAD LETTER QUEUE (DLQ)" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan

# 4.1 Enviar tarea con error forzado para probar que viaja a la DLQ
Write-Host "`n[4.1] Enviando mensaje con fallo simulado hacia Notificaciones..." -ForegroundColor Yellow
$resDlq = Invoke-RestMethod -Uri "$baseUrl/api/tareas/probar-dlq?tipo=notificacion" -Method Post
$resDlq | ConvertTo-Json

Start-Sleep -Seconds 1

Write-Host "`n========================================================" -ForegroundColor Cyan
Write-Host " 5. CONSULTA DE HISTORIAL EN MEMORIA" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan
$historial = Invoke-RestMethod -Uri "$baseUrl/api/tareas/historial" -Method Get
Write-Host "`nTareas Procesadas Exitosamente:" -ForegroundColor Green
$historial.tareasProcesadas | Format-Table -AutoSize
Write-Host "`nTareas en Dead Letter Queue (DLQ):" -ForegroundColor Magenta
$historial.tareasDlq | Format-Table -AutoSize

