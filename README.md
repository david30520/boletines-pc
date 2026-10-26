# Servicio REST para administración de tareas

[![CI](https://github.com/david30520/boletines-pc/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/david30520/boletines-pc/actions/workflows/ci.yml)

Proyecto Maven con Java 21, Spring Boot 3.5.16, Spring Web, Bean Validation,
Spring Data JPA/Hibernate y PostgreSQL.

## Levantar el proyecto con Docker

Requisitos: Docker en ejecución y Docker Compose (comando `docker compose`).
La primera construcción necesita conexión a Internet para descargar las imágenes
y las dependencias de Maven. La compilación con Java 21 y Maven se realiza dentro
de la imagen Docker.

Ejecuta los siguientes comandos desde la raíz del repositorio.

### Configurar la base de datos

Si todavía no tienes un fichero `.env`, créalo a partir del ejemplo:

```bash
cp .env.example .env
```

Edita `.env` antes del primer arranque:

| Variable            | Uso                                                                |
| ------------------- | ------------------------------------------------------------------ |
| `POSTGRES_DB`       | Nombre de la base de datos; el ejemplo usa `tareas`.               |
| `POSTGRES_USER`     | Usuario de PostgreSQL; el ejemplo usa `app`.                       |
| `POSTGRES_PASSWORD` | Contraseña del usuario; sustituye `cambia_esto` por tu contraseña. |

Docker Compose carga este fichero y configura tanto PostgreSQL como la conexión
de la API. `.env` está excluido de Git.

### Construir y arrancar

```bash
docker compose up -d --build
```

Este comando construye la imagen de la aplicación y levanta los dos servicios
de `docker-compose.yml` en segundo plano:

- `db`: PostgreSQL 16. En el primer arranque crea el usuario y la base de datos
  indicados en `.env`.
- `api`: aplicación Spring Boot. Arranca cuando la comprobación de disponibilidad
  de PostgreSQL es satisfactoria y se conecta a `db:5432` por la red de Compose.

La API está disponible en `http://localhost:8080/api/tasks` y su estado en
`http://localhost:8080/actuator/health`. PostgreSQL se publica en
`127.0.0.1:5432` para conexiones desde el equipo local. Los puertos `8080` y `5432`
deben estar libres antes de arrancar.

Para consultar el estado y seguir los logs:

```bash
docker compose ps
docker compose logs -f api db
```

`Ctrl+C` deja de mostrar los logs; los contenedores siguen en ejecución.
Tras modificar el código, vuelve a ejecutar `docker compose up -d --build` para
reconstruir y actualizar la aplicación.

### Persistencia y parada

PostgreSQL guarda los datos en el volumen nombrado `db-data`, montado en
`/var/lib/postgresql/data`. Hibernate crea o actualiza las tablas al arrancar con
`ddl-auto: update`. Los datos se conservan al reiniciar o recrear los contenedores.

Para detener y eliminar los contenedores conservando la base de datos:

```bash
docker compose down
```

Para volver a arrancar con los datos guardados, desde el mismo proyecto:

```bash
docker compose up -d
```

Las variables `POSTGRES_*` inicializan PostgreSQL cuando el volumen está vacío.
Cambiar esos valores en `.env` después del primer arranque no modifica el usuario,
la contraseña ni la base de datos ya existentes.

**Para empezar desde cero, el siguiente comando elimina también el volumen y
todos los datos de PostgreSQL:**

```bash
docker compose down -v
```

El siguiente `docker compose up -d` inicializará una base de datos vacía con los
valores de `.env`.

## Levantar el proyecto con Dev Container

Requisitos en el equipo: Docker en ejecución, Visual Studio Code y la
[extensión Dev Containers](https://code.visualstudio.com/docs/devcontainers/containers).
La configuración de `.devcontainer/devcontainer.json` proporciona Java 21, Maven
y Docker dentro del contenedor de desarrollo.

### Abrir el entorno

1. Abre la carpeta raíz del repositorio en VS Code.
2. Abre la paleta de comandos (`F1`) y ejecuta **Dev Containers: Reopen in Container**.
3. Espera a que termine la creación y preparación del entorno. La primera vez se
   descargan la imagen, las herramientas y las dependencias de Maven, por lo que
   necesitas conexión a Internet.
4. Revisa `.env`: la preparación lo copia de `.env.example` si todavía no existe.
   Ajusta `POSTGRES_DB`, `POSTGRES_USER` y `POSTGRES_PASSWORD` antes de arrancar,
   como se explica en [Configurar la base de datos](#configurar-la-base-de-datos).

### Arrancar la API y PostgreSQL

Abre una terminal integrada de VS Code dentro del Dev Container y ejecuta desde
la raíz del repositorio:

```bash
docker compose up -d --build
docker compose ps
```

La preparación del entorno descarga las dependencias; el comando anterior
levanta los servicios `api` y `db`. Para seguir el arranque:

```bash
docker compose logs -f api db
```

La configuración solicita el reenvío de los puertos `8080` y `5432` al equipo
local. Abre `http://localhost:8080/api/tasks` para acceder a la API y
`http://localhost:8080/actuator/health` para comprobar su estado. Si VS Code
asigna otro puerto local, consulta la dirección en la pestaña **Puertos**.

Desde la misma terminal puedes ejecutar las pruebas con las herramientas del entorno:

```bash
mvn test
```

Tras cambiar el código, ejecuta `docker compose up -d --build` para actualizar la
API. Para detener los servicios conservando los datos, ejecuta
`docker compose down` dentro del Dev Container; `docker compose up -d` vuelve a
levantarlos con el volumen existente.

Este entorno usa Docker dentro del contenedor de desarrollo, con su propio motor
y sus propios volúmenes. Ejecuta los comandos de Compose desde su terminal para
gestionar esas instancias. La persistencia de PostgreSQL sigue las reglas de
[Persistencia y parada](#persistencia-y-parada).

## Modelo y reglas

| Campo JSON    | Tipo                  | Regla                                                                                                     |
| ------------- | --------------------- | --------------------------------------------------------------------------------------------------------- |
| `id`          | Entero largo          | Lo genera la base de datos; solo aparece en las respuestas.                                               |
| `title`       | Texto                 | Obligatorio, no puede estar en blanco, máximo 120 caracteres. Se eliminan espacios exteriores al guardar. |
| `description` | Texto                 | Opcional, admite `null`, máximo 2000 caracteres.                                                          |
| `status`      | Enum                  | Obligatorio: `TODO`, `IN_PROGRESS` o `DONE`.                                                              |
| `priority`    | Enum                  | Obligatoria: `LOW`, `MEDIUM` o `HIGH`.                                                                    |
| `dueDate`     | Fecha `AAAA-MM-DD`    | Obligatoria; hoy o una fecha futura, tomando UTC como referencia.                                         |
| `completedAt` | Instante UTC ISO-8601 | Solo lectura. Momento (sin fracciones de segundo) en que la tarea pasó a `DONE`; `null` en otro estado.   |

Estas reglas se aplican tanto al crear como al actualizar. `PUT` sustituye todos
los campos editables y mantiene el identificador; no crea una tarea inexistente.
Si una tarea ya ha vencido, sigue pudiendo consultarse y borrarse. Para modificarla,
debe enviarse una fecha límite válida.

### Flujo de estados

Al crear una tarea se admite cualquier estado inicial. A partir de ahí, los cambios de
estado, tanto con `PUT` como con `PATCH /api/tasks/{id}/status`, siguen este flujo:

```text
TODO ⇄ IN_PROGRESS ⇄ DONE
```

| Desde \ Hacia | `TODO`      | `IN_PROGRESS`       | `DONE`      |
| ------------- | ----------- | ------------------- | ----------- |
| `TODO`        | Sin cambios | Permitida           | **409**     |
| `IN_PROGRESS` | Permitida   | Sin cambios         | Permitida   |
| `DONE`        | **409**     | Permitida (reabrir) | Sin cambios |

Enviar el mismo estado que ya tiene la tarea no es un error y no modifica nada.
Al pasar a `DONE` se guarda `completedAt`; al reabrir la tarea vuelve a `null`.
Una transición no válida devuelve `409 Conflict` y la tarea no se modifica.

`PATCH /api/tasks/{id}/status` solo cambia el estado: no exige reenviar el resto de
campos ni revalida la fecha límite. Así, una tarea vencida puede marcarse como `DONE`
sin cambiar su `dueDate`.

La validación temporal se declara en el DTO de entrada, no en la entidad persistida:
el paso del tiempo no convierte en inválidos los registros almacenados. El servicio
también valida las peticiones cuando se invoca directamente, fuera del controlador.

## Endpoints

| Método   | Ruta                     | Resultado                                                                                                                              |
| -------- | ------------------------ | -------------------------------------------------------------------------------------------------------------------------------------- |
| `POST`   | `/api/tasks`             | `201 Created`, tarea creada y cabecera `Location`.                                                                                     |
| `GET`    | `/api/tasks`             | `200 OK`, lista ordenada por `id` o `[]`; admite filtros `status` y `priority` combinables, y paginación opcional con `page` y `size`. |
| `GET`    | `/api/tasks/{id}`        | `200 OK` o `404 Not Found`.                                                                                                            |
| `PUT`    | `/api/tasks/{id}`        | `200 OK`, `404 Not Found` o `409 Conflict` si el cambio de estado no está permitido.                                                   |
| `PATCH`  | `/api/tasks/{id}/status` | Cuerpo `{"status": "..."}`. `200 OK`, `400 Bad Request`, `404 Not Found` o `409 Conflict`.                                             |
| `DELETE` | `/api/tasks/{id}`        | `204 No Content` o `404 Not Found`.                                                                                                    |

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
  -d '{"title":"Preparar entrega","description":"Entrega revisada","status":"IN_PROGRESS","priority":"MEDIUM","dueDate":"2099-12-31"}'

# Cambiar solo el estado (IN_PROGRESS -> DONE) y reabrir (DONE -> IN_PROGRESS)
curl -i -X PATCH http://localhost:8080/api/tasks/1/status \
  -H 'Content-Type: application/json' -d '{"status":"DONE"}'
curl -i -X PATCH http://localhost:8080/api/tasks/1/status \
  -H 'Content-Type: application/json' -d '{"status":"IN_PROGRESS"}'

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

| Parámetro | Valores admitidos                                          | Valor si se omite y se activa la paginación |
| --------- | ---------------------------------------------------------- | ------------------------------------------- |
| `page`    | Entero entre `0` y `2147483647`; la primera página es `0`. | `0`                                         |
| `size`    | Entero entre `1` y `100`, ambos incluidos.                 | `20`                                        |

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

| Cabecera        | Significado                                                  |
| --------------- | ------------------------------------------------------------ |
| `X-Page`        | Índice solicitado, empezando por `0`.                        |
| `X-Page-Size`   | Tamaño efectivo de página, aunque se devuelvan menos tareas. |
| `X-Total-Count` | Total de coincidencias antes de paginar.                     |
| `X-Total-Pages` | Total de páginas para ese conjunto filtrado y tamaño.        |

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

Un cambio de estado no permitido devuelve `409`:

```json
{
    "type": "about:blank",
    "title": "Transición no válida",
    "status": 409,
    "detail": "No se puede pasar de TODO a DONE",
    "instance": "/api/tasks/1/status"
}
```

El JSON mal formado, las fechas inválidas, los enums desconocidos o numéricos y los
identificadores no numéricos también devuelven `400`. Los fallos inesperados se
registran en el servidor y devuelven `500` con un mensaje genérico, sin trazas en la respuesta.

## Tests unitarios

Puedes ejecutar los tests desde la terminal del Dev Container. Para ejecutarlos
directamente en tu equipo se necesitan JDK 21 y Maven 3.6.3 o posterior:

```bash
mvn test
```

La construcción de la imagen Docker omite los tests (`-DskipTests`).

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
las nueve combinaciones del flujo de estados; transiciones permitidas y rechazadas
con `PUT` y `PATCH` sin alterar la entidad; idempotencia del mismo estado; gestión de
`completedAt`; cierre de tareas vencidas; y traducción de errores a respuestas HTTP.

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
├── domain/         # Flujo de estados de TaskStatus
├── exception/      # Tests unitarios del manejo de errores
└── service/        # Tests unitarios de reglas y operaciones
```

La clase `Task` representa la persistencia; los DTOs definen el contrato HTTP.
Las operaciones de escritura se ejecutan en transacciones y las consultas usan
transacciones de solo lectura. Hibernate implementa JPA y gestiona las tablas en PostgreSQL.

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
