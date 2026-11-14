---
paths: ["**/package-info.java", "**/internal/**", "**/*Listener*.java"]
---
# Módulos de aplicación (meta-harness-factory)
- De otro módulo solo su API pública (paquete raíz o `@NamedInterface`) y solo si `allowedDependencies` lo permite; nada de `internal/`.
- Entre módulos, eventos; quien escucha es idempotente. Cambiar un evento publicado o `package-info.java` es riesgo 2.
- Detalle: skill `spring-modulith`. Lo comprueba `ApplicationModules.verify()`.
