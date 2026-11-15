---
type: FTD
title: Línea temporal de visitas en la ficha del dueño
description: Las visitas de cada mascota se muestran como línea temporal vertical (más reciente arriba, marca para futuras, texto si no hay).
tags: [ftd, owner]
estado: hecho
rama: feat/2-visit-timeline
tarea: 2
riesgo: 1
generated: { by: agent:claude-sonnet-5-5, at: 2026-11-14 }
---

# Línea temporal de visitas en la ficha del dueño

## Intención
Tarea #2: sustituir la tabla de visitas de `ownerDetails.html` por una línea temporal vertical. Sin cambios en el esquema ni en la base de datos.

## Alcance
- `Pet.getVisitsNewestFirst()`: visitas derivadas, orden de fecha descendente (no persistente).
- `ownerDetails.html`: línea temporal con fecha y descripción, marca distinta para visitas futuras y texto cuando no hay visitas.
- Claves i18n nuevas (`noVisits`, `upcomingVisit`) en `messages.properties` y en los 8 idiomas: `I18nPropertiesSyncTest` exige las mismas claves en todos los ficheros (alcance ampliado al descubrirlo; traducciones sin validar por nativos).
- Visitas sin fecha (`visit_date` es nullable): se muestran al final y no se marcan como futuras.
- CSS mínimo en `petclinic.css`.

## Fuera de alcance
- Modelo de datos, migraciones, formularios de visita.

## Checklist
- [x] Orden más reciente primero — prueba: `PetVisitsOrderTests`
- [x] Visita futura marcada y pasada sin marca — prueba: `OwnerControllerTests` (HTML de `/owners/{id}`)
- [x] Mascota sin visitas muestra el texto — prueba: `OwnerControllerTests`
- [x] Visita sin fecha no rompe la ficha — prueba: `OwnerControllerTests.testShowOwnerWithUndatedVisitStillRenders`
- [x] Controles: `node .mh/scripts/mvnq.mjs verify`

## Evidencia
> [!success] Comportamiento
> Los tests nuevos fallaron antes del código (error de compilación en `getVisitsNewestFirst`) y los de fecha nula fallaron con NPE antes de la corrección. Tras el cambio: `mvnq verify` → `Tests run: 70, Failures: 0, Errors: 0`, `BUILD SUCCESS`. La comprobación es con MockMvc sobre el HTML renderizado; no se ha abierto la app en un navegador (no ejecutado).

> [!info] Revisión
> Revisor de corrección (contexto limpio, riesgo 1): 2 hallazgos. Fecha nula → NPE/500 (corregido con test). Claves i18n fuera del alcance del FTD (alcance corregido: el test de sincronía lo exige).

## Notas
- El CSS compilado `petclinic.css` está versionado y no hay fuente LESS; las reglas se añadieron al final.
- La hora de «hoy» para marcar visitas futuras es la del servidor.
