# Boletín 1

## Resumen de commits

| # | Mensaje del commit | Qué se hizo | Autor | Enlace |
|---|--------------------|-------------|-------|--------|
| 1 | `chore: proyecto Maven inicial de la API de tareas` | Se añadió API inicial y ficheros configuración `.gitignore` y `.gitattributes` | Óscar Abellán Bleda | [35558d8] |
| 2 | `chore(editor): añade .editorconfig` | Se añadió el fichero `.editorconfig` | Óscar Abellán Bleda | [e769f56] |
| 3 | `feat(tasks): añade endpoint de estadisticas de tareas` | Se añadió un nuevo endpoint | Óscar Abellán Bleda | [2b8be91] |
| 4 | `chore(build): añade Spotless para formateo automático`        | Se añadió plugin Spotless al `pom.xml`        | Óscar Abellán Bleda   | [8ce1572] |
| 5 | `chore(hooks): añade hook de pre-commit con Spotless`        | Se añadió el hook de pre-commit en el repositorio       | Óscar Abellán Bleda   | [198bea4] |
| 6 | `docs(readme): documenta la configuración del hook`        | Se documentó configuración del hook en `README.md`       | Óscar Abellán Bleda   | [7e48dcb] |
| 7 | `docs(task): documenta el método changeStatus`        | Se documentó un método de la clase`Task.java`       | Óscar Abellán Bleda   | [91a97b6] |
| 8 | `feat(task): permite títulos de hasta 150 caracteres`        | Se modificó longitud máxima de un título en la clase `Task.java`     | Óscar Abellán Bleda   | [7690480] |
| 9 | `feat(task): reduce el título máximo a 80 caracteres`        | Se modificó longitud máxima de un título en la clase `Task.java`       | Óscar Abellán Bleda   | [cc759bc] |
| 10 | `fix(task): resuelve conflicto en el límite de longitud del título`        | Se resolvió conflicto en la clase `Task.java`     | Óscar Abellán Bleda   | [b33e720] |

## Detalle por commit

### 1. `chore: proyecto Maven inicial de la API de tareas` 
- **Qué se hizo:** Añadir los ficheros .gitignore y .gitattributes junto con el código inicial de la API REST de la parte B
- **Autor:** Óscar Abellán Bleda
- **Relación con el boletín:** apartado D, con la inicialización del repositorio Git y primer commit
- **Uso de IA:** Se ha utilizado Claude Code con el siguiente prompt: Genera un proyecto Maven con Spring Boot 3 y Java 21: una API REST de gestión de tareas (entidad Task con id, título, descripción, estado, prioridad y fecha límite). Usa Spring Data JPA con Hibernate para la persistencia y H2 en memoria como base de datos. Incluye endpoints CRUD, capa de servicio, repositorio JPA, validaciones con Bean Validation, manejo de errores con @RestControllerAdvice y tests unitarios con JUnit 5.

Requisitos mínimos de la aplicación para considerarse “no trivial”:

Al menos una entidad de dominio con varios campos y validaciones.
Persistencia mediante Spring Data JPA/Hibernate.
Operaciones CRUD completas (crear, leer, actualizar, borrar).
Separación en capas (controlador / servicio / repositorio).
Manejo de errores centralizado: un recurso inexistente devuelve 404, no una traza de excepción.
Al menos 5 tests que comprueben reglas de negocio reales (p. ej. “no se puede crear una tarea con fecha límite pasada”), no simples assertNotNull.
No debe incluir tests de integración que levanten el contexto de la aplicación y/o una base de datos. Solo debe incluir tests unitarios.
El proyecto debe generar mediante mvn package un JAR ejecutable (fat JAR) que incluya todas las dependencias y pueda iniciarse mediante java -jar target/<nombre>.jar.

### 2. `chore(editor): añade .editorconfig`
- **Qué se hizo:** Añadir fichero .editorconfig
- **Autor:** Óscar Abellán Bleda
- **Relación con el boletín:** apartado D, commit separado para añadir fichero .editorconfig
- **Uso de IA:** no se ha usado

### 3. `feat(tasks): añade endpoint de estadisticas de tareas`
- **Qué se hizo:** Añadir la funcionalidad para mostrar mediante un endpoint estadisticas sobre las tareas
- **Autor:** Óscar Abellán Bleda
- **Relación con el boletín:** apartado D, cambio no trivial en una nueva rama y fusión a main
- **Uso de IA:** Se ha utilizado Claude Code con el siguiente prompt: Añademe una nueva funcionalidad como por ejemplo un nuevo endpoint pero en una rama distinta a la rama main con el nombre feat/descripcion-feat

### 4. `chore(build): añade Spotless para formateo automático`
- **Qué se hizo:** Añadir el plugin Spotless en el pom.xml para el formateo automático en cada commit. 
- **Autor:** Óscar Abellán Bleda
- **Relación con el boletín:** apartado E, añadir el plugin y probarlo
- **Uso de IA:** no se ha usado

### 5. `chore(hooks): añade hook de pre-commit con Spotless`
- **Qué se hizo:** Añadir un hook (.githooks/pre-commit) para evitar hacer commit de un código sin formateo
- **Autor:** Óscar Abellán Bleda
- **Relación con el boletín:** apartado E, añadir el hook de pre-commit
- **Uso de IA:** no se ha usado

### 6. `docs(readme): documenta la configuración del hook`
- **Qué se hizo:** Añadir documentación del hook en fichero README.md
- **Autor:** Óscar Abellán Bleda
- **Relación con el boletín:** apartado E, documentar el uso del hook después de clonar el repositorio
- **Uso de IA:** no se ha usado

### 7. `docs(task): documenta el método changeStatus`
- **Qué se hizo:** Añadir comentario con mal identación al fichero Task.java para comprobar el hook de pre-commit
- **Autor:** Óscar Abellán Bleda
- **Relación con el boletín:** apartado E, comprobar funcionamiento de hook
- **Uso de IA:** no se ha usado

### 8. `feat(task): permite títulos de hasta 150 caracteres`
- **Qué se hizo:** Modificar máxima longitud que puede llegar a tener un título de una tarea
- **Autor:** Óscar Abellán Bleda
- **Relación con el boletín:** apartado F, provocar un conflicto en el fichero Task.java
- **Uso de IA:** no se ha usado

### 9. `feat(task): reduce el título máximo a 80 caracteres`
- **Qué se hizo:** Modificar máxima longitud que puede llegar a tener un título de una tarea
- **Autor:** Óscar Abellán Bleda
- **Relación con el boletín:** apartado F, provocar un conflicto en el fichero Task.java
- **Uso de IA:** no se ha usado

### 10. `fix(task): resuelve conflicto en el límite de longitud del título`
- **Qué se hizo:** Resolver el conflicto del fichero Task.java 
- **Autor:** Óscar Abellán Bleda
- **Relación con el boletín:** apartado F, resolver un conflicto
- **Uso de IA:** no se ha usado



## Descripción narrativa de lo hecho en la sesión y relación con los commits

### Parte A

Se ha comprobado la instalación de las herramientas necesarias para el desarrollo del boletín 1 (Git, JDK 21 y Maven). Además, se ha configurado la identidad en git (nombre y email) que se usará para firmar todos los commits; se ha configurado una política de pull donde solamente se acepta la operación fast-forward, evitando merges automáticos; y se ha configurado el nombre que tomará la primera rama (principal) cuando un repositorio es creado (git init).

### Parte B

Se le ha pedido a la IA (Claude Code) que generase una API REST para la gestión de tareas con Spring Boot y Maven. Se utilizó el prompt de ejemplo del boletín 1 para generar dicha API. En el modelo del dominio se dispone de la entidad `task` con sus diferentes campos.


### Parte C

Una vez generados todos los ficheros de la API REST se procedió al empaquetado de la aplicación mediante el comando `mvn clean package`. Este comando ejecuta previamente al empaquetado las fases de compilación y validación (test unitarios). El resultado del comando anterior es un JAR ejecutable que incluye todas las dependencias.

La API puede levantarse con el comando `java -jar target/task-manager.jar`, que lanza la aplicación y se queda a la escucha de las peticiones a los endpoints disponibles. La URL base se corresponde con `http://localhost:8080/api/tasks`.

Finalmente, se han probado los diferentes endpoints de la API utilizando la herramienta curl. Como ejemplo, para crear una tarea se emplea:

```bash
curl -i -X POST $URL -H 'Content-Type: application/json' \
  -d '{"title":"Preparar informe","description":"Resultados Q3","priority":"HIGH","dueDate":"2030-01-15"}'
```
donde `$URL` es la URL base.

### Parte D

Se procede a inicializar el repositorio git con el comando `git init`. El API REST devuelto por la IA en la parte B junto con los archivos de configuración base `.gitignore` y `.gitattributes` se añadieron mediante el commit 1.

Mediante el commit 2 se añadió el fichero `.editorconfig` para que todos los editores de código empleen la misma identación.

Posteriormente se añadió mediante el commit 3 una nueva funcionalidad que consiste en un nuevo endpoint donde se muestran estadisticas sobre las tareas. Esta nueva funcionalidad se añadió mediante una nueva rama llamada `feat/estadisticas-tareas`. 

Finalmente la nueva rama se fusionó en la rama main. Se observa que git realiza un **Fast-forward** ya que la rama main no tenia nuevos commits.

```bash
git merge feat/estadisticas-tareas
Actualizando e769f56..2b8be91
Fast-forward
README.md                                                          | 22 +++++++++++++++
src/main/java/com/example/taskmanager/dto/TaskStatsResponse.java   | 23 ++++++++++++++++
src/main/java/com/example/taskmanager/service/TaskService.java     | 38 ++++++++++++++++++++++++++
src/main/java/com/example/taskmanager/web/TaskController.java      |  6 +++++
src/test/java/com/example/taskmanager/service/TaskServiceTest.java | 75 ++++++++++++++++++++++++++++++++++++++++++++++++++-
5 files changed, 163 insertions(+), 1 deletion(-)
```

Tras ejecutar el comando `git log --oneline --graph --all` se observa como no se ha creado ningún commit nuevo (Fast-forward):

```bash
* 2b8be91 (HEAD -> main, feat/estadisticas-tareas) feat(tasks): añade endpoint de estadisticas de tareas
* e769f56 chore(editor): añade .editorconfig
* 35558d8 chore: proyecto Maven inicial de la API de tareas
```


### Parte E

En este apartado se introdujo el uso de Spotless para que el proyecto tenga un único estilo. Para ello, se creó un hook de pre-commit que impide commitear código sin formatear. Para probar que este hook funciona correctamente, se añadió un comentario mal identado en un método de la clase `Task.java`. Al ejecutar el comando `mvn spotless:check` vemos que aparece un error porque no está bien formateado.

```bash
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.510 s
[INFO] Finished at: 2026-09-28T21:35:08+02:00
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal com.diffplug.spotless:spotless-maven-plugin:2.43.0:check (default-cli) on project task-manager: The following files had format violations:
[ERROR]     src/main/java/com/example/taskmanager/domain/Task.java
[ERROR]         @@ -85,7 +85,8 @@
[ERROR]          ····this.dueDate·=·dueDate;
[ERROR]          ····this.updatedAt·=·now;
[ERROR]          ··}
[ERROR]         -············//·Cambia·el·estado·de·la·tarea·y·actualiza·la·fecha·de·modificación
[ERROR]         +
[ERROR]         +··//·Cambia·el·estado·de·la·tarea·y·actualiza·la·fecha·de·modificación
[ERROR]          ··public·void·changeStatus(TaskStatus·status,·LocalDateTime·now)·{
[ERROR]          ····this.status·=·status;
[ERROR]          ····this.updatedAt·=·now;
[ERROR] Run 'mvn spotless:apply' to fix these violations.
```
No obstante, al realizar el commit 7 asociado a la introducción del comentario mal identado, vemos como el hook se ejecuta automáticamente y el fichero queda bien identado. Ahora, el comando `mvn spotless:check` no falla

### Parte F

En este apartado final se ha provocado y resuelto un conflicto. Para ello se crearon dos ramas: `feat/titulo-mas-largo` y `feat/titulo-mas-corto`. Cada rama tiene asociado un commit (8 y 9) que modifica la misma línea del fichero `Task.java`. Concretamente se modifica la longitud máxima que puede alcanzar el título de cierta tarea.

Posteriormente, se fusionó la rama `feat/titulo-mas-largo` con main sin problemas. Sin embargo, al fusionar la segunda rama, git nos indica que se generó un conflicto que se tiene que resolver manualmente.

```text
<<<<<<< HEAD
  public static final int TITLE_MAX_LENGTH = 150;
=======
  public static final int TITLE_MAX_LENGTH = 80;
>>>>>>> feat/titulo-mas-corto
```

El conflicto se ha resuelto editando el fichero `Task.java` dejando la versión de la rama `feat/titulo-mas-corto`. El historial de commits quedaría de la siguiente forma:

```bash
git log --oneline --graph --all
*   b33e720 (HEAD -> main) fix(task): resuelve conflicto en el límite de longitud del título
|\  
| * cc759bc (feat/titulo-mas-corto) feat(task): reduce el título máximo a 80 caracteres
* | 7690480 (feat/titulo-mas-largo) feat(task): permite títulos de hasta 150 caracteres
|/  
* 91a97b6 docs(task): documenta el método changeStatus
* 7e48dcb docs(readme): documenta la configuración del hook
* 198bea4 chore(hooks): añade hook de pre-commit con Spotless
* 8ce1572 chore(build): añade Spotless para formateo automático
* 2b8be91 (feat/estadisticas-tareas) feat(tasks): añade endpoint de estadisticas de tareas
* e769f56 chore(editor): añade .editorconfig
* 35558d8 chore: proyecto Maven inicial de la API de tareas
```

La resolución del conflicto anterior da lugar a un merge commit con dos padres, donde ambas ramas convergen a main.
