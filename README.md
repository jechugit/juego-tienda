# Proyecto Tienda de Videojuegos - Microservicios

Este proyecto implementa una tienda de videojuegos usando una arquitectura de microservicios con Spring Boot, Spring Cloud, Eureka, Config Server, API Gateway, OpenFeign, JPA, Flyway y MySQL.

La aplicacion esta separada por responsabilidades: catalogo de videojuegos, usuarios, autenticacion, carrito, pagos, pedidos, resenas e inventario. Los microservicios se descubren entre si usando Eureka y se consumen desde un unico punto de entrada mediante API Gateway.

## Arquitectura General

```text
Cliente / Postman / Navegador
        |
        v
API Gateway - puerto 8080
        |
        v
Eureka - descubrimiento de servicios - puerto 8761
        |
        +--> videojuegos
        +--> usuarios
        +--> authentication
        +--> carrito
        +--> pagos
        +--> pedidos
        +--> resenas
        +--> inventario

Config Server - puerto 8888
        |
        v
config-microservicios/*.properties
```

## Componentes del Proyecto

| Carpeta | Funcion |
| --- | --- |
| `eureka` | Servidor Eureka. Permite que los microservicios se registren y se encuentren entre si. |
| `config-server` | Servidor centralizado de configuracion. Lee los archivos de `config-microservicios`. |
| `api-gateway` | Entrada principal del sistema. Redirige las rutas publicas hacia cada microservicio. |
| `videojuegos` | Gestiona el catalogo de videojuegos. |
| `usuarios` | Gestiona usuarios, roles y datos personales. |
| `authentication` | Gestiona registro, login y credenciales. |
| `carrito` | Gestiona items del carrito por usuario. |
| `pagos` | Gestiona pagos a partir del resumen del carrito. |
| `pedidos` | Gestiona pedidos y reportes de pedidos. |
| `resenas` | Gestiona resenas y reportes de resenas. |
| `inventario` | Gestiona stock de videojuegos. |
| `config-microservicios` | Contiene la configuracion externa de los microservicios. |

## Tecnologias Usadas

- Java 25
- Spring Boot 4.0.6
- Spring Cloud 2025.1.1
- Spring Cloud Config Server
- Spring Cloud Netflix Eureka
- Spring Cloud Gateway WebFlux
- Spring Cloud OpenFeign
- Spring Data JPA
- Spring Validation
- Flyway
- MySQL
- Lombok
- Maven Wrapper

## Requisitos

Antes de ejecutar el proyecto necesitas:

- Java 25 instalado.
- MySQL activo en `localhost:3306`.
- Maven no es obligatorio porque cada microservicio trae su propio `mvnw`.
- Usuario de base de datos `root`.
- Password vacia para MySQL, o configurar otra usando variables de entorno.

Por defecto los microservicios usan:

```properties
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=
```

Cada microservicio crea su propia base de datos automaticamente con `createDatabaseIfNotExist=true`.

## Bases de Datos

El proyecto usa una base de datos por microservicio:

| Microservicio | Base de datos |
| --- | --- |
| `videojuegos` | `bd_videojuegos` |
| `usuarios` | `bd_usuarios` |
| `authentication` | `bd_auth` |
| `carrito` | `bd_carrito` |
| `pagos` | `bd_pagos` |
| `pedidos` | `bd_pedidos` |
| `resenas` | `bd_resenas` |
| `inventario` | `bd_inventario` |

Las tablas y datos iniciales se cargan con Flyway desde:

```text
src/main/resources/db/migration
```

## Orden de Ejecucion

Es importante levantar los servicios en este orden.

### 1. Levantar Eureka

```bash
cd eureka
./mvnw spring-boot:run
```

Eureka quedara disponible en:

```text
http://localhost:8761
```

### 2. Levantar Config Server

```bash
cd config-server
./mvnw spring-boot:run
```

Config Server quedara disponible en:

```text
http://localhost:8888
```

Ejemplo para revisar configuracion:

```text
http://localhost:8888/videojuegos/default
http://localhost:8888/api-gateway/default
```

### 3. Levantar los Microservicios de Negocio

Abrir una terminal por cada microservicio:

```bash
cd videojuegos
./mvnw spring-boot:run
```

```bash
cd usuarios
./mvnw spring-boot:run
```

```bash
cd authentication
./mvnw spring-boot:run
```

```bash
cd carrito
./mvnw spring-boot:run
```

```bash
cd pagos
./mvnw spring-boot:run
```

```bash
cd pedidos
./mvnw spring-boot:run
```

```bash
cd resenas
./mvnw spring-boot:run
```

```bash
cd inventario
./mvnw spring-boot:run
```

### 4. Levantar API Gateway

```bash
cd api-gateway
./mvnw spring-boot:run
```

El gateway quedara disponible en:

```text
http://localhost:8080
```

## Puertos

| Servicio | Puerto |
| --- | --- |
| Eureka | `8761` |
| Config Server | `8888` |
| API Gateway | `8080` |
| Microservicios de negocio | Puerto aleatorio, configurado con `server.port=0` |

Los microservicios usan puerto aleatorio porque se comunican por nombre mediante Eureka, por ejemplo:

```text
lb://videojuegos
lb://usuarios
lb://carrito
```

## Rutas del API Gateway

Todas estas rutas se consumen desde:

```text
http://localhost:8080
```

| Ruta | Microservicio |
| --- | --- |
| `/videojuegos` | `videojuegos` |
| `/usuarios` | `usuarios` |
| `/auth` | `authentication` |
| `/carrito` | `carrito` |
| `/pagos` | `pagos` |
| `/pedidos` | `pedidos` |
| `/resenas` | `resenas` |
| `/inventario` | `inventario` |

## Flujo Principal del Sistema

Un flujo normal de uso puede ser:

1. Registrar un usuario en `/auth/registro`.
2. Iniciar sesion en `/auth/login`.
3. Consultar videojuegos en `/videojuegos`.
4. Agregar productos al carrito en `/carrito`.
5. Consultar resumen del carrito en `/carrito/usuario/{usuarioId}/resumen`.
6. Crear un pago en `/pagos`.
7. El servicio de pagos aprueba el pago y vacia el carrito.
8. Opcionalmente crear pedidos en `/pedidos`.
9. Opcionalmente crear resenas en `/resenas`.
10. Consultar o actualizar stock en `/inventario`.

Importante: el pago no crea automaticamente un pedido ni descuenta inventario. Esas operaciones existen en servicios separados.

## Microservicio Videojuegos

Gestiona el catalogo de videojuegos.

### Modelo principal

Campos relevantes:

- `id`
- `nombre`
- `categoria`
- `precio`
- `plataforma`
- `descripcion`
- `desarrollador`
- `fechaLanzamiento`
- `imagenUrl`
- `activo`

### Endpoints

| Metodo | Ruta | Funcion |
| --- | --- | --- |
| `GET` | `/videojuegos` | Lista todos los videojuegos. |
| `GET` | `/videojuegos/{id}` | Busca un videojuego por ID. |
| `POST` | `/videojuegos` | Crea un videojuego. |
| `PUT` | `/videojuegos/{id}` | Actualiza un videojuego. |
| `DELETE` | `/videojuegos/{id}` | Elimina un videojuego. |
| `GET` | `/videojuegos/buscar?nombre=...` | Busca por nombre. |
| `GET` | `/videojuegos/buscar?categoria=...` | Busca por categoria. |
| `GET` | `/videojuegos/buscar?plataforma=...` | Busca por plataforma. |

### Ejemplo de creacion

```json
{
  "nombre": "Hades",
  "categoria": "Roguelike",
  "precio": 14990,
  "plataforma": "PC",
  "descripcion": "Juego de accion roguelike.",
  "desarrollador": "Supergiant Games",
  "fechaLanzamiento": "2020-09-17",
  "imagenUrl": "https://example.com/hades.jpg",
  "activo": true
}
```

## Microservicio Usuarios

Gestiona los usuarios del sistema.

### Modelo principal

Campos relevantes:

- `id`
- `nombre`
- `apellido`
- `correo`
- `telefono`
- `direccion`
- `rol`
- `activo`
- `fechaRegistro`

### Endpoints

| Metodo | Ruta | Funcion |
| --- | --- | --- |
| `GET` | `/usuarios` | Lista todos los usuarios. |
| `GET` | `/usuarios?activos=true` | Lista solo usuarios activos. |
| `GET` | `/usuarios/{id}` | Busca usuario por ID. |
| `GET` | `/usuarios/buscar?correo=...` | Busca usuario por correo. |
| `POST` | `/usuarios` | Crea un usuario. |
| `PUT` | `/usuarios/{id}` | Actualiza un usuario. |
| `DELETE` | `/usuarios/{id}` | Desactiva un usuario. |

### Ejemplo de creacion

```json
{
  "nombre": "Juan",
  "apellido": "Perez",
  "correo": "juan@tiendajuegos.cl",
  "telefono": "+56912345678",
  "direccion": "Santiago",
  "rol": "CLIENTE",
  "activo": true
}
```

## Microservicio Authentication

Gestiona registro, login y credenciales.

Este servicio se comunica con `usuarios` usando OpenFeign:

- Al registrarse, crea primero el usuario en `usuarios`.
- Luego guarda la credencial en `bd_auth`.
- La password se guarda encriptada con BCrypt.

### Endpoints

| Metodo | Ruta | Funcion |
| --- | --- | --- |
| `POST` | `/auth/registro` | Registra usuario y credencial. |
| `POST` | `/auth/login` | Valida correo y password. |
| `GET` | `/auth/credenciales` | Lista credenciales. |
| `GET` | `/auth/credenciales/{id}` | Busca credencial por ID. |
| `PUT` | `/auth/credenciales/{id}/password` | Cambia password. |
| `DELETE` | `/auth/credenciales/{id}` | Desactiva credencial. |

### Ejemplo de registro

```json
{
  "nombre": "Juan",
  "apellido": "Perez",
  "correo": "juan@tiendajuegos.cl",
  "telefono": "+56912345678",
  "direccion": "Santiago",
  "rol": "CLIENTE",
  "password": "123456"
}
```

### Ejemplo de login

```json
{
  "correo": "juan@tiendajuegos.cl",
  "password": "123456"
}
```

Respuesta esperada:

```json
{
  "usuarioId": 5,
  "correo": "juan@tiendajuegos.cl",
  "rol": "CLIENTE",
  "mensaje": "Login exitoso",
  "autenticado": true
}
```

Nota: este proyecto no genera JWT ni maneja sesiones. El login solo valida credenciales y devuelve una respuesta simple.

## Microservicio Carrito

Gestiona los productos agregados al carrito por usuario.

Este servicio se comunica con `videojuegos` usando OpenFeign para validar que el videojuego exista y obtener su precio.

### Modelo principal

Campos relevantes:

- `id`
- `usuarioId`
- `videojuegoId`
- `cantidad`
- `precioUnitario`
- `subtotal`
- `fechaAgregado`

### Endpoints

| Metodo | Ruta | Funcion |
| --- | --- | --- |
| `GET` | `/carrito/usuario/{usuarioId}` | Lista items del carrito de un usuario. |
| `GET` | `/carrito/usuario/{usuarioId}/resumen` | Muestra items y total del carrito. |
| `GET` | `/carrito/{id}` | Busca item por ID. |
| `POST` | `/carrito` | Agrega un videojuego al carrito. |
| `PUT` | `/carrito/{id}/cantidad` | Actualiza cantidad de un item. |
| `DELETE` | `/carrito/{id}` | Elimina un item. |
| `DELETE` | `/carrito/usuario/{usuarioId}` | Vacia el carrito de un usuario. |

### Ejemplo de agregar item

```json
{
  "usuarioId": 2,
  "videojuegoId": 1,
  "cantidad": 1
}
```

Si el item ya existe para ese usuario y videojuego, el sistema suma la cantidad.

### Ejemplo de actualizar cantidad

```json
{
  "cantidad": 3
}
```

## Microservicio Pagos

Gestiona pagos de usuarios.

Este servicio se comunica con `carrito` usando OpenFeign:

- Consulta el resumen del carrito.
- Valida que tenga total mayor a 0.
- Crea un pago con estado `APROBADO`.
- Genera un codigo de transaccion.
- Vacia el carrito del usuario.

### Modelo principal

Campos relevantes:

- `id`
- `usuarioId`
- `monto`
- `metodoPago`
- `estado`
- `codigoTransaccion`
- `fechaPago`

### Endpoints

| Metodo | Ruta | Funcion |
| --- | --- | --- |
| `GET` | `/pagos` | Lista todos los pagos. |
| `GET` | `/pagos/{id}` | Busca pago por ID. |
| `GET` | `/pagos/usuario/{usuarioId}` | Lista pagos de un usuario. |
| `POST` | `/pagos` | Crea un pago desde el carrito. |
| `PUT` | `/pagos/{id}/estado` | Cambia estado del pago. |
| `PUT` | `/pagos/{id}/anular` | Anula un pago. |
| `DELETE` | `/pagos/{id}` | Elimina un pago. |

### Ejemplo de crear pago

```json
{
  "usuarioId": 2,
  "metodoPago": "TARJETA"
}
```

Estados usados por el proyecto:

- `APROBADO`
- `ANULADO`

Tambien se puede enviar otro estado mediante `/pagos/{id}/estado`.

## Microservicio Pedidos

Gestiona pedidos y reportes.

Este servicio valida que el usuario exista llamando a `usuarios` con OpenFeign.

### Modelo principal

Campos relevantes:

- `id`
- `usuarioId`
- `nombreJuego`
- `precio`
- `fechaPedido`

### Endpoints

| Metodo | Ruta | Funcion |
| --- | --- | --- |
| `GET` | `/pedidos` | Lista todos los pedidos. |
| `GET` | `/pedidos/{id}` | Busca pedido por ID. |
| `POST` | `/pedidos` | Crea un pedido. |
| `PUT` | `/pedidos/{id}` | Actualiza un pedido. |
| `DELETE` | `/pedidos/{id}` | Elimina un pedido. |
| `GET` | `/pedidos/detalle` | Lista pedidos con datos del usuario. |
| `GET` | `/pedidos/usuario/{usuarioId}` | Lista pedidos de un usuario. |
| `GET` | `/pedidos/reportes/fecha?desde=YYYY-MM-DD&hasta=YYYY-MM-DD` | Reporte por rango de fecha. |
| `GET` | `/pedidos/reportes/precio?minimo=...&maximo=...` | Reporte por rango de precio. |

### Ejemplo de creacion

```json
{
  "usuarioId": 2,
  "nombreJuego": "Cyberpunk 2077",
  "precio": 37990,
  "fechaPedido": "2026-05-19"
}
```

## Microservicio Resenas

Gestiona resenas de videojuegos.

Este servicio valida que el usuario exista llamando a `usuarios` con OpenFeign.

### Modelo principal

Campos relevantes:

- `id`
- `usuarioId`
- `nombreJuego`
- `comentario`
- `puntuacion`
- `fechaResena`

### Endpoints

| Metodo | Ruta | Funcion |
| --- | --- | --- |
| `GET` | `/resenas` | Lista todas las resenas. |
| `GET` | `/resenas/{id}` | Busca resena por ID. |
| `POST` | `/resenas` | Crea una resena. |
| `PUT` | `/resenas/{id}` | Actualiza una resena. |
| `DELETE` | `/resenas/{id}` | Elimina una resena. |
| `GET` | `/resenas/detalle` | Lista resenas con datos del usuario. |
| `GET` | `/resenas/usuario/{usuarioId}` | Lista resenas de un usuario. |
| `GET` | `/resenas/reportes/fecha?desde=YYYY-MM-DD&hasta=YYYY-MM-DD` | Reporte por rango de fecha. |
| `GET` | `/resenas/reportes/puntuacion?min=1&max=5` | Reporte por puntuacion. |

### Ejemplo de creacion

```json
{
  "usuarioId": 2,
  "nombreJuego": "Cyberpunk 2077",
  "comentario": "Muy buen juego.",
  "puntuacion": 5,
  "fechaResena": "2026-05-19"
}
```

La puntuacion debe estar entre `1` y `5`.

## Microservicio Inventario

Gestiona stock de videojuegos.

Este servicio se comunica con `videojuegos` usando OpenFeign para validar que el videojuego exista.

### Modelo principal

Campos relevantes:

- `id`
- `videojuegoId`
- `stock`
- `stockMinimo`
- `fechaActualizacion`

### Endpoints

| Metodo | Ruta | Funcion |
| --- | --- | --- |
| `GET` | `/inventario` | Lista todo el inventario. |
| `GET` | `/inventario/bajo-stock` | Lista inventarios con stock menor o igual al minimo. |
| `GET` | `/inventario/{id}` | Busca inventario por ID. |
| `GET` | `/inventario/videojuego/{videojuegoId}` | Busca inventario por videojuego. |
| `POST` | `/inventario` | Crea inventario para un videojuego. |
| `PUT` | `/inventario/{id}` | Actualiza inventario. |
| `PUT` | `/inventario/videojuego/{videojuegoId}/stock` | Reemplaza el stock actual. |
| `PUT` | `/inventario/videojuego/{videojuegoId}/entrada` | Aumenta stock. |
| `PUT` | `/inventario/videojuego/{videojuegoId}/salida` | Disminuye stock. |
| `DELETE` | `/inventario/{id}` | Elimina inventario. |

### Ejemplo de crear inventario

```json
{
  "videojuegoId": 1,
  "stock": 20,
  "stockMinimo": 5
}
```

### Ejemplo de actualizar stock directo

```json
{
  "stock": 15
}
```

### Ejemplo de entrada o salida de stock

```json
{
  "cantidad": 3
}
```

Si se intenta hacer una salida mayor al stock disponible, el servicio responde con error de conflicto.

## Datos Iniciales

El proyecto carga datos iniciales con Flyway:

- Usuarios de ejemplo, incluyendo `admin@tiendajuegos.cl`.
- Videojuegos de ejemplo.
- Inventario inicial.
- Carrito inicial para usuario `2`.
- Pagos iniciales.
- Pedidos iniciales.
- Resenas iniciales.

## Ejemplos Rapidos con curl

Listar videojuegos:

```bash
curl http://localhost:8080/videojuegos
```

Buscar videojuego por ID:

```bash
curl http://localhost:8080/videojuegos/1
```

Registrar usuario:

```bash
curl -X POST http://localhost:8080/auth/registro \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan",
    "apellido": "Perez",
    "correo": "juan@tiendajuegos.cl",
    "telefono": "+56912345678",
    "direccion": "Santiago",
    "rol": "CLIENTE",
    "password": "123456"
  }'
```

Login:

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "juan@tiendajuegos.cl",
    "password": "123456"
  }'
```

Agregar item al carrito:

```bash
curl -X POST http://localhost:8080/carrito \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 2,
    "videojuegoId": 1,
    "cantidad": 1
  }'
```

Ver resumen del carrito:

```bash
curl http://localhost:8080/carrito/usuario/2/resumen
```

Crear pago:

```bash
curl -X POST http://localhost:8080/pagos \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 2,
    "metodoPago": "TARJETA"
  }'
```

Crear resena:

```bash
curl -X POST http://localhost:8080/resenas \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 2,
    "nombreJuego": "Cyberpunk 2077",
    "comentario": "Muy buen juego.",
    "puntuacion": 5,
    "fechaResena": "2026-05-19"
  }'
```

Aumentar stock:

```bash
curl -X PUT http://localhost:8080/inventario/videojuego/1/entrada \
  -H "Content-Type: application/json" \
  -d '{
    "cantidad": 5
  }'
```

## Configuracion por Variables de Entorno

Puedes cambiar configuraciones sin editar los archivos usando variables de entorno.

Ejemplos:

```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_USER=root
export DB_PASSWORD=mi_password
export EUREKA_SERVER_URL=http://localhost:8761/eureka/
export CONFIG_SERVER_URL=http://localhost:8888
```

Tambien puedes fijar puertos especificos:

```bash
export VIDEOJUEGOS_PORT=8081
export USUARIOS_PORT=8082
export AUTHENTICATION_PORT=8083
export CARRITO_PORT=8084
export PAGOS_PORT=8085
export PEDIDOS_PORT=8086
export RESENAS_PORT=8087
export INVENTARIO_PORT=8088
```

## Validaciones y Reglas

- Los correos de usuario deben ser unicos.
- Las passwords se guardan encriptadas con BCrypt.
- Los videojuegos deben tener precio mayor a 0.
- El carrito valida que el videojuego exista antes de agregarlo.
- El pago solo se puede crear si el carrito tiene total mayor a 0.
- Al crear un pago se vacia el carrito.
- Pedidos y resenas validan que el usuario exista.
- Inventario valida que el videojuego exista.
- No se puede disminuir stock por debajo de 0.

## Consideraciones Importantes

- No hay frontend incluido; el sistema se consume por API REST.
- No hay autenticacion con JWT.
- El API Gateway no aplica seguridad; solo enruta.
- El pago no descuenta inventario automaticamente.
- El pago no crea pedido automaticamente.
- Cada microservicio tiene su propia base de datos.
- La comunicacion interna entre microservicios se hace con OpenFeign y Eureka.
- Si un microservicio no aparece en Eureka, el gateway no podra enrutar hacia el.

## Problemas Comunes

### El gateway responde error al llamar una ruta

Revisar que:

- Eureka este levantado.
- Config Server este levantado.
- El microservicio correspondiente este registrado en Eureka.
- El gateway este levantado despues de Config Server.

### Un microservicio no inicia por base de datos

Revisar que:

- MySQL este activo.
- El usuario y password sean correctos.
- El puerto sea `3306`.
- El usuario tenga permisos para crear bases de datos.

### Config Server no encuentra configuracion

El Config Server busca archivos en:

```text
./config-microservicios
../config-microservicios
```

Por eso es recomendable ejecutarlo desde la carpeta `config-server`.

### Las rutas internas fallan con Feign

Revisar que el servicio destino este levantado y registrado en Eureka. Por ejemplo:

- `carrito` necesita `videojuegos`.
- `pagos` necesita `carrito`.
- `authentication` necesita `usuarios`.
- `pedidos` necesita `usuarios`.
- `resenas` necesita `usuarios`.
- `inventario` necesita `videojuegos`.

## Resumen de Dependencias entre Microservicios

| Servicio | Depende de |
| --- | --- |
| `authentication` | `usuarios` |
| `carrito` | `videojuegos` |
| `pagos` | `carrito` |
| `pedidos` | `usuarios` |
| `resenas` | `usuarios` |
| `inventario` | `videojuegos` |
| `api-gateway` | Eureka y todos los servicios registrados |

## Estado del Proyecto

El proyecto esta preparado como backend REST de microservicios para una tienda de videojuegos. Permite:

- Gestionar catalogo de videojuegos.
- Gestionar usuarios.
- Registrar e iniciar sesion.
- Gestionar carrito.
- Generar pagos.
- Gestionar pedidos.
- Gestionar resenas.
- Gestionar inventario.
- Consultar reportes basicos por fecha, precio, puntuacion y stock.

