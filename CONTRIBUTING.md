# Guía de contribución

## Requisitos

Para trabajar con el proyecto es necesario disponer de:

- Git
- Java 21
- Maven

## Preparación del proyecto

Clonar el repositorio:

```bash
git clone URL_DEL_REPOSITORIO
cd NOMBRE_DEL_REPOSITORIO
```

Construir el proyecto y ejecutar los tests:

```bash
mvn clean package
```

Ejecutar la aplicación:

```bash
java -jar target/tasks-api-0.0.1-SNAPSHOT.jar
```

## Hooks de Git

El repositorio incluye hooks versionados en la carpeta `.githooks`.

Después de clonar el repositorio es necesario configurar Git para que utilice estos hooks:

```bash
git config core.hooksPath .githooks
```

El hook de `pre-commit` ejecuta Spotless automáticamente para garantizar que el código quede correctamente formateado antes de realizar cada commit.

## Flujo de trabajo

No se deben realizar cambios directamente sobre la rama `main`.

Para desarrollar una nueva funcionalidad o modificación se debe crear una rama a partir de `main`:

```bash
git switch main
git pull --ff-only
git switch -c feat/nombre-funcionalidad
```

Una vez finalizado el cambio:

1. Ejecutar los tests y comprobar que el proyecto funciona correctamente.
2. Realizar los commits necesarios.
3. Subir la rama al repositorio remoto.
4. Abrir un Pull Request hacia `main`.
5. Resolver los comentarios o cambios solicitados durante la revisión.
6. Fusionar el Pull Request una vez aprobado.

Para subir por primera vez una rama al repositorio remoto:

```bash
git push -u origin nombre-rama
```

## Convención de commits

El proyecto utiliza la convención Conventional Commits.

Algunos ejemplos son:

```text
feat(tasks): añade búsqueda de tareas por título
fix(api): corrige validación de tareas
test(service): añade tests del filtrado
docs(readme): actualiza documentación
chore(build): actualiza configuración de Maven
```

Los tipos principales utilizados son:

- `feat`: nueva funcionalidad.
- `fix`: corrección de errores.
- `docs`: cambios únicamente en documentación.
- `test`: cambios relacionados con tests.
- `refactor`: reestructuración del código sin modificar su comportamiento.
- `chore`: tareas de configuración o mantenimiento.
- `ci`: cambios relacionados con integración continua.

## Pull Requests

Todo cambio destinado a `main` debe realizarse mediante un Pull Request.

Antes de fusionar un Pull Request:

- Los tests deben ejecutarse correctamente.
- El código debe estar correctamente formateado.
- El Pull Request debe recibir al menos una aprobación.
- Las conversaciones y comentarios de revisión deben estar resueltos.
- Deben respetarse las revisiones solicitadas mediante `CODEOWNERS`.

Los Pull Requests deben utilizar la plantilla incluida en `.github/PULL_REQUEST_TEMPLATE.md`.

Cuando el Pull Request esté asociado a un Issue, debe incluirse en su descripción:

```text
Closes #N
```

donde `N` es el número del Issue correspondiente.

## Política de fusión

La estrategia de fusión utilizada en el proyecto es **Squash and merge**.

De esta forma, todos los commits realizados durante el desarrollo de una funcionalidad se agrupan en un único commit al incorporarse a `main`, manteniendo un historial más limpio y legible.

El título del Pull Request debe seguir la convención de commits utilizada en el proyecto, por ejemplo:

```text
feat(tasks): añade filtro de tareas por prioridad
```

Este título será utilizado como mensaje del commit resultante al realizar el `Squash and merge`.

## Formato del código

El proyecto utiliza Spotless con Google Java Format para mantener un estilo de código uniforme.

Para comprobar manualmente el formato:

```bash
mvn spotless:check
```

Para aplicar automáticamente el formato:

```bash
mvn spotless:apply
```

El hook de `pre-commit` ejecuta automáticamente `spotless:apply` antes de crear cada commit.
