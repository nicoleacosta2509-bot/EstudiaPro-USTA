// Íconos SVG sencillos (usan el color del texto)
function Icono({ children, tam = 20 }) {
  return (
    <svg width={tam} height={tam} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"
      strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
      {children}
    </svg>
  );
}

export const IconoInicio = () => (
  <Icono><path d="M3 11l9-8 9 8" /><path d="M5 10v10h14V10" /></Icono>
);
export const IconoTareas = () => (
  <Icono><path d="M9 11l3 3 8-8" /><path d="M20 12v7a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h9" /></Icono>
);
export const IconoExamen = () => (
  <Icono><path d="M14 3H6a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V9z" /><path d="M14 3v6h6" /><path d="M8 13h8M8 17h5" /></Icono>
);
export const IconoHorario = () => (
  <Icono><rect x="3" y="4" width="18" height="17" rx="2" /><path d="M3 9h18M8 2v4M16 2v4" /></Icono>
);
export const IconoAsignatura = () => (
  <Icono><path d="M4 19V5a2 2 0 0 1 2-2h13v16H6a2 2 0 0 0-2 2z" /><path d="M8 7h7" /></Icono>
);
export const IconoCampana = () => (
  <Icono><path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9" /><path d="M10 21a2 2 0 0 0 4 0" /></Icono>
);
export const IconoMenu = () => (
  <Icono><path d="M3 6h18M3 12h18M3 18h18" /></Icono>
);
export const IconoSalir = () => (
  <Icono tam={18}><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" /><path d="M16 17l5-5-5-5M21 12H9" /></Icono>
);
