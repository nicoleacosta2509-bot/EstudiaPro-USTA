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
  backend/   Spring Boot 3.3 + Java 21 + JPA + JWT (puerto 8080), ver backend/README.md
  frontend/  React 18 + Vite (puerto 5173), ver frontend/README.md
  docker-compose.yml  PostgreSQL 16 para desarrollo
```

## Arranque rápido

Requisitos: JDK 21, Maven 3.9 y Node 18 o superior.

```bash
# 1) Backend (sin configurar nada usa H2 en archivo; para PostgreSQL ver backend/README.md)
cd backend
mvn spring-boot:run

# 2) Frontend, en otra terminal
cd frontend
npm install
npm run dev
```

Abrir http://localhost:5173. La primera vez se crea una cuenta de ejemplo con datos de prueba: `demo@estudiapro.co` / `demo123`. Esa cuenta es solo para desarrollo; se desactiva con `DATOS_DEMO=false`.

## Funcionalidades

- **Cuenta:** registro e inicio de sesión. Cada estudiante ve solo su información.
- **Panel:** tareas pendientes, en proceso y vencidas, exámenes de los próximos 14 días, porcentaje de avance, lo que vence en los próximos 7 días y las clases de hoy.
- **Tareas:** crear, editar y eliminar, con prioridad, asignatura y fecha de entrega. Se ven en lista o en tablero (Pendiente → En proceso → Terminada) y se filtran por estado y asignatura.
- **Exámenes:** fecha, lugar y temas. Se marcan como presentados.
- **Horario:** cuadrícula semanal con color por asignatura. No deja guardar clases que se cruzan.
- **Recordatorios:** al guardar una tarea o un examen se elige con cuánta anticipación avisar (1 hora, 1 día, 2 días o 1 semana). La app revisa cada minuto y muestra el aviso, también como notificación del navegador si el usuario da permiso.

## API

Todas las rutas están bajo `/api`, usan JSON y piden `Authorization: Bearer <token>`, salvo registro y login. Los errores llegan como `{ "mensaje": "...", "campos": { "campo": "..." } }`.

| Recurso | Rutas |
|---|---|
| Autenticación | `POST /auth/registro`, `POST /auth/login`, `GET /auth/yo` |
| Asignaturas | `GET/POST /asignaturas`, `PUT/DELETE /asignaturas/{id}` |
| Horario | `GET/POST /horarios`, `PUT/DELETE /horarios/{id}` |
| Tareas | `GET /tareas?estado=&asignaturaId=`, `POST /tareas`, `PUT/DELETE /tareas/{id}`, `PATCH /tareas/{id}/estado` |
| Exámenes | `GET/POST /examenes`, `PUT/DELETE /examenes/{id}`, `PATCH /examenes/{id}/estado` |
| Recordatorios | `GET /recordatorios`, `GET /recordatorios/pendientes`, `PATCH /recordatorios/{id}/atendido` |
| Panel | `GET /panel` |

## Pruebas

`mvn test` en `backend/` corre pruebas de integración con H2 en memoria:

- **RC-03:** se rechazan campos vacíos, fechas pasadas, formatos inválidos y clases que se cruzan.
- **RC-04:** lo que se crea, edita o cambia de estado se mantiene al volver a consultarlo.
- **Seguridad:** sin token no se puede consultar nada, y un estudiante no ve ni borra datos de otro.
- **Recordatorios y panel:** un recordatorio desaparece al terminar la tarea, y el panel calcula bien el avance.

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
