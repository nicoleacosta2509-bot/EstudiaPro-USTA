# Medición de RC-02 (eficiencia de desempeño)

Mide el tiempo de respuesta de cuatro operaciones comunes, con 10 peticiones cada una, usando la cuenta de
ejemplo (`DATOS_DEMO=true`). Las tareas que crea las borra al terminar.

```bash
cd backend && mvn spring-boot:run      # en otra terminal
python tests/rendimiento/medir_rc02.py
```

| Operación | Promedio | Máximo |
|---|---|---|
| Cargar el panel (`GET /api/panel`) | 0,009 s | 0,010 s |
| Listar tareas (`GET /api/tareas`) | 0,007 s | 0,008 s |
| Crear una tarea (`POST /api/tareas`) | 0,010 s | 0,039 s |
| Cambiar el estado (`PATCH /api/tareas/{id}/estado`) | 0,008 s | 0,014 s |

Medición del 8 de octubre de 2026 con H2 en archivo y la cuenta de ejemplo (4 tareas). El umbral de la ficha es
un promedio menor a 1,0 s. Falta repetirla con PostgreSQL y unas 50 tareas (issue #11).
