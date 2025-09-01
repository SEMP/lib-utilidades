# Repository Guidelines

## Project Structure & Module Organization
- Source code: `lib_utilidades/src` (JPMS module `lib_utilidades`).
- Tests: `lib_utilidades/test` (JUnit 5/Jupiter).
- Resources: `lib_utilidades/resources` (i18n message bundles).
- Third‑party JARs: `lib_utilidades/lib` (Jackson, JUnit params).
- IDE: Eclipse project (`.project`, `.classpath`) targeting Java 17.

## Build, Test, and Development Commands
- Java version: 17. Ensure `lib_utilidades/lib` is on the module/class path.
- Compile sources (CLI):
  - `javac -d lib_utilidades/bin --module-path lib_utilidades/lib $(find lib_utilidades/src -name '*.java')`
- Run tests:
  - Eclipse: Right‑click project → Run As → JUnit Test.
  - CLI (if you add `junit-platform-console-standalone.jar` under `lib_utilidades/lib`):
    - `java -jar lib_utilidades/lib/junit-platform-console-standalone.jar -cp lib_utilidades/bin:lib_utilidades/lib/* --scan-class-path`

## Coding Style & Naming Conventions
- Language: Java 17; 4‑space indentation; no tabs.
- Packages: `py.com.semp.lib.utilidades.*`.
- Classes: PascalCase; methods/fields: camelCase; constants: UPPER_SNAKE_CASE.
- Public API: keep stable; prefer clear, null‑safe inputs (`Objects.requireNonNull`).
- Formatting: follow Eclipse defaults; keep lines readable (<120 cols).

## Testing Guidelines
- Framework: JUnit 5 (Jupiter).
- Location: mirror production packages under `lib_utilidades/test`.
- Naming: `XxxTest.java` per class under test.
- Coverage: prioritize utilities, edge cases, concurrency, and exception paths.

## Commit & Pull Request Guidelines
- Commits: short, imperative subject in Spanish preferred (e.g., `utilities: agrega trimFromStart`).
- Scope: optionally prefix with area (e.g., `messages:`, `data:`). Keep subjects ≤72 chars.
- PRs: include motivation, summary of changes, tests added/updated, and any behavior changes. Link issues and add before/after examples when helpful.

## Notes & Tips
- Module requires Jackson and JUnit; ensure jars in `lib_utilidades/lib` are available during compile/test.
- Localization files live under `lib_utilidades/resources/py/com/semp/lib/utilidades/` (`messages*.properties`).
