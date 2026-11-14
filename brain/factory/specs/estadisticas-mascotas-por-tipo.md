---
type: Spec
title: Estadísticas de mascotas por tipo
description: Cualquier visitante de PetClinic podrá abrir «Estadísticas» desde el menú y ver cuántas mascotas hay de cada tipo, en gráfico y en tabla, con totales y porcentajes.
tags: [spec, owner]
estado: implementada
tarea: 6
rama: feat/6-estadisticas-mascotas-por-tipo
riesgo: 1
sources:
  - { resource: ../constitucion.md }
  - { resource: ../revision.md }
  - { resource: ../../wiki/producto/glosario.md }
generated: { by: agent:claude-opus-5-5, at: 2026-11-14 }
---

# Estadísticas de mascotas por tipo

## Problema
Hoy no hay ninguna vista agregada de las mascotas: para saber cuántas hay de cada tipo hay que recorrer los propietarios uno a uno. El personal de la clínica no tiene una visión rápida de la composición de su clientela.

## Coste de no hacerlo
La pregunta «¿cuántos gatos/perros/… atendemos?» sigue sin respuesta en la aplicación; se contesta a mano o con consultas directas a la base de datos.

## Alternativa más barata
Una tabla HTML sin gráfico. No basta: la tarea pide expresamente un gráfico con leyenda. La tabla se incluye igualmente como alternativa en texto (decisión 5 del usuario).

## Objetivos / No objetivos
Objetivos:
- Entrada «Estadísticas» en el menú principal que lleva a una página nueva.
- Gráfico (SVG o CSS, sin dependencias nuevas) con el número de mascotas de cada tipo y su leyenda.
- Total general y, por tipo, número y porcentaje; los tipos sin mascotas aparecen con 0.
- Tabla con las mismas cifras como alternativa en texto.
- Datos leídos de la base de datos en cada petición (sin caché).
- Legible en móvil (criterio medible en CA7).

No objetivos:
- Filtros por fecha, propietario o veterinario; otras estadísticas (visitas, especialidades).
- Exportar datos (CSV, imagen) o API JSON.
- Gráficos interactivos o JavaScript nuevo.
- Control de acceso (la aplicación no tiene seguridad hoy; la página es pública como el resto).

## Las 4 preguntas

### 1. ¿Qué podrá hacer el usuario?
Cualquier visitante (no hay autenticación) pulsa «Estadísticas» en el menú, en escritorio o en móvil (menú colapsado de Bootstrap), y abre la página de estadísticas. Sin precondiciones: funciona también si no hay mascotas o si hay tipos sin mascotas.

### 2. ¿Qué resultado observable obtiene?
`GET /statistics` (P1) devuelve 200 con una página que usa el layout común con la entrada «Estadísticas» marcada como activa y contiene, en este orden:

1. Título de la página (i18n).
2. Total general de mascotas.
3. Gráfico SVG de barras horizontales (P4): una barra por tipo, longitud proporcional al número de mascotas, con nombre del tipo y número junto a la barra; los tipos con 0 tienen barra de longitud 0 pero conservan su fila y etiqueta.
4. Leyenda: por tipo, muestra de color, nombre, número y porcentaje.
5. Tabla (alternativa en texto): columnas tipo, número de mascotas, porcentaje; fila final de total (número total, 100 % o 0 % si no hay mascotas).

Boceto (datos de ejemplo, 13 mascotas):

```
Estadísticas                        Total de mascotas: 13
bird    ███████            2
cat     ██████████████     4
dog     ██████████████     4
...
Leyenda: ■ bird 2 (15,4 %) ■ cat 4 (30,8 %) ...
| Tipo | Mascotas | %      |
| bird | 2        | 15,4 % |
...
| Total| 13       | 100 %  |
```

### 3. ¿Qué debe respetar?
- **Módulo owner** (decisión 1): el controlador en `owner/ui` (`@Controller` de paquete, inyección por constructor, como `OwnerController`/`PetController`); la consulta de conteo en `owner/domain` (`PetTypeRepository`). Ningún otro módulo gana dependencias Java: `ModularityTests` (`ApplicationModules.verify()`) sigue en verde (constitución C1). No se toca ningún `package-info.java` (eso sería riesgo 2).
- **Menú**: `templates/fragments/layout.html` solo añade un `menuItem` con un enlace por URL; es una plantilla, no crea dependencia Java entre `system`/`vet` y `owner`. Las entradas existentes (home, owners, vets, error) no cambian ni de orden ni de comportamiento; la nueva va tras «vets» y antes de «error» (P3).
- **Sin dependencias nuevas** (decisión 2): ni webjars ni librerías de gráficos ni JavaScript nuevo; SVG generado en servidor con Thymeleaf y estilos en `src/main/scss/petclinic.scss` (compilado por libsass del `pom.xml`).
- **Tipos con 0** (decisión 4): la consulta parte de `PetType` y cuenta mascotas con unión externa, de modo que todo tipo existente sale aunque no tenga mascotas.
- **Datos frescos**: la consulta se ejecuta en cada `GET`; sin `@Cacheable` ni caché en sesión o en memoria.
- **Solo lectura**: la página no modifica datos. Sin `@Transactional` en el controlador (regla del repo: solo en servicios de aplicación).
- **Portabilidad**: consulta JPQL (no SQL nativo) para que funcione con H2, MySQL y PostgreSQL, perfiles que ya existen.
- **i18n**: todo texto visible con claves `#{...}`; claves nuevas en todos los `messages*.properties`, para que `I18nPropertiesSyncTest` (sincronía de claves y `checkNonInternationalizedStrings`) siga en verde. Los nombres de tipo se muestran tal como están en la base de datos (hoy no se traducen en ninguna parte de la aplicación).
- **Calidad**: TDD; ningún test existente se debilita ni desactiva (Q1). Código, commits y PR en inglés (G2).
- **Lenguaje ubicuo**: «mascota» = `Pet`, «tipo de mascota» = `PetType`. El [glosario del producto](../../wiki/producto/glosario.md) está vacío; conviene añadir ambos términos (fuera del alcance de esta spec).

### 4. ¿Cómo se demuestra?
- Tests de repositorio `@DataJpaTest` (junto a `ClinicServiceTests` en `owner/domain`) para el conteo, el 0 y el orden.
- Tests `@WebMvcTest(StatisticsController.class)` con `PetTypeRepository` simulado (patrón de `PetControllerTests`) para modelo, vista, porcentajes y contenido HTML.
- Test de integración (`@SpringBootTest` + MockMvc o el patrón de `PetClinicIntegrationTests`) para datos frescos y menú.
- `ModularityTests` e `I18nPropertiesSyncTest` sin cambios, en verde.
- Todo con `node .mh/scripts/mvnq.mjs verify`; evidencia en la salida del build y en la traza de la tarea. La comprobación visual en móvil se registra como verificación manual o «no ejecutado».

## Criterios de aceptación
- **CA1** Dado el layout común, cuando se pide cualquier página (p. ej. `GET /`), entonces el menú contiene un enlace a `/statistics` con el texto de la clave i18n `statistics`, y las cuatro entradas existentes siguen presentes. *Prueba*: MockMvc sobre `/` comprueba `a[href="/statistics"]` y los enlaces existentes.
- **CA2** Dados los datos de ejemplo, cuando se pide `GET /statistics`, entonces responde 200, vista `statistics/petsByType`, entrada de menú activa `statistics`, y la tabla muestra bird 2, cat 4, dog 4, hamster 1, lizard 1, snake 1 y total 13. *Prueba*: test de integración sobre la base H2 de ejemplo.
- **CA3** Dado un tipo de mascota nuevo sin mascotas (creado en el test), cuando se pide la página, entonces ese tipo aparece en gráfico, leyenda y tabla con 0 y 0 %. *Prueba*: `@DataJpaTest` que persiste un `PetType` y comprueba que la consulta lo devuelve con 0; `@WebMvcTest` que comprueba su fila en el HTML.
- **CA4** Dados N mascotas en total, cuando se muestra un tipo con n mascotas, entonces su porcentaje es n/N·100 redondeado a 1 decimal (HALF_UP) (P2); el total muestra 100 %. Ejemplo: 4 de 13 → 30,8. *Prueba*: `@WebMvcTest` con repositorio simulado y valores 4/13, 2/13, 1/13.
- **CA5** Dado que no hay ninguna mascota (todos los tipos con 0), cuando se pide la página, entonces responde 200, total 0, todos los tipos con 0 y 0 %, sin error por división entre cero. *Prueba*: `@WebMvcTest` con repositorio simulado que devuelve todos a 0.
- **CA6** Dado que se ha visitado la página, cuando se añade una mascota de tipo «cat» y se vuelve a pedir la página, entonces el número de cat y el total aumentan en 1 sin reiniciar la aplicación. *Prueba*: test de integración (`@SpringBootTest`, `@Transactional` en el test para revertir) con dos `GET` y un alta entre medias.
- **CA7** Legible en móvil, medido así: (a) el SVG declara `viewBox` y no tiene `width`/`height` fijos en píxeles (escala al 100 % del contenedor); (b) el gráfico es de barras horizontales, con etiquetas sin rotar; (c) la tabla va dentro de un contenedor `table-responsive`; (d) la página no fuerza ancho mínimo mayor de 360 px. *Prueba*: (a) y (c) por MockMvc sobre el HTML; (b) y (d) verificación visual con `./init.sh` a 375 px de ancho (registrar captura o «no ejecutado»).
- **CA8** Dado el gráfico, cuando lo lee un lector de pantalla, entonces el SVG tiene `role="img"` y un `<title>` i18n, y la tabla existe con las mismas cifras que el gráfico y la leyenda. *Prueba*: `@WebMvcTest` comprueba `role`, `<title>` y que número y porcentaje de cada tipo coinciden en leyenda y tabla.
- **CA9** Dado el orden de presentación, cuando se muestra la página, entonces gráfico, leyenda y tabla listan los tipos en orden alfabético por nombre (P5). *Prueba*: `@DataJpaTest` sobre el orden de la consulta y `@WebMvcTest` sobre el orden en el HTML.
- **CA10** Dado el cambio completo, cuando se ejecuta `node .mh/scripts/mvnq.mjs verify`, entonces pasa en verde con `ModularityTests` e `I18nPropertiesSyncTest` sin modificar y el `pom.xml` sin dependencias nuevas. *Prueba*: salida del build y `git diff main -- pom.xml` vacío en `<dependencies>`.

## Puntos abiertos
| # | Tipo | Punto | Decisión |
|---|---|---|---|
| P1 | pregunta abierta | Ruta URL y nombres: `GET /statistics`, vista `statistics/petsByType.html`, valor de `menu`/`active` `statistics`, clave i18n `statistics`. | Recomendado: sí, esos nombres. |
| P2 | pregunta abierta | Formato del porcentaje: 1 decimal, redondeo HALF_UP, separador decimal según el idioma de la petición; la suma por tipo puede no dar exactamente 100 (p. ej. 100,1) y no se ajusta. | Recomendado: sí, 1 decimal HALF_UP sin ajuste a 100. |
| P3 | pregunta abierta | Posición en el menú: tras «vets», antes de «error». Icono: `bar-chart` de la misma fuente de iconos que usan las demás entradas. | Recomendado: sí. |
| P4 | pregunta abierta | Tipo de gráfico: barras horizontales en SVG (escalan mejor en pantallas estrechas que columnas o tarta y muestran bien el 0). | Recomendado: barras horizontales. |
| P5 | pregunta abierta | Orden de los tipos: alfabético por nombre (igual que `findPetTypes()`), no por número descendente. | Recomendado: alfabético. |
| P6 | supuesto tomado | Colores: paleta fija en SCSS que se repite cíclicamente si hay más tipos que colores; el número va siempre escrito junto a la barra, así que el color no es la única pista. | — |
| P7 | supuesto tomado | Traducciones: se añaden textos reales en todos los `messages*.properties`; la revisión por hablantes nativos de fa, ko, ru, tr, etc. queda «no ejecutado». | — |
| P8 | supuesto tomado | Los nombres de tipo (cat, dog…) no se traducen, como en el resto de la aplicación. | — |
| P9 | supuesto tomado | Los tests de MySQL/PostgreSQL (Testcontainers) solo corren si hay Docker; si no, se declaran «no ejecutado». La portabilidad se apoya en usar JPQL. | — |
