# API Reactiva de Gestión de Usuarios

## Descripción

Esta aplicación es una implementación de un servicio de usuarios reactivo, desarrollada como parte de un reto de onboarding. La aplicación utiliza una arquitectura limpia (Clean Architecture) y sigue los principios del Domain-Driven Design (DDD).

Las principales funcionalidades son:
- Creación de usuarios a partir de la API pública de [reqres.in](https://reqres.in/).
- Almacenamiento de usuarios en una base de datos PostgreSQL de forma reactiva.
- Consulta de usuarios por ID, con un mensaje de error si el usuario no existe.
- Consulta de todos los usuarios existentes.
- Consulta de usuarios por nombre.
- Implementación de caché con Redis para optimizar las consultas.
- Envío de eventos a una cola de AWS SQS de forma asíncrona después de la creación de un usuario.
- Un entry point que consume los eventos de la cola, transforma los datos del usuario a mayúsculas y los almacena en DynamoDB.

## Prerrequisitos

Asegúrate de tener instalado el siguiente software en tu máquina local:

- [Java](https://www.java.com/en/download/)
- [Gradle](https://gradle.org/install/)
- [Docker](https://www.docker.com/products/docker-desktop/)
- [AWS CLI](https://aws.amazon.com/cli/) (para interactuar con Localstack)

## Configuración y Ejecución

Sigue estos pasos para configurar y ejecutar el proyecto en tu entorno local:

### 1. Levantar el entorno de Docker

El proyecto utiliza `docker-compose` para gestionar los servicios de `PostgreSQL`, `Redis` y `Localstack`. Para iniciar estos servicios, ejecuta el siguiente comando en la raíz del proyecto:

```bash
docker-compose up -d
```

### 2. Configurar Localstack (AWS)

Una vez que los contenedores estén en funcionamiento, necesitas crear la cola de SQS y la tabla de DynamoDB en Localstack.

#### Crear la cola de SQS

Ejecuta el siguiente comando para crear la cola `user_created_queue`:

```bash
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name user_created_queue```

#### Crear la tabla de DynamoDB

Ejecuta el siguiente comando para crear la tabla `users`:

```bash
aws --endpoint-url=http://localhost:4566 dynamodb create-table \
    --table-name UsersNoSQL \
    --attribute-definitions AttributeName=id,AttributeType=S \
    --key-schema AttributeName=id,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5
```

### 3. Ejecutar la aplicación

Finalmente, puedes ejecutar la aplicación Spring Boot utilizando el wrapper de Gradle:

```bash
./gradlew bootRun
```

La aplicación estará disponible en `http://localhost:8080`.

## Endpoints de la API

La aplicación expone los siguientes endpoints REST:

### Crear un usuario

- **Método**: `POST`
- **Endpoint**: `/api/v1/users/{id}`
- **Descripción**: Crea un nuevo usuario obteniendo los datos de la API de `reqres.in` a partir de un `id`. Si el usuario ya existe en la base de datos, no se actualiza y se retorna el usuario existente.
- **Ejemplo de uso**:
  ```bash
  curl -X POST http://localhost:8080/api/v1/users/1
  ```

### Consultar un usuario por ID

- **Método**: `GET`
- **Endpoint**: `/api/v1/users/{id}`
- **Descripción**: Consulta un usuario por su `id`. Si el usuario no existe, retorna un mensaje indicando que no se encontró.
- **Ejemplo de uso**:
  ```bash
  curl http://localhost:8080/api/v1/users/1
  ```

### Consultar todos los usuarios

- **Método**: `GET`
- **Endpoint**: `/api/v1/users`
- **Descripción**: Consulta todos los usuarios almacenados en la base de datos.
- **Ejemplo de uso**:
  ```bash
  curl http://localhost:8080/api/v1/users
  ```

### Consultar usuarios por nombre

- **Método**: `GET`
- **Endpoint**: `/api/users/name`
- **Parámetros de consulta**: `name`
- **Descripción**: Consulta los usuarios cuyo nombre (`first_name`) coincida con el parámetro `name`.
- **Ejemplo de uso**:
  ```bash
  curl "http://localhost:8080/api/v1/users/name?name=George"
  ```