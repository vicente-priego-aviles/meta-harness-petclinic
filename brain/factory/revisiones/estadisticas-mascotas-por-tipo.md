---
type: Revisión
title: Revisión — estadisticas-mascotas-por-tipo
rama: feat/6-estadisticas-mascotas-por-tipo
resultado: aprobada
rondas: 1
---

Riesgo 1, un revisor (`revisor-java-spring`). El código de producción no tuvo hallazgos bloqueantes. Los hallazgos del revisor los comprobé yo mismo contra el código, sin pasar por `verificador-hallazgos`; queda anotado porque el procedimiento lo pide.

| # | Revisor | Hallazgo | Gravedad | Veredicto | Cierre |
|---|---|---|---|---|---|
| H1 | java-spring | `StatisticsControllerTests`: la aserción de `<title>` era vacua (el `<head>` ya lo emite) | media | CONFIRMADO | arreglado: regex acotada a `<svg role="img">` seguido de `<title>` |
| H2 | java-spring | El orden (CA9) no se comprobaba; la regex `bird.*cat` coincidía con cualquier sección | media | CONFIRMADO | arreglado: regex sobre filas de tabla y entradas de leyenda en secuencia |
| H3 | java-spring | `not(containsString("<svg width="))` no detectaba `width`/`height` fijos | baja | CONFIRMADO | arreglado: regex sobre los atributos del `<svg>` |
| H4 | java-spring | El redondeo HALF_UP no estaba probado (los valores dan igual con HALF_EVEN) | media | CONFIRMADO | arreglado: caso 1/16 = 6,25 → 6,3 |
| H5 | java-spring | CA3 y CA5 débiles (`"0.0"` lo cumplía la fila de total) | baja-media | CONFIRMADO | arreglado: se comprueba cada tipo con 0 en leyenda y tabla |
| H6 | java-spring | CA2 no comprobaba las cifras por tipo ni la entrada activa | media | CONFIRMADO | arreglado: el test de integración comprueba las seis filas, el total 13 y que `/statistics` es la entrada activa |
| H7 | java-spring | Falta de test de coincidencia leyenda/tabla (CA8) | baja-media | CONFIRMADO | arreglado: ambos se comprueban con las mismas cifras |
| H8 | java-spring | División entera: una cuenta pequeña frente al máximo daba barra de ancho 0, igual que un tipo con 0 | baja | CONFIRMADO | arreglado: el ancho se calcula en el controlador con mínimo 1 si hay mascotas; test añadido |
| H9 | java-spring | Etiqueta de 90 unidades sin recorte: un nombre largo o una cifra enorme se solapa o sale del `viewBox` | baja | CONFIRMADO | aparcado: con los tipos actuales no ocurre; si se añaden nombres largos habrá que ampliar el `viewBox` |
| H10 | java-spring | `role="img"` sin `aria-labelledby` | baja (informativo) | CONFIRMADO | arreglado: `aria-labelledby` apunta al `<title>` |
