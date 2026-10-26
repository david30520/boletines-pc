# Boletín 1

## Resumen de commits

| # | Mensaje del commit | Qué se hizo | Autor | Enlace |
|---|--------------------|-------------|-------|--------|
| 1 | `chore: proyecto Maven inicial de la API de tareas`        | Proyecto Spring Boot inicial, `.gitignore` y `.gitattributes`         | David Abellán Bleda   | [f5a25b0]() |
| 2 | `chore(editor): añade .editorconfig`        | Añadir archivo .`editorconfig`         | David Abellán Bleda   | [5ee17c8]() |
| 3 | `feat(tasks): añade filtro de tareas por estado`        | Añadir un endpoint para consultar las tareas según su estado         | David Abellán Bleda   | [eb8344d]() |
| 4 | `chore(build): añade Spotless para formateo automático`        | Añadir Spotless al pom.xml         | David Abellán Bleda   | [8c823da]() |
| 5 | `chore(hooks): añade hook de pre-commit con Spotless`        | Añadir el hook de pre-commit        | David Abellán Bleda   | [a8d5ba4]() |
| 6 | `docs(readme): documenta configuración del hook`        | Añadir la documentación del hook pre-commit en el `README.md`        | David Abellán Bleda   | [65480b5]() |
| 7 | `docs(api): documenta el controlador de tareas`        | Añadir documentación Javadoc al controlador de tareas        | David Abellán Bleda   | [0477264]() |
| 8 | `docs(readme): actualiza nombre de la API`        | Cambiar el nombre del proyecto en `README.md`        | David Abellán Bleda   | [9491b93]() |
| 9 | `docs(readme): modifica título del proyecto`        | Cambiar el nombre del proyecto en `README.md`        | David Abellán Bleda   | [c52c23c]() |
| 10 | `docs(readme): resuelve conflicto de fusión en README`        | Resolver conflicto de fusión en `README.md`        | David Abellán Bleda   | [79a8fd3]() |

## Detalle por commit
### 1. `chore: proyecto Maven inicial de la API de tareas`
- **Qué se hizo:** Añadir archivos .gitignore y .gitattributes junto con el código inicial de la API REST creada en la parte B. 
- **Autor:** David Abellán Bleda
- **Relación con el boletín:** apartado D inicialización del repositorio git junto con el primer commit
- **Uso de IA:** Se ha usado codex GPT6-Astra Extra-High para la creación de la API REST de tareas usando el prompt de ejemplo, es decir: 
    > Genera un proyecto Maven con Spring Boot 3 y Java 21: una API REST de gestión de tareas (entidad `Task` con id, título, descripción, estado, prioridad y fecha límite). Usa Spring Data JPA con Hibernate para la persistencia y H2 en memoria como base de datos. Incluye endpoints CRUD, capa de servicio, repositorio JPA, validaciones con Bean Validation, manejo de errores con `@RestControllerAdvice` y tests unitarios con JUnit 5.
    >
    > **Requisitos mínimos** de la aplicación para considerarse "no trivial":
    >
    > * Al menos una entidad de dominio con varios campos y validaciones.
    > * Persistencia mediante Spring Data JPA/Hibernate.
    > * Operaciones CRUD completas (crear, leer, actualizar, borrar).
    > * Separación en capas (controlador / servicio / repositorio).
    > * Manejo de errores centralizado: un recurso inexistente devuelve 404, no una traza de excepción.
    > * Al menos 5 tests que comprueben reglas de negocio reales (p. ej. "no se puede crear una tarea con fecha límite pasada"), no simples `assertNotNull`.
    > * No debe incluir tests de integración que levanten el contexto de la aplicación y/o una base de datos. Solo debe incluir tests unitarios.
    > * El proyecto debe generar mediante mvn package un JAR ejecutable (fat JAR) que incluya todas las dependencias y pueda iniciarse mediante `java -jar target/<nombre>.jar`.
### 2. `chore(editor): añade .editorconfig`
- **Qué se hizo:** Añadir archivo .editorconig. 
- **Autor:** David Abellán Bleda
- **Relación con el boletín:** apartado D adición del archivo .editorconfig en un commit separado
- **Uso de IA:** no se usa
### 3. `feat(tasks): añade filtro de tareas por estado`
- **Qué se hizo:** Añadir la funcionalidad de filtrar tareas por estado a la API REST. 
- **Autor:** David Abellán Bleda
- **Relación con el boletín:** apartado D cambio pequeño pero no trivial de la API en una nueva rama y fusionarla con main
- **Uso de IA:** Se ha usado codex GPT6-Astra Extra-High para añadir la nueva funcionalidad utilizando el siguiente prompt:

    > Quiero añadir a la API REST de tareas la posibilidad de filtrar las tareas por estado.
    >
    > Requisitos:
    > - Mantén la arquitectura existente del proyecto.
    > - El endpoint que actualmente permite recuperar todas las tareas debe seguir funcionando sin parámetros.
    > - Añade la posibilidad de indicar opcionalmente un estado para recuperar únicamente las tareas que tengan dicho estado, por ejemplo mediante una petición equivalente a: GET /api/tasks?status=IN_PROGRESS
    > - Añade o modifica tests unitarios para comprobar el nuevo comportamiento.
    > - No añadas tests de integración ni tests que levanten el contexto de Spring.
    > - No realices ningún commit ni operación Git.
    > - Al terminar, ejecuta los tests del proyecto con Maven y explícame qué archivos has modificado y por qué.
### 4. `chore(build): añade Spotless para formateo automático`
- **Qué se hizo:** Añadir Spotless en el pom.xml para el formateo automático. 
- **Autor:** David Abellán Bleda
- **Relación con el boletín:** apartado E añadir el uso de Spotless y probarlo
- **Uso de IA:** no se usa
### 5. `chore(hooks): añade hook de pre-commit con Spotless`
- **Qué se hizo:** Añadir el hook .githooks/pre-commit para que no se pueda comittear código sin formatear. 
- **Autor:** David Abellán Bleda
- **Relación con el boletín:** apartado E añadir el hook pre-commit de forma separada
- **Uso de IA:** no se usa
### 6. `docs(readme): documenta configuración del hook`
- **Qué se hizo:** Añadir la documentación del hook pre-commit en el README.md. 
- **Autor:** David Abellán Bleda
- **Relación con el boletín:** apartado E documentar el uso del hook para que cada persona lo configura al utilizar el repositorio
- **Uso de IA:** no se usa
### 7. `docs(api): documenta el controlador de tareas`
- **Qué se hizo:** Añadir documentación Javadoc al controlador de tareas TaskController.java con mala identación para comprobar que se ejecuta el hook pre-commit. 
- **Autor:** David Abellán Bleda
- **Relación con el boletín:** apartado E probar el hook pre-commit.
- **Uso de IA:** no se usa
### 8. `docs(readme): actualiza nombre de la API`
- **Qué se hizo:** Cambiar el nombre del proyecto en `README.md` 
- **Autor:** David Abellán Bleda
- **Relación con el boletín:** apartado F provocar un conflicto creando dos ramas que modifiquen la misma línea del archivo `README.md`. Este commit pertenece a la rama **docs/nombre-api-a**
- **Uso de IA:** no se usa
### 9. `docs(readme): modifica título del proyecto`
- **Qué se hizo:** Cambiar el nombre del proyecto en `README.md` 
- **Autor:** David Abellán Bleda
- **Relación con el boletín:** apartado F provocar un conflicto creando dos ramas que modifiquen la misma línea del archivo `README.md`. Este commit pertenece a la rama **docs/nombre-api-b**
- **Uso de IA:** no se usa
### 10. `docs(readme): resuelve conflicto de fusión en README`
- **Qué se hizo:** Resolver el conflicto en el fichero `README.md` dejando la versión de la rama **docs/nombre-api-b**
- **Autor:** David Abellán Bleda
- **Relación con el boletín:** apartado F resolver un conflicto debido a la fusion de dos ramas que modifican la misma línea del archivo `README.md`. 
- **Uso de IA:** no se usa


## Descripción narrativa de lo hecho en la sesión y relación con los commits

### Parte A
La instalación del entorno ya estaba realizada con anterioridad y se verificó con:
```bash
git --version                                                                              
java -version
mvn -version
git version 2.54.0
openjdk version "21.0.12" 2026-07-21
OpenJDK Runtime Environment (build 21.0.12+8-1-24.04-Ubuntu)
OpenJDK 64-Bit Server VM (build 21.0.12+8-1-24.04-Ubuntu, mixed mode, sharing)
Apache Maven 3.8.7
Maven home: /usr/share/maven
Java version: 21.0.12, vendor: Ubuntu, runtime: /usr/lib/jvm/java-21-openjdk-amd64
Default locale: es_ES, platform encoding: UTF-8
OS name: "linux", version: "7.0.0-31-generic", arch: "amd64", family: "unix"

```

### Parte B

Se generó una API REST de gestión de tareas (To-Do) mediante el modelo GPT6-Astra Extra High, para ello se utilizó el propmt de ejemplo del boletin1. La aplicación generada utiliza una entidad `Task` compuesta por los campos identificador, título, descripción, estado, prioridad y fecha límite.


### Parte C

Tras generar la aplicación, se compiló, se ejecutaron los tests y se empaquetó la aplicación mediante el comando ``` mvn clean package```, lo cual generó el JAR ejecutable que se utiliza para iniciar la aplicación ejecutando ```   java -jar target/tasks-api-0.0.1-SNAPSHOT.jar ```
Con la aplicación en ejecución se realizaron peticiones mediante curl
para comprobar las operaciones CRUD de la API.

### Parte D

Tras inicializar el repositorio, el código inicial de la API y los archivos de configuración se añadieron mediante los commits 1 y 2. Posteriormente, se creó una nueva funcionalidad de filtro por estado de tareas en una nueva rama, tras hacer el commit (3) con esa nueva funcionalidad, al fusionar la rama con main se observa que git ha hecho un **Fast-forward** ya que no se han hecho nuevos commits en main mientras se trabajaba en feat/filtro-estado. Esto se observa a continuación:
```bash

git merge feat/filtro-estado                                                              
Actualizando 5ee17c8..eb8344d
Fast-forward
 README.md                                                      |  5 +++--
 src/main/java/com/example/tasks/controller/TaskController.java |  6 ++++--
 src/main/java/com/example/tasks/repository/TaskRepository.java |  5 +++++
 src/main/java/com/example/tasks/service/TaskService.java       |  8 ++++++--
 src/test/java/com/example/tasks/service/TaskServiceTest.java   | 23 ++++++++++++++++++++---
 5 files changed, 38 insertions(+), 9 deletions(-)
```
Por tanto, al ejecutar el comando ```git log --oneline --graph --all``` se ve como no se ha creado ningún commit nuevo de merge:
```bash
* eb8344d (HEAD -> main, feat/filtro-estado) feat(tasks): añade filtro de tareas por estado
* 5ee17c8 chore(editor): añade .editorconfig
* f5a25b0 chore: proyecto Maven inicial de la API de tareas
(END)
```

### Parte E
En este apartado se añadió el uso de Spotless que el proyecto tenga un único estilo y se pueda comprobar automáticamente, usando un hook pre-commit que el código quede formateado en cada commit obligatoriamente. Finalmente, para comprobar que funciona se añadió en la clase `TaskController.java` documentación Javadoc mal indentada a propósito, en este momento, al ejecutar el comando ```mvn spotless:check````sucede un error:
```bash                                                                       
[INFO] Spotless.Java is keeping 14 files clean - 1 needs changes to be clean, 0 were already clean, 13 were skipped because caching determined they were already clean
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.223 s
[INFO] Finished at: 2026-09-28T12:13:38+02:00
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal com.diffplug.spotless:spotless-maven-plugin:2.43.0:check (default-cli) on project tasks-api: The following files had format violations:
[ERROR]     src/main/java/com/example/tasks/controller/TaskController.java
[ERROR]         @@ -19,7 +19,7 @@
[ERROR]          import·org.springframework.web.bind.annotation.RestController;
[ERROR]          import·org.springframework.web.servlet.support.ServletUriComponentsBuilder;
[ERROR]          
[ERROR]         -/**Controlador·REST·encargado·de·gestionar·las·operaciones·sobre·tareas.·*/
[ERROR]         +/**·Controlador·REST·encargado·de·gestionar·las·operaciones·sobre·tareas.·*/
[ERROR]          @RestController
[ERROR]          @RequestMapping("/api/tasks")
[ERROR]          public·class·TaskController·{
[ERROR] Run 'mvn spotless:apply' to fix these violations.

```
Sin embargo, al hacer el commit (nº7) el hook se ejecuta automáticamente sin la necesidad de usar `mvn spotless:apply` manualmente y el código resultante queda bien formateado, por tanto, al ejecutar `mvn spotless:check` después de hacer el commit se comprobó que el error anterior desaparece.

## Parte F
En este último apartado, se provocó y resolvió un conflicto, para ello se crearon dos ramas: `docs/nombre-api-a` y `docs/nombre-api-b`. Estas ramas contienen un commit cada una (8 y 9) que modifican la primera línea del fichero `README.md` correspondiente con el título del proyecto.

A continuación, se fusionó a main la rama `docs/nombre-api-a` de forma exitosa, sin embargo, al fusionar la segunda rama sucedió un conflicto:

```text
<<<<<<< HEAD
# Tasks API
=======
# Servicio REST para administración de tareas
>>>>>>> docs/nombre-api-b
```
Este conflicto se resolvió editando el fichero `README.md` dejando la versión de la rama `docs/nombre-api-b`, finalmente, el historial de commits quedó de la siguiente manera:
```bash
git log --oneline --graph --all
*   79a8fd3 (HEAD -> main) docs(readme): resuelve conflicto de fusión en README
|\  
| * c52c23c (docs/nombre-api-b) docs(readme): modifica título del proyecto
* | 9491b93 (docs/nombre-api-a) docs(readme): actualiza nombre de la API
|/  
* 0477264 docs(api): documenta el controlador de tareas
* 65480b5 docs(readme): documenta configuración del hook
* a8d5ba4 chore(hooks): añade hook de pre-commit con Spotless
* 8c823da chore(build): añade Spotless para formateo automático
* eb8344d (feat/filtro-estado) feat(tasks): añade filtro de tareas por estado
* 5ee17c8 chore(editor): añade .editorconfig
* f5a25b0 chore: proyecto Maven inicial de la API de tareas
```
Como vemos, la resolución del conflicto dio lugar a un commit de merge que integra ambas ramas después de resolver manualmente el contenido de `README.md`.
