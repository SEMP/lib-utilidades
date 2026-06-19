# lib-utilidades — Documentación del proyecto

## 1. Rol en SAMR2

Librería de **utilidades generales** del sistema SAMR2 — toolkit base sobre el que
se apoyan los drivers IEC 62056-21 y el resto del ecosistema (buffers circulares,
tipos de datos, logging, máquinas de estado, configuración, i18n). Es una
reescritura SAMR2 de la utilidad equivalente de SAMR1. Coordenada Maven:
`py.com.semp:lib.utilidades`.

## 2. Stack / tecnologías

- **Java 17** (`maven.compiler.release=17`), encoding UTF-8.
- Proyecto **modular (JPMS)** — tiene `module-info.java`.
- Coordenada Maven: `py.com.semp:lib.utilidades:2.1.2` (packaging `jar`;
  `<name>lib-utilidades</name>`).
- Plugins Maven: `maven-compiler-plugin` 3.13.0, `maven-surefire-plugin` 3.2.5.

## 3. Estructura del repo

El `pom.xml` está **anidado** en `lib_utilidades/pom.xml` (no en la raíz del repo).
Código en `lib_utilidades/src/main/java/py/com/semp/lib/utilidades/`, con paquetes
top-level:

- `communication` (+ `interfaces`, `listeners`)
- `configuration` — `Values`, `ConfigurationValues`
- `data` — `CircularByteBuffer` (+ `Iterator`), `Pair`, `TypedParameter/Result/Row/Value`
- `exceptions`
- `internal`
- `log` — `Logger`, `DefaultLogger`, `LoggerManager`, `LogLevel`
- `messages`
- `shutdown`
- `state/machines` — `State`, `StateManager`
- `utilities` — `ArrayUtils`, `Utilities`, `Converter`, `NamedThreadFactory`

Recursos i18n: `src/main/resources/py/com/semp/lib/utilidades/messages.properties`
(+ `_en`, `_es`).

Tests en `src/test/java/...` espejando los paquetes (`CircularByteBufferTest`,
`PairTest`, `ArrayUtilsTest`, `UtilitiesTest`, `MessageUtilTest`,
`MessageManagerTest`, etc.), con fixtures JSON en `src/test/resources/data/buffer/`.

## 4. Build, ejecución y tests

Es una **librería** (no se ejecuta).

- **Build/test:** `mvn -f lib_utilidades/pom.xml package` (Surefire corre los tests
  JUnit 5).

## 5. Dependencias e integraciones

- **Dependencias (pom):** solo de `scope=test` —
  `com.fasterxml.jackson.core:jackson-databind:2.15.2` (deserializar fixtures) y
  `org.junit.jupiter:junit-jupiter:5.9.3`. **Sin dependencias de producción.**
- **Quién la consume:** `samr2-adq` la declara como dependencia Maven y la resuelve
  desde su `local-maven-repo/` (jar versionado en git). También es base de los
  drivers (`samr.drivers`) y de `lib-socket`.
- **Origen:** reescritura SAMR2; remote en **GitHub público**
  (`git@github.com:SEMP/lib-utilidades.git`).

## 6. Ramas y versionado

- Rama principal: `main` — **ya contiene la migración a Maven** (la rama
  `feat/migracion-maven`, con la migración + el fix de i18n, fue fusionada). No hay
  `master`.
- **No hay archivos `version-*.txt`.** La versión es única y vive en el `pom.xml`
  (`2.1.2`).

## 7. Gotchas / notas conocidas

- **ResourceBundle / path dotted (AB#42116) — ya corregido.** En la migración Maven,
  `Values.Constants.MESSAGES_PATH` pasó de `"/py/com/semp/lib/utilidades/"` a la
  forma *dotted* `"py.com.semp.lib.utilidades."`. La forma con `/` inicial
  funcionaba en module-path (Surefire) pero rompía en classpath (Spring Boot
  `JarLauncher` del `samr2-adq`) → `MissingResourceException` silenciado y mensajes
  como `"Failed to load message: <KEY>"`. **Usar siempre la forma dotted.** Ver
  walkthrough `docs/walkthroughs/20260527-1345_fix-messages-path-dotted.md` y la
  memoria `samr2-agent/memorias/resourcebundle-jpms-classpath-gotcha.md`.
- README mínimo; convenciones del repo en `AGENTS.md` (paquetes
  `py.com.semp.lib.utilidades.*`, Java 17, 4 espacios, commits en español
  imperativo).
- Tras cambiar esta lib, refrescar el jar del consumidor con
  `samr2-adq/scripts/actualizar-deps-locales.sh`.

## 8. Referencias

- `lib_utilidades/pom.xml` — coordenadas y deps.
- `lib_utilidades/src/main/java/py/com/semp/lib/utilidades/` — paquetes.
- `src/main/resources/py/com/semp/lib/utilidades/messages*.properties` — i18n.
- `docs/walkthroughs/20260527-1345_fix-messages-path-dotted.md`
- `AGENTS.md` (convenciones del repo).
- Canónico: `samr2-agent/CLAUDE.md`; memoria `resourcebundle-jpms-classpath-gotcha.md`.
