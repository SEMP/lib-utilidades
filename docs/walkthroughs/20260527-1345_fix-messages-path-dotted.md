# Fix `MESSAGES_PATH` con `/` inicial — bump `2.1.1 → 2.1.2`

**Fecha:** 2026-05-27 · **ADO:** AB#42116

## Cambio

`Values.Constants.MESSAGES_PATH`: `"/py/com/semp/lib/utilidades/"` → `"py.com.semp.lib.utilidades."`.

`MessageUtil` arma el bundle name como `PATH + RESOURCE`. Con el `/` inicial, `ResourceBundle.getBundle` solo funcionaba en module-path (Surefire), pero fallaba en classpath (Spring Boot `JarLauncher` del consumidor `samr2-adq`) porque `ClassLoader.getResource` no strippea el slash inicial como sí lo hace `Module.getResourceAsStream`. Resultado: `MissingResourceException` silenciado por el catch de `MessageRetriever` → mensajes salían como `"Failed to load message: <KEY>"`.

Forma dotted es la canónica documentada en `ResourceBundle.getBundle`.

## Archivos

- `lib_utilidades/src/main/java/py/com/semp/lib/utilidades/configuration/Values.java`: constante a dotted.
- `lib_utilidades/src/test/java/py/com/semp/lib/utilidades/internal/MessageUtilTest.java#testResource`: construcción del path se adapta al nuevo formato (`"/" + PATH.replace('.','/') + RESOURCE + ".properties"`).
- `lib_utilidades/pom.xml`: `<version>2.1.1</version>` → `2.1.2`.

## Verificación

- `mvn clean install`: 159 tests OK, `lib.utilidades-2.1.2.jar` instalado en `~/.m2`.

## Walkthrough central

`samr2-tareas/walkthroughs/20260527-1345_fix-i18n-messages-path-leading-slash.md` cubre el cambio en los 10 repos + consumidores.
