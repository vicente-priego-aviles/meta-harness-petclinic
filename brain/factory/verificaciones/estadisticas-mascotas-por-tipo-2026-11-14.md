---
type: Verificación
title: Verificación — estadisticas-mascotas-por-tipo
spec: ../specs/estadisticas-mascotas-por-tipo.md
rama: feat/6-estadisticas-mascotas-por-tipo
fecha: 2026-11-14
resultado: aprobada
---

Aprobada con un único punto no ejecutado: la comprobación visual de CA7 (b) y (d) a 375 px, que hace el usuario aparte.

| Criterio | Prueba | Resultado |
|---|---|---|
| CA1 | `StatisticsIntegrationTests.menuLinksToStatistics`: `GET /` contiene los enlaces a `/statistics`, `/owners/find`, `/vets.html` y `/oups` | ✅ pasado |
| CA2 | `StatisticsIntegrationTests.showsSampleData`: 200, vista `statistics/petsByType`, total 13, las seis filas de la tabla en orden y `/statistics` como entrada activa | ✅ pasado |
| CA3 | `PetTypeRepositoryTests.includesTypesWithoutPetsWithZero` (tipo nuevo con 0) y `StatisticsControllerTests.zeroPetsShowsEveryTypeWithZeroWithoutDividingByZero` (fila de leyenda y de tabla con 0 y 0,0 %) | ✅ pasado |
| CA4 | `StatisticsControllerTests.percentagesAreRoundedAndMatchInLegendAndTable` (2, 4 y 7 de 13) y `halfIsRoundedUp` (1/16 = 6,25 → 6,3) | ✅ pasado |
| CA5 | `StatisticsControllerTests.zeroPetsShowsEveryTypeWithZeroWithoutDividingByZero`: total 0, sin `NaN` | ✅ pasado |
| CA6 | `StatisticsIntegrationTests.reflectsNewPetWithoutRestart`: total 13, se añade un gato, total 14 | ✅ pasado |
| CA7 (a) y (c) | `StatisticsControllerTests.chartIsAccessibleAndScalesToTheScreen`: `viewBox`, sin `width` ni `height` en el `<svg>`, `table-responsive` | ✅ pasado |
| CA7 (b) y (d) | Comprobación visual a 375 px con `./init.sh` | ⏸ no ejecutado (la hace el usuario aparte) |
| CA8 | `chartIsAccessibleAndScalesToTheScreen` (`role="img"` seguido de `<title>`) y `percentagesAreRoundedAndMatchInLegendAndTable` (las mismas cifras en leyenda y tabla) | ✅ pasado |
| CA9 | `PetTypeRepositoryTests.countsPetsPerTypeOrderedByName` (orden de la consulta) y `StatisticsControllerTests.keepsRepositoryOrderInLegendAndTable` (el HTML conserva ese orden) | ✅ pasado |
| CA10 | `node .mh/scripts/mvnq.mjs verify` en verde; `git diff main -- pom.xml` sin líneas | ✅ pasado |

## Controles
| Control | Resultado |
|---|---|
| `node .mh/scripts/mvnq.mjs verify` | ✅ 74 tests, 0 fallos, 0 errores, 0 omitidos |
| `ModularityTests` (Spring Modulith) | ✅ 2 tests, sin modificar |
| `I18nPropertiesSyncTest` | ✅ 2 tests, sin modificar |
| `spring-javaformat:validate` | ✅ tras `spring-javaformat:apply` |
| ArchUnit | ⏸ no aplica: el proyecto no lo usa |
| Integración con la rama principal | ✅ `main` no se ha movido desde el origen de la rama (`20cb35a`), no hay nada que integrar |
| Tests de MySQL y PostgreSQL | ⏸ las suites existentes pasaron, pero ningún test nuevo ejecuta la consulta de conteo contra esas bases; la portabilidad se apoya en que es JPQL (P9) |
| Revisión de traducciones por hablantes nativos (P7) | ⏸ no ejecutado: `fa`, `ko`, `ru`, `tr` sin revisar |

## Evidencias
> [!success] CA10
> `Tests run: 74, Failures: 0, Errors: 0, Skipped: 0` y `BUILD SUCCESS` en `mvnq verify`. `StatisticsControllerTests`: 6 tests; `StatisticsIntegrationTests`: 3; `PetTypeRepositoryTests`: 2. Todos en verde.

> [!success] CA4
> El caso 1/16 distingue HALF_UP de HALF_EVEN: 6,25 sale como 6,3 con HALF_UP y saldría 6,2 con HALF_EVEN.

> [!warning] Lo que no demuestra esta verificación
> Que la página se ve bien en un móvil real (CA7 b y d) ni que las traducciones sean correctas. El hallazgo H9 de la revisión (nombres de tipo muy largos que se solaparían con la barra) sigue aparcado.

## Plan de vuelta atrás
Revertir el commit `2fa9446` (`git revert 2fa9446`). No hay migraciones ni cambios de esquema ni dependencias nuevas, así que la vuelta atrás no deja restos en la base de datos.
