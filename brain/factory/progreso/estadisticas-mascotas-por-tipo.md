---
type: Progreso
title: Progreso — estadisticas-mascotas-por-tipo
rama: feat/6-estadisticas-mascotas-por-tipo
---

## T001 — Página /statistics (2026-11-14)

**Resultado**: hecha, pendiente de PR. `node .mh/scripts/mvnq.mjs verify` pasa con 74 tests, incluidos `ModularityTests` e `I18nPropertiesSyncTest` sin modificar. El `pom.xml` no cambia.

**Evidencia**: `StatisticsControllerTests` (CA3-CA9), `PetTypeRepositoryTests` (CA3, CA9) y `StatisticsIntegrationTests` (CA1, CA2, CA6). Revisión en [revisiones/estadisticas-mascotas-por-tipo.md](../revisiones/estadisticas-mascotas-por-tipo.md): una ronda, nueve hallazgos arreglados y uno aparcado.

**Desviaciones y puntos no ejecutados**:
- El subagente `desarrollador` no pudo ejecutar Bash, así que T001 la hice en la sesión principal. No vi los tests en rojo antes de implementar: escribí los tests y el código en la misma pasada y la primera ejecución ya fue con ambos.
- CA7 (b) y (d), comprobación visual a 375 px con `./init.sh`: **no ejecutado**.
- P7, revisión de las traducciones por hablantes nativos: **no ejecutado**. Las claves de `fa`, `ko`, `ru` y `tr` están sin revisar.
- P9, tests de MySQL y PostgreSQL con Docker: **no ejecutado**.
- Sin ejecutar el build antes de aplicar el formateador, `spring-javaformat:validate` falló una vez; se corrigió con `spring-javaformat:apply`.

**Siguiente**: cierre (verificación final con evidencias y PR) cuando lo pidas.
