# meta-harness-petclinic

PetClinic Modulith trabajado como factoría de software para evaluar meta-harness-factory

Brain: brain/   (memoria del proyecto: consulta brain/wiki/index.md antes de decidir; skill `brain`)

## Mapa
| Ruta | Qué hay |
|---|---|
| `src/main/java/org/springframework/samples/petclinic/owner/` | módulo de aplicación (Spring Modulith; sin dependencias declaradas) |
| `src/main/java/org/springframework/samples/petclinic/system/` | módulo de aplicación (Spring Modulith; sin dependencias declaradas) |
| `src/main/java/org/springframework/samples/petclinic/vet/` | módulo de aplicación (Spring Modulith; solo puede usar: owner) |
| `brain/` | memoria y factoría (bóveda de Obsidian) |

## Comandos
```bash
node .mh/scripts/mvnq.mjs verify                  # build y tests (salida filtrada)
node .mh/scripts/riesgo.mjs              # nivel de riesgo del cambio actual
./init.sh                                # arrancar y smoke test
```

## Reglas
- Carriles Responder · FTD · ASDD (ver orientación de inicio de sesión; glosario en brain/wiki/factoria/glosario.md).
- Constitución: brain/factory/constitucion.md. Qué cuenta como fallo: brain/factory/revision.md.
- Maven siempre con `node .mh/scripts/mvnq.mjs` (salida filtrada).
- Idioma: Código, commits, issues y PR en inglés; notas del brain en español.

# Compact instructions
Conserva: carril y documento FTD/spec en curso, tarea actual, decisiones nuevas, controles ejecutados y pendientes.
