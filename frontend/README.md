# Frontend: React (Vite)

Interfaz web de EstudiaPro: panel, tareas (lista y tablero), exámenes, horario semanal, asignaturas y recordatorios.

## Arranque

Necesita Node 18 o superior y el backend corriendo en `http://localhost:8080`.

```bash
cd frontend
npm install
npm run dev      # http://localhost:5173
```

En desarrollo, Vite manda las peticiones de `/api` al backend (ver `vite.config.js`), así que no hay que configurar nada.
Si el backend está en otra dirección, copie `.env.example` como `.env` y ajuste `VITE_API_URL`.

```bash
npm run lint     # análisis estático con ESLint
npm run build    # genera la versión de producción en dist/
```

## Estructura

```
src/
  api/cliente.js      peticiones al backend (token, errores)
  context/            sesión del usuario y recordatorios
  components/         layout, modales, campos de formulario
  pages/              una pantalla por archivo
  utils/              fechas y constantes
  styles.css          estilos y colores (tokens en :root)
```

Debe funcionar en Google Chrome y Microsoft Edge (RC-05); la prueba en ambos navegadores se registra antes de la entrega.
