---
name: spring-modulith
description: Monolito modular con Spring Modulith — módulos de aplicación, API pública e internal/, eventos entre módulos (idempotentes) y verificación con ApplicationModules. Úsala al crear o cambiar código que cruza módulos, eventos, listeners o package-info.java.
paths: ["**/package-info.java", "**/internal/**", "**/domain/**", "**/application/**", "**/*Listener*.java", "**/*Event*.java", "**/Modularity*Test*.java"]
tags: [stack, ddd, spring-modulith, arquitectura, eventos]
---

# Spring Modulith: fronteras entre módulos

## El modelo
- **Módulo de aplicación** = cada paquete directo bajo el paquete de la clase `@SpringBootApplication` (`…/owner`, `…/vet`).
- **API pública** = los tipos del paquete raíz del módulo, más los paquetes marcados con `@NamedInterface`. Todo lo de los subpaquetes (`internal/`, `domain/`, `ui/`…) es **privado** salvo que se exponga así.
- **Dependencias permitidas**: `@ApplicationModule(allowedDependencies = { "owner" })` en el `package-info.java` del módulo. Sin ciclos.
- **Comunicación entre módulos**: preferiblemente por **eventos de aplicación** (`ApplicationEventPublisher` + `@ApplicationModuleListener`, que es asíncrono, transaccional y en transacción nueva). Llamar a un bean de otro módulo solo si está en su API pública y la dependencia está permitida.

## Reglas
1. **Antes de usar algo de otro módulo**, mira su `package-info.java` y el tuyo: si la dependencia no está permitida o el tipo no es público, no la fuerces. Las salidas son: un evento, exponer una API mínima en el paquete raíz o con `@NamedInterface`, o una copia de lectura alimentada por eventos. Si hay que elegir, es una **decisión** (ASDD), no un detalle.
2. **El evento es un contrato**: los tipos del paquete raíz que se publican como eventos (records) los consumen otros módulos y quedan guardados en `event_publication`. Cambiar sus campos rompe a quien escucha y puede impedir leer eventos pendientes → riesgo 2. Añadir es más seguro que cambiar o quitar.
3. **Listeners idempotentes**: el registro de publicaciones reentrega lo que no se completó. El listener debe poder recibir el mismo evento dos veces sin duplicar ni cambiar el resultado (comprobar si ya existe, clave natural, *upsert* consciente).
4. **Entidades JPA en `domain/`** son normales en Spring Modulith: no es arquitectura hexagonal. No las «purifiques» sin que se pida.
5. **Una transacción por módulo**: no modifiques datos de otro módulo en tu transacción; publica un evento.

## Comprobarlo
- `ApplicationModules.of(Aplicacion.class).verify()` en el test de modularidad: sin ciclos, sin acceso a tipos privados, dependencias permitidas. **Configuración protegida**: no se relaja para que pase.
- Tests de módulo con `@ApplicationModuleTest` y `Scenario`: `scenario.publish(evento)` o `scenario.stimulate(() -> …)` y esperar el efecto o el evento de salida.

## Señales de alarma (las busca el revisor de módulos)
- Import de un subpaquete de otro módulo (`…vet.internal.…` desde `owner`).
- Nueva dependencia entre módulos sin declarar, o que crea un ciclo.
- Listener que no aguanta el mismo evento dos veces.
- Cambio de campos de un evento publicado sin plan para los pendientes.
- Test de modularidad desactivado, excluido o relajado.
