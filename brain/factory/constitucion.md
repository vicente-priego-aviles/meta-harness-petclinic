---
type: Constitución
title: Constitución de meta-harness-petclinic
description: Reglas fijas del proyecto y definición de terminado; cambiarlas requiere confirmación.
tags: [constitucion, factoria]
generated: { by: meta-harness-factory, at: 2026-11-12 }
status: draft
---

# Constitución

> [!warning] Borrador
> Generado por el instalador. Revísalo con el agente (skill `asdd`, paso 0) y márcalo `status: stable`.

## 1. Stack canónico
- Build: maven (Maven Wrapper)
- Spring Boot (versión en el pom.xml)
- Spring Modulith (módulos de aplicación y eventos)

## 2. Arquitectura
- C1. Los módulos de aplicación solo se usan entre sí por su API pública (paquete raíz del módulo o `@NamedInterface`) y por eventos; nada de `internal/` desde fuera. Lo comprueba `ApplicationModules.verify()` en el test de modularidad.
- C2. Quien escucha un evento de otro módulo es idempotente: el registro de publicaciones puede reentregarlo.

## 3. Procesos
- <sin reglas de procesos; completar si hace falta>

## 4. Calidad
- Q1. TDD; ningún test se debilita ni desactiva para que pase.

## 5. Seguridad
- S1. Ningún secreto en el repositorio.

## 6. Git
- G1. Riesgo 2 → PR con aprobación humana.
- G2. Código, commits, issues y PR en inglés; notas del brain en español.

## 7. Definición de terminado
- [ ] `node .mh/scripts/mvnq.mjs verify` en verde
- [ ] Evidencia del comportamiento pedido (FTD o verificación)
- [ ] Controles no ejecutados declarados como «no ejecutado»

## 8. Registro de enmiendas
| Fecha | Cambio | Aprobado por |
|---|---|---|
| 2026-11-12 | Borrador inicial | — |
