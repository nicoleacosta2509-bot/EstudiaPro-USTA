// Utilidades de fechas. El backend usa "yyyy-MM-ddTHH:mm" en hora local, sin zona horaria

const formatoFechaHora = new Intl.DateTimeFormat('es-CO', {
  weekday: 'short',
  day: 'numeric',
  month: 'short',
  hour: 'numeric',
  minute: '2-digit',
});

const formatoFecha = new Intl.DateTimeFormat('es-CO', {
  weekday: 'long',
  day: 'numeric',
  month: 'long',
});

const formatoRelativo = new Intl.RelativeTimeFormat('es', { numeric: 'auto' });

// new Date("2026-10-20T23:59") se interpreta en hora local, que es lo que queremos
export function aFecha(texto) {
  if (!texto) return null;
  const fecha = new Date(texto);
  return Number.isNaN(fecha.getTime()) ? null : fecha;
}

export function formatearFechaHora(texto) {
  const fecha = aFecha(texto);
  return fecha ? formatoFechaHora.format(fecha) : '';
}

export function formatearFechaLarga(fecha = new Date()) {
  return formatoFecha.format(fecha);
}

// "07:00:00" -> "07:00"
export function formatearHora(texto) {
  return texto ? texto.slice(0, 5) : '';
}

function dosDigitos(n) {
  return String(n).padStart(2, '0');
}

// Fecha actual en el formato del input datetime-local
export function ahoraParaInput() {
  const d = new Date();
  return `${d.getFullYear()}-${dosDigitos(d.getMonth() + 1)}-${dosDigitos(d.getDate())}T${dosDigitos(d.getHours())}:${dosDigitos(d.getMinutes())}`;
}

// Lo que devuelve el backend puede traer segundos; el input solo acepta hasta minutos
export function paraInput(texto) {
  return texto ? texto.slice(0, 16) : '';
}

export function esPasada(texto) {
  const fecha = aFecha(texto);
  return fecha ? fecha.getTime() < Date.now() : false;
}

// "hoy", "mañana", "en 3 días", "hace 2 días"
export function tiempoRelativo(texto) {
  const fecha = aFecha(texto);
  if (!fecha) return '';
  const minutos = Math.round((fecha.getTime() - Date.now()) / 60000);
  if (Math.abs(minutos) < 60) return formatoRelativo.format(minutos, 'minute');
  const horas = Math.round(minutos / 60);
  if (Math.abs(horas) < 24) return formatoRelativo.format(horas, 'hour');
  const hoy = new Date();
  hoy.setHours(0, 0, 0, 0);
  const dia = new Date(fecha);
  dia.setHours(0, 0, 0, 0);
  const dias = Math.round((dia.getTime() - hoy.getTime()) / 86400000);
  return formatoRelativo.format(dias, 'day');
}

// "HH:mm" -> minutos desde la medianoche
export function horaAMinutos(texto) {
  if (!texto) return 0;
  const [h, m] = texto.split(':').map(Number);
  return h * 60 + m;
}
