# Backend: Spring Boot

API REST de EstudiaPro. Spring Boot 3.3, Java 21, Spring Data JPA, Spring Security con JWT y validación con Bean Validation.

## Arranque

```bash
cd backend
mvn spring-boot:run            # H2 en archivo (backend/data), puerto 8080
mvn test                       # pruebas de integración
```

Con PostgreSQL (por ejemplo con `docker compose up -d db` desde la raíz):

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

| Variable | Por defecto | Nota |
|---|---|---|
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | `jdbc:postgresql://localhost:5432/estudiapro`, `estudiapro`, `estudiapro123` | solo con el perfil `postgres` |
| `JWT_SECRET` | secreto de desarrollo | cambiarlo en cualquier despliegue (Base64, 256 bits o más) |
| `CORS_ORIGINS` | `http://localhost:5173` | orígenes del frontend separados por coma |
| `DATOS_DEMO` | `true` | crea la cuenta de ejemplo si la base está vacía |

## Paquetes

```
co.edu.usta.estudiapro
  usuario/       registro, login y cuenta
  seguridad/     JWT, filtro y configuración de Spring Security
  asignatura/    materias del estudiante
  horario/       clases de la semana (valida cruces)
  tarea/         tareas con prioridad y estado
  examen/        parciales y quices
  recordatorio/  avisos que se crean al guardar tareas y exámenes
  panel/         resumen de la pantalla de inicio
  comun/         errores y formato de respuesta
```

Cada módulo tiene entidad, repositorio, DTOs, servicio y controlador. Toda consulta filtra por el estudiante autenticado: nadie ve ni modifica datos de otro.
