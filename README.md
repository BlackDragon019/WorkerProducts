# Worker Java + Go (Pedidos Processing)

Este repositorio contiene un *worker* en Java que consume mensajes de Kafka, enriquece con APIs en Go (catalog-service-go) y persiste en MongoDB. Se usa Redis para locks distribuidos y almacenamiento de mensajes fallidos.

## Servicios
- catalog-service-go: API Go con endpoints:
  - GET /api/v1/products/{id}
  - GET /api/v1/customers/{id}
- catalog-service-java/worker: Worker Spring Boot (WebFlux) que consume `orders-topic` y persiste en MongoDB.
- infrastructure/docker-compose.yml: orquesta Kafka, Zookeeper, Redis, MongoDB, el servicio Go y el worker Java.

## Cómo ejecutar (con Docker)
1. Desde la carpeta `infrastructure` ejecutar:
   ```bash
   docker-compose up --build
   ```
2. El worker leerá mensajes de Kafka (`orders-topic`).
3. Puedes crear productos en la API Go:
   ```bash
   curl -X POST -H "Content-Type: application/json" http://localhost:8081/api/v1/products -d '{"productId":"p1","name":"Laptop","price":999}'

   ```PowerShell
   Invoke-RestMethod -Uri 'http://localhost:8081/api/v1/products' -Method Post -ContentType 'application/json' -Body '{"productId":"p1","name":"Laptop","price":1199}'
   ```
4. Enviar un mensaje de prueba a Kafka (por ejemplo usando kafka-console-producer o una herramienta):
   Topic: orders-topic
   Mensaje (JSON):
   ```json
   {"orderId":"o123","customerId":"c1","productIds":["p1"]}
   ```

## Notas
- Ver lista de productos:
Navegador: http://localhost:8081/api/v1/products

- Consultar MongoDB directamente (mongosh)
- Conéctate al contenedor de Mongo y consulta la colección:
docker exec -it mongodb-worker mongosh --eval "db.getSiblingDB('orders_db').orders_processed.find({orderId:'order-e2e-5'}).pretty()"
- También puedes listar todas:
docker exec -it mongodb-worker mongosh --eval "db.getSiblingDB('orders_db').orders_processed.find().limit(10).pretty()"
