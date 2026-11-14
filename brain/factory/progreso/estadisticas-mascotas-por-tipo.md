---
type: Progreso
title: Progreso — estadisticas-mascotas-por-tipo
rama: feat/6-estadisticas-mascotas-por-tipo
estado: cerrado
---

## T001 — Página /statistics (2026-11-14)

**Resultado**: hecha, pendiente de PR. `node .mh/scripts/mvnq.mjs verify` pasa con 74 tests, incluidos `ModularityTests` e `I18nPropertiesSyncTest` sin modificar. El `pom.xml` no cambia.

**Evidencia**: `StatisticsControllerTests` (CA3-CA9), `PetTypeRepositoryTests` (CA3, CA9) y `StatisticsIntegrationTests` (CA1, CA2, CA6). Revisión en [revisiones/estadisticas-mascotas-por-tipo.md](../revisiones/estadisticas-mascotas-por-tipo.md): una ronda, nueve hallazgos arreglados y uno aparcado.

**Desviaciones y puntos no ejecutados**:
- El subagente `desarrollador` no pudo ejecutar Bash, así que T001 la hice en la sesión principal. No vi los tests en rojo antes de implementar: escribí los tests y el código en la misma pasada y la primera ejecución ya fue con ambos.
- CA7 (b) y (d), comprobación visual a 375 px con `./init.sh`: **no ejecutado**.
- P7, revisión de las traducciones por hablantes nativos: **no ejecutado**. Las claves de `fa`, `ko`, `ru` y `tr` están sin revisar.
- P9, consulta de conteo contra MySQL y PostgreSQL: **no ejecutado**. Las suites existentes de esas bases sí pasaron en `verify` (había Docker), pero ningún test nuevo ejecuta la consulta contra ellas. Corrijo así lo que antes decía este punto.
- Sin ejecutar el build antes de aplicar el formateador, `spring-javaformat:validate` falló una vez; se corrigió con `spring-javaformat:apply`.

## Cierre (2026-11-14)
Verificación en [verificaciones/estadisticas-mascotas-por-tipo-2026-11-14.md](../verificaciones/estadisticas-mascotas-por-tipo-2026-11-14.md): aprobada, con CA7 (b) y (d) no ejecutados (la comprobación visual a 375 px la hace el usuario aparte). Revisión de cierre: segunda ronda sin hallazgos nuevos. `main` no se había movido, sin integración pendiente.

**Siguiente**: push y PR cuando el usuario lo pida; al abrir el PR, tarea #6 a `revision`.
