# Servicio REST para administración de tareas

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
| `GET` | `/api/tasks` | `200 OK`, lista ordenada por `id` o `[]`; admite filtros `status` y `priority` combinables, y paginación opcional con `page` y `size`. |
| `GET` | `/api/tasks/{id}` | `200 OK` o `404 Not Found`. |
| `PUT` | `/api/tasks/{id}` | `200 OK` o `404 Not Found`. |
| `DELETE` | `/api/tasks/{id}` | `204 No Content` o `404 Not Found`. |


## Ejemplo de CRUD

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

# Filtro de prioridad
curl -i 'http://localhost:8080/api/tasks?priority=HIGH'
curl -i 'http://localhost:8080/api/tasks?status=TODO&priority=HIGH'


# Actualizar
curl -i -X PUT http://localhost:8080/api/tasks/1 \
  -H 'Content-Type: application/json' \
  -d '{"title":"Preparar entrega","description":"Entrega revisada","status":"DONE","priority":"MEDIUM","dueDate":"2099-12-31"}'

# Borrar y comprobar el 404
curl -i -X DELETE http://localhost:8080/api/tasks/1
curl -i http://localhost:8080/api/tasks/1
```

## Ejemplos de uso del Filtro de Tareas por Prioridad

### 1. Filtrar tareas únicamente por prioridad Alta
```bash
curl -X GET "http://localhost:8080/api/tasks?priority=HIGH"
```

### 2. Filtrar tareas combinando Estado y Prioridad
```bash
curl -X GET "http://localhost:8080/api/tasks?status=TODO&priority=LOW"
```

### 3. Respuesta Errónea (400 Bad Request) por prioridad inválida
```bash
curl -X GET "http://localhost:8080/api/tasks?priority=high"

Nota: Los valores del parámetro obligatoriamente deben estar en mayúsculas (LOW, MEDIUM, HIGH).
```

### 4. Respuesta errónea por prioridad inexistente.
```bash
curl -i 'http://localhost:8080/api/tasks?priority=URGENT'
```
### 5. Respuesta errónea por prioridad vacía.
```bash
curl -i 'http://localhost:8080/api/tasks?priority='
```

## Paginación opcional del listado

`GET /api/tasks` devuelve un array JSON de tareas ordenadas por `id` ascendente,
tanto con paginación como sin ella. Si no se envían `page` ni `size`, devuelve
todas las coincidencias, conservando el comportamiento anterior, también cuando
se filtra por `status`, por `priority` o por ambos.

La presencia de cualquiera de los dos parámetros activa la paginación:

| Parámetro | Valores admitidos | Valor si se omite y se activa la paginación |
| --- | --- | --- |
| `page` | Entero entre `0` y `2147483647`; la primera página es `0`. | `0` |
| `size` | Entero entre `1` y `100`, ambos incluidos. | `20` |

Además, el desplazamiento `page * size` no puede superar `2147483647`
(`Integer.MAX_VALUE`), el límite portable de JPA. Se calcula con `long`, después
de aplicar los valores predeterminados, para evitar desbordamientos. Por ejemplo,
`page=2147483647&size=1` cumple el límite, pero `page=1073741824&size=2`
devuelve `400 Bad Request`.

Los filtros se aplican antes de paginar y se combinan con AND. El total cuenta
solo las tareas que cumplen los filtros. La paginación y el recuento se realizan
en la base de datos mediante Spring Data; no se carga la lista completa para
recortarla en memoria. Se mantiene el orden por `id` para recorrer páginas
consecutivas sin solapamientos mientras no cambien los datos.

```bash
# Listado completo, sin paginación
curl -i 'http://localhost:8080/api/tasks'
curl -i 'http://localhost:8080/api/tasks?priority=HIGH'

# Primera página de 20 tareas
curl -i 'http://localhost:8080/api/tasks?page=0&size=20'

# Segunda página con tamaño predeterminado 20
curl -i 'http://localhost:8080/api/tasks?page=1'

# Primera página con tamaño 10
curl -i 'http://localhost:8080/api/tasks?size=10'

# Paginación con cada filtro y con ambos
curl -i 'http://localhost:8080/api/tasks?status=IN_PROGRESS&page=0&size=10'
curl -i 'http://localhost:8080/api/tasks?priority=HIGH&page=0&size=10'
curl -i 'http://localhost:8080/api/tasks?status=TODO&priority=HIGH&page=1&size=10'
```

Las respuestas paginadas añaden estas cabeceras; el cuerpo sigue siendo el array
habitual de `TaskResponse`, sin un objeto envolvente:

| Cabecera | Significado |
| --- | --- |
| `X-Page` | Índice solicitado, empezando por `0`. |
| `X-Page-Size` | Tamaño efectivo de página, aunque se devuelvan menos tareas. |
| `X-Total-Count` | Total de coincidencias antes de paginar. |
| `X-Total-Pages` | Total de páginas para ese conjunto filtrado y tamaño. |

Con 23 coincidencias, `page=1&size=10` devuelve las posiciones 11 a 20 y las
cabeceras `X-Page: 1`, `X-Page-Size: 10`, `X-Total-Count: 23` y
`X-Total-Pages: 3`. La última página (`page=2`) contiene 3 tareas. Una página fuera
de rango, con un desplazamiento válido, devuelve `200 OK` y `[]`, conservando el
índice solicitado y los totales reales. Si no hay coincidencias, ambos totales
son `0`. Las peticiones sin paginación no incluyen estas cabeceras.

Se rechazan con `400 Bad Request` los valores negativos, vacíos, no enteros,
desbordados o fuera de los límites anteriores. Un parámetro vacío no se considera
omitido y no recibe el valor predeterminado. Por ejemplo, `page=-1`, `size=0`,
`size=101`, `page=`, `size=`, `page=abc`, `size=1.5` y `page=2147483648` son
inválidos. El cuerpo `ProblemDetail` identifica el parámetro y su regla:

```json
{
  "type": "about:blank",
  "title": "Paginación no válida",
  "status": 400,
  "detail": "El parámetro 'size' debe ser un número entero entre 1 y 100",
  "instance": "/api/tasks"
}
```

Las validaciones existentes de filtros siguen aplicándose con paginación:
`priority=`, `priority=high` y `priority=URGENT` devuelven `400` con el error de
prioridad habitual. Un estado desconocido también devuelve `400`.

Las combinaciones cuyo desplazamiento supera `Integer.MAX_VALUE` se rechazan
antes de consultar el repositorio, aunque no existan tareas. La respuesta utiliza
el mismo `ProblemDetail` de paginación, con el detalle
`La combinación de 'page' y 'size' debe cumplir page * size <= 2147483647`.
La paginación utiliza exclusivamente `Pageable`, `PageRequest`, `Page` y los
métodos paginados de Spring Data JPA, sin consultas SQL nativas ni rutas
específicas de un motor de base de datos.

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
inexistentes; listado y filtros por estado y prioridad; paginación con las cuatro
combinaciones de filtros, orden y metadatos; valores predeterminados y límites;
primera página, intermedias, última incompleta, páginas vacías y fuera de rango;
y traducción de errores a respuestas HTTP.

**Solo hay tests unitarios**: no se utilizan `@SpringBootTest`, `@DataJpaTest`,
`@WebMvcTest`, contextos de Spring ni conexiones a bases de datos. Las pruebas del
manejador de errores invocan sus métodos directamente con objetos de petición simulados.
El controlador se prueba con MockMvc standalone y un servicio simulado.
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
├── controller/     # Contrato HTTP con MockMvc standalone
├── exception/      # Tests unitarios del manejo de errores
└── service/        # Tests unitarios de reglas y operaciones
```

La clase `Task` representa la persistencia; los DTOs definen el contrato HTTP.
Las operaciones de escritura se ejecutan en transacciones y las consultas usan
transacciones de solo lectura. Hibernate implementa JPA y crea el esquema en H2.

## Configuración del hook de Git

El repositorio incluye un hook de pre-commit que ejecuta Spotless antes
de realizar cada commit.

Después de clonar el repositorio debe configurarse Git para utilizar los
hooks versionados:

```bash
git config core.hooksPath .githooks
```
Referencias oficiales: [compatibilidad de Spring Boot 3.5 con Java y Maven](https://docs.spring.io/spring-boot/3.5/system-requirements.html),
[personalización de Bean Validation](https://docs.spring.io/spring-boot/3.5/reference/io/validation.html)
y [empaquetado de JAR ejecutables](https://docs.spring.io/spring-boot/3.5/maven-plugin/packaging.html).
