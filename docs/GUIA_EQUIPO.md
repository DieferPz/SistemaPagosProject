# Guía del equipo – Sistema de Procesamiento de Pagos (Java Swing + MySQL)

## 1. Puesta en marcha (cada integrante, una sola vez)
1. Instalar MySQL local (8.0.16 o superior) y NetBeans con JDK 21.
2. Ejecutar `database/schema.sql` en MySQL Workbench (crea la base `sistema_pagos` y las tablas).
   Opcional: `database/seed.sql` para tener ventas de ejemplo.
3. Copiar `db.properties.example` → `db.properties` y poner su usuario/contraseña de MySQL (este archivo NO se sube a git).
4. Verificar que existe `libs/mysql-connector-j.jar` (ver `libs/LEEME.txt`).
5. Abrir la carpeta `SistemaPagos` como proyecto en NetBeans → Run.
   En el primer arranque se crea el usuario **admin / admin123** (cámbienlo después).

## 2. Arquitectura (capas)
```
ui  ──▶  service  ──▶  dao (interfaces) ──▶ dao.mysql (JDBC)
              └──▶  payment (métodos de pago)      model / validation / security / exception
```
- **ui**: solo pantallas Swing. Nunca ejecutan SQL ni reglas de negocio.
- **service**: reglas de negocio y validaciones. Lanzan `ValidacionException` / `NegocioException`.
- **dao**: contratos (interfaces). `dao.mysql`: implementación con JDBC.
- **payment**: jerarquía `MetodoPago` (Template Method): `validarDatos()` + `simular()`.
- **UiUtil.ejecutar(...)** captura las excepciones y muestra un mensaje comprensible.
- Los mensajes de excepción ya están pensados para mostrarse al usuario tal cual.

## 3. Reparto de módulos
| Persona | Módulo | Archivos propios | Qué falta hacer |
|---|---|---|---|
| **A (líder)** | Base de datos, login/logout, integración | `dao/mysql/*`, `AuthService`, `LoginFrame`, `MainFrame`, `AppContext`, `database/*` | Probar los DAOs contra MySQL real, pantalla/gestión de usuarios si se desea, integrar ramas, revisar PRs |
| **B** | Ventas (Proceso 1) | `VentaService`, `VentasPanel` | Formulario alta, modificar/eliminar (solo pendientes), buscar por código y fecha, detalle de venta |
| **C** | Pagos (Procesos 2 y 3) | `payment/*`, `PagoService`, `PagoPanel` | Pantalla de pago con campos dinámicos por método, mostrar resultado/motivo/cambio |
| **D** | Consultas y resumen (Procesos 4 y 5) | `ConsultaService`, `ReporteService`, `TransaccionesPanel`, `ResumenPanel` | Filtros (método, estado, fechas, venta), búsqueda por código, resumen claro método × estado |

**Regla de oro:** cada quien edita solo sus archivos. Si necesitan cambiar un contrato
(interfaz DAO, modelo o servicio de otro), avisen al grupo y hagan un PR pequeño aparte.

## 4. Flujo de trabajo con Git
- `main` siempre debe compilar. Nadie hace push directo a `main`.
- Una rama por módulo: `feature/ventas`, `feature/pagos`, `feature/consultas`, `feature/auth-db`.
- Commits pequeños y con mensaje claro (`Ventas: validar importe mayor que cero`).
- Antes de abrir un PR: `git pull origin main`, compilar y probar su pantalla.
- Todo PR lo revisa al menos otra persona.
- Reunión corta de integración cada 2–3 días.

## 5. Datos de prueba para la demostración
| Método | Aprobado | Rechazado (simulado) |
|---|---|---|
| Efectivo | importe recibido ≥ importe de la venta (calcula cambio) | — (si no alcanza: error de validación) |
| Tarjeta | 16 dígitos, p. ej. `4111 1111 1111 1111`, tipo CREDITO/DEBITO, importe ≤ 5000 | número terminado en `0000`, o importe > 5000 |
| Transferencia | cuenta 8–20 dígitos (`12345678`), referencia alfanumérica 6–20 (`ABC123`), importe ≤ 10000 | importe > 10000 |
| Pago electrónico | proveedor + token `tok_abc12345` | token que termina en `_fail` |

Usar solo datos ficticios. Nunca números reales de tarjeta: el sistema solo guarda los últimos 4 dígitos.

## 6. Reglas de negocio ya implementadas en los servicios
- Código de venta único; importe numérico > 0 con máximo 2 decimales; campos obligatorios sin espacios.
- Venta pagada: no se modifica, no se elimina, no se vuelve a pagar.
- El importe del pago es el importe de la venta (sin comisiones).
- Pago rechazado: se registra la transacción con su motivo y la venta sigue pendiente.
- Pago aprobado: se registra y la venta pasa a PAGADA en la misma transacción SQL (`TransaccionDAOMySQL.registrarResultado`).
- Resumen: los rechazados no suman a los ingresos aprobados.
- Todo servicio exige sesión iniciada (`Sesion.requerirAutenticacion()`).

## 7. Casos de la sección 5.5 del documento (lista de verificación)
- [x] Venta inexistente · [x] Código duplicado · [x] Campos vacíos/ inválidos · [x] Importe ≤ 0 o mal formateado
- [x] Método no seleccionado · [x] Datos del método incompletos · [x] Efectivo insuficiente
- [x] Pago rechazado · [x] Venta ya pagada · [x] Transacción inexistente
- [x] Error de conexión/lectura/escritura MySQL (`PersistenciaException`)
- [ ] Consulta o reporte sin resultados → mostrar mensaje en las pantallas (B y D)
- [ ] Error al actualizar el estado de la venta → cubierto por rollback; probarlo (A y C)
