// Valores fijos que usa la interfaz (deben coincidir con los enums del backend)

export const ESTADOS_TAREA = [
  { valor: 'PENDIENTE', etiqueta: 'Pendiente' },
  { valor: 'EN_PROCESO', etiqueta: 'En proceso' },
  { valor: 'TERMINADA', etiqueta: 'Terminada' },
];

export const ESTADOS_EXAMEN = [
  { valor: 'PENDIENTE', etiqueta: 'Pendiente' },
  { valor: 'PRESENTADO', etiqueta: 'Presentado' },
];

export const PRIORIDADES = [
  { valor: 'BAJA', etiqueta: 'Baja' },
  { valor: 'MEDIA', etiqueta: 'Media' },
  { valor: 'ALTA', etiqueta: 'Alta' },
];

export const DIAS = [
  { valor: 'LUNES', etiqueta: 'Lunes', corto: 'Lun' },
  { valor: 'MARTES', etiqueta: 'Martes', corto: 'Mar' },
  { valor: 'MIERCOLES', etiqueta: 'Miércoles', corto: 'Mié' },
  { valor: 'JUEVES', etiqueta: 'Jueves', corto: 'Jue' },
  { valor: 'VIERNES', etiqueta: 'Viernes', corto: 'Vie' },
  { valor: 'SABADO', etiqueta: 'Sábado', corto: 'Sáb' },
  { valor: 'DOMINGO', etiqueta: 'Domingo', corto: 'Dom' },
];

// Minutos de anticipación del recordatorio ("" = sin recordatorio)
export const OPCIONES_RECORDATORIO = [
  { valor: '', etiqueta: 'Sin recordatorio' },
  { valor: '60', etiqueta: '1 hora antes' },
  { valor: '1440', etiqueta: '1 día antes' },
  { valor: '2880', etiqueta: '2 días antes' },
  { valor: '10080', etiqueta: '1 semana antes' },
];

export const COLORES_ASIGNATURA = ['#2f6fed', '#0f9d76', '#e2563b', '#9b51e0', '#d99a00', '#e0457b', '#1b8fb3', '#5f6b7a'];

export function etiquetaDe(lista, valor) {
  return lista.find((item) => item.valor === valor)?.etiqueta ?? valor;
}
