# API REST de tareas

Proyecto Maven con Java 21, Spring Boot 3.5.16, Spring Web, Bean Validation,
Spring Data JPA/Hibernate y H2 en memoria.

## Compilar y ejecutar

Requisitos: JDK 21 y Maven 3.6.3 o posterior. La primera compilación necesita
acceso a Maven Central para descargar dependencias.

```bash
mvn clean package
java -jar target/tasks-api-0.0.1-SNAPSHOT.jar
```

La API escucha en `http://localhost:8080/api/tasks`. Para cambiar el puerto:

```bash
java -jar target/tasks-api-0.0.1-SNAPSHOT.jar --server.port=8081
```

El plugin de Spring Boot genera un **JAR ejecutable con todas las dependencias**.
H2 crea las tablas al arrancar; los datos se pierden al detener el proceso.

## Modelo y reglas

| Campo JSON | Tipo | Regla |
| --- | --- | --- |
| `id` | Entero largo | Lo genera la base de datos; solo aparece en las respuestas. |
| `title` | Texto | Obligatorio, no puede estar en blanco, máximo 120 caracteres. Se eliminan espacios exteriores al guardar. |
| `description` | Texto | Opcional, admite `null`, máximo 2000 caracteres. |
| `status` | Enum | Obligatorio: `TODO`, `IN_PROGRESS` o `DONE`. |
| `priority` | Enum | Obligatoria: `LOW`, `MEDIUM` o `HIGH`. |
| `dueDate` | Fecha `AAAA-MM-DD` | Obligatoria; hoy o una fecha futura, tomando UTC como referencia. |

Estas reglas se aplican tanto al crear como al actualizar. `PUT` sustituye todos
los campos editables y mantiene el identificador; no crea una tarea inexistente.
Si una tarea ya ha vencido, sigue pudiendo consultarse y borrarse. Para modificarla,
debe enviarse una fecha límite válida. No hay restricciones adicionales de transición
entre estados.

La validación temporal se declara en el DTO de entrada, no en la entidad persistida:
el paso del tiempo no convierte en inválidos los registros almacenados. El servicio
también valida las peticiones cuando se invoca directamente, fuera del controlador.

## Endpoints

| Método | Ruta | Resultado |
| --- | --- | --- |
| `POST` | `/api/tasks` | `201 Created`, tarea creada y cabecera `Location`. |
| `GET` | `/api/tasks` | `200 OK`, lista ordenada por `id` o `[]`; admite el filtro opcional `status`. |
| `GET` | `/api/tasks/{id}` | `200 OK` o `404 Not Found`. |
| `PUT` | `/api/tasks/{id}` | `200 OK` o `404 Not Found`. |
| `DELETE` | `/api/tasks/{id}` | `204 No Content` o `404 Not Found`. |

### Ejemplo de CRUD

La fecha de ejemplo debe seguir siendo hoy o futura al ejecutar la petición.
En una base de datos recién iniciada, la primera tarea tendrá el identificador `1`;
en otros casos, usa el `id` o la cabecera `Location` de la respuesta.

```bash
# Crear
curl -i -X POST http://localhost:8080/api/tasks \
  -H 'Content-Type: application/json' \
  -d '{"title":"Preparar entrega","description":"Revisar el boletín","status":"TODO","priority":"HIGH","dueDate":"2099-12-31"}'

# Listar y consultar
curl -i http://localhost:8080/api/tasks
curl -i 'http://localhost:8080/api/tasks?status=IN_PROGRESS'
curl -i http://localhost:8080/api/tasks/1

# Actualizar
curl -i -X PUT http://localhost:8080/api/tasks/1 \
  -H 'Content-Type: application/json' \
  -d '{"title":"Preparar entrega","description":"Entrega revisada","status":"DONE","priority":"MEDIUM","dueDate":"2099-12-31"}'

# Borrar y comprobar el 404
curl -i -X DELETE http://localhost:8080/api/tasks/1
curl -i http://localhost:8080/api/tasks/1
```

### Errores

`@RestControllerAdvice` devuelve cuerpos `ProblemDetail` (`application/problem+json`).
Un recurso inexistente devuelve `404`:

```json
{
  "type": "about:blank",
  "title": "Tarea no encontrada",
  "status": 404,
  "detail": "No existe la tarea con id 99",
  "instance": "/api/tasks/99"
}
```

Los datos inválidos devuelven `400` y un mapa `errors` por campo. Por ejemplo,
enviar `"dueDate":"2000-01-01"` genera:

```json
{
  "type": "about:blank",
  "title": "Datos no válidos",
  "status": 400,
  "detail": "Uno o varios campos no cumplen las restricciones",
  "instance": "/api/tasks",
  "errors": {
    "dueDate": "La fecha límite no puede estar en el pasado"
  }
}
```

El JSON mal formado, las fechas inválidas, los enums desconocidos o numéricos y los
identificadores no numéricos también devuelven `400`. Los fallos inesperados se
registran en el servidor y devuelven `500` con un mensaje genérico, sin trazas en la respuesta.

## Tests unitarios

```bash
mvn test
```

Los tests usan JUnit 5, Mockito y Bean Validation. El repositorio se simula con
Mockito y el reloj de validación está fijado al 21 de septiembre de 2026, por lo que
las pruebas no dependen de la fecha de ejecución. Se usa el generador de mocks por
subclases.

Se comprueban las fechas pasadas y el límite de hoy; títulos ausentes, vacíos o
demasiado largos; descripciones demasiado largas; campos obligatorios; límites
máximos permitidos; normalización del título; actualización sin cambiar el ID;
rechazo de cambios inválidos sin alterar la entidad; consulta y borrado de recursos
inexistentes; listado y filtrado por estado; y traducción de errores a respuestas HTTP.

**Solo hay tests unitarios**: no se utilizan `@SpringBootTest`, `@DataJpaTest`,
`@WebMvcTest`, contextos de Spring ni conexiones a bases de datos. Las pruebas del
manejador de errores invocan sus métodos directamente con objetos de petición simulados.
`mvn package` ejecuta estos mismos tests antes de generar el JAR.

## Estructura

```text
src/main/java/com/example/tasks/
├── TasksApplication.java
├── config/         # Reloj UTC para Bean Validation
├── controller/     # Endpoints REST
├── domain/         # Entidad Task y enums
├── dto/            # Petición validada y respuesta
├── exception/      # Excepción de dominio y RestControllerAdvice
├── repository/     # JpaRepository<Task, Long>
└── service/        # Operaciones y límites transaccionales
src/main/resources/application.yml
src/test/java/com/example/tasks/
├── exception/      # Tests unitarios del manejo de errores
└── service/        # Tests unitarios de reglas y operaciones
```

La clase `Task` representa la persistencia; los DTOs definen el contrato HTTP.
Las operaciones de escritura se ejecutan en transacciones y las consultas usan
transacciones de solo lectura. Hibernate implementa JPA y crea el esquema en H2.

Referencias oficiales: [compatibilidad de Spring Boot 3.5 con Java y Maven](https://docs.spring.io/spring-boot/3.5/system-requirements.html),
[personalización de Bean Validation](https://docs.spring.io/spring-boot/3.5/reference/io/validation.html)
y [empaquetado de JAR ejecutables](https://docs.spring.io/spring-boot/3.5/maven-plugin/packaging.html).
