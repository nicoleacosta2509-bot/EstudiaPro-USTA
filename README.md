# EstudiaPro — Plataforma de organización académica

Aplicación web para que un estudiante reúna en un solo lugar sus tareas, exámenes y horarios, reciba recordatorios y siga el avance de sus actividades (Acta de constitución, Gerencia de Software — USTA Villavicencio, sexto semestre).

**Equipo**
- David Alejandro Martín: desarrollo técnico (frontend, backend, integración con PostgreSQL).
- Nicole Tatiana Acosta Tellez: análisis, pruebas y documentación, apoyo en desarrollo.

**Interlocutora principal:** Laura Valentina Rojas (estudiante de Ingeniería de Sistemas).

**Tecnologías:** React · Spring Boot · PostgreSQL

## Alcance v1.0

**Incluye:** registro y consulta de tareas · registro y consulta de exámenes · organización de horarios · recordatorios de actividades y fechas · seguimiento básico del estado de las actividades.

**Excluye:** conexión con sistemas de la universidad (matrícula, notas) · app móvil nativa · recomendaciones con IA · módulos administrativos · integraciones externas no necesarias.

## Estructura

```
EstudiaPro/
  docs/      Acta de constitución y tablero configurado
  backend/   Spring Boot + PostgreSQL (puerto 8080)
  frontend/  React
```

## Requisitos de calidad

| ID | Aspecto | Requisito |
|---|---|---|
| RC-01 | Usabilidad | Funciones entendibles sin explicación extensa; errores que dicen qué corregir. |
| RC-02 | Rendimiento | Pantallas principales sin esperas innecesarias. |
| RC-03 | Integridad de datos | Validar campos obligatorios y fechas; no guardar datos incompletos. |
| RC-04 | Confiabilidad | Lo que se registra o edita se mantiene al volver a consultarlo. |
| RC-05 | Compatibilidad | Funciona en Google Chrome y Microsoft Edge. |
| RC-06 | Mantenibilidad | Código separado entre frontend, backend y base de datos. |

## Tablero

Cuatro estados: **Pendiente → En proceso → En revisión → Terminado**.

- Cada tarea necesita un responsable antes de pasar a *En proceso*.
- Pasa a *En revisión* cuando está lista para comprobarse.
- Solo pasa a *Terminado* si cumple su objetivo, no tiene errores conocidos que impidan su uso y la revisó el integrante responsable.
- El tablero se revisa en el seguimiento semanal.
