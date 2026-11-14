---
type: Plan
title: Plan — Estadísticas de mascotas por tipo
description: Una sola tarea vertical que añade la página /statistics en el módulo owner con gráfico SVG, leyenda, tabla y entrada de menú.
tags: [plan, owner]
spec: ../specs/estadisticas-mascotas-por-tipo.md
estado: vigente
generated: { by: agent:claude-sonnet-5-5, at: 2026-11-14 }
---

# Plan — Estadísticas de mascotas por tipo

## §0 Restricciones globales (literales)
- Módulo owner: el controlador en `owner/ui` (`@Controller` de paquete, inyección por constructor, como `OwnerController`/`PetController`); la consulta de conteo en `owner/domain` (`PetTypeRepository`). Ningún otro módulo gana dependencias Java: `ModularityTests` sigue en verde. No se toca ningún `package-info.java` (eso sería riesgo 2).
- Menú: `templates/fragments/layout.html` solo añade un `menuItem` con un enlace por URL. Las entradas existentes (home, owners, vets, error) no cambian ni de orden ni de comportamiento; la nueva va tras «vets» y antes de «error», con el icono `bar-chart`.
- Sin dependencias nuevas: ni webjars ni librerías de gráficos ni JavaScript nuevo; SVG generado en servidor con Thymeleaf y estilos en `src/main/scss/petclinic.scss`.
- Tipos con 0: la consulta parte de `PetType` y cuenta mascotas con unión externa.
- Datos frescos: sin `@Cacheable` ni caché en sesión o en memoria.
- Solo lectura. Sin `@Transactional` en el controlador.
- Consulta JPQL, no SQL nativo (H2, MySQL y PostgreSQL).
- i18n: todo texto visible con claves `#{...}`; claves nuevas en todos los `messages*.properties` (`I18nPropertiesSyncTest`). Los nombres de tipo no se traducen.
- TDD; ningún test existente se debilita ni desactiva. Código, commits y PR en inglés.
- Decisiones: ruta `GET /statistics`, vista `statistics/petsByType`, menú activo y clave i18n `statistics`; porcentaje con 1 decimal HALF_UP sin ajuste a 100; gráfico de barras horizontales en SVG; orden alfabético por nombre.

## Arquitectura
Módulo `owner`. `PetTypeRepository` gana una consulta JPQL de conteo por tipo; `StatisticsController` en `owner/ui` calcula porcentajes y rellena el modelo; plantilla nueva `statistics/petsByType.html`; entrada en `layout.html`; estilos en `petclinic.scss`; claves en los 9 `messages*.properties`.

## Contratos
`GET /statistics` → 200, vista `statistics/petsByType`, `menu=statistics`. Modelo: total general y lista de filas (tipo, número, porcentaje).

## Datos
Sin cambios de esquema ni migraciones.

## Estrategia de pruebas
| Criterio | Tipo de prueba | Dónde |
|---|---|---|
| CA1, CA2, CA6 | integración `@SpringBootTest` + MockMvc | `owner/ui/StatisticsIntegrationTests` |
| CA3, CA9 | `@DataJpaTest` | `owner/domain/PetTypeRepositoryTests` |
| CA3, CA4, CA5, CA7a, CA7c, CA8, CA9 | `@WebMvcTest(StatisticsController.class)` | `owner/ui/StatisticsControllerTests` |
| CA7b, CA7d | verificación visual a 375 px con `./init.sh` | registro en la verificación |
| CA10 | `node .mh/scripts/mvnq.mjs verify` y `git diff main -- pom.xml` | cierre |

## Tareas
| Id | Tarea | Hecho cuando (literal) | Depende | CA | Gestor |
|---|---|---|---|---|---|
| T001 | Página `/statistics` completa (consulta, controlador, plantilla SVG/leyenda/tabla, menú, SCSS, i18n) | `node .mh/scripts/mvnq.mjs verify` pasa con los tests nuevos, `ModularityTests` e `I18nPropertiesSyncTest` sin modificar | — | CA1–CA10 | — (sin subtarea, decisión del usuario) |

## Riesgos
- Riesgo 1. Si alguna ruta sube a riesgo 2 (p. ej. un `*.java` en la raíz de `owner`), parar y avisar.
- La comprobación visual en móvil puede quedar «no ejecutado» si no se puede arrancar la aplicación.
