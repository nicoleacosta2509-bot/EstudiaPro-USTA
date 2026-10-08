import { useEffect } from 'react';

// Ventana modal genérica: se cierra con Escape o con el botón ×
export default function Modal({ titulo, abierto, alCerrar, children, ancho = 520 }) {
  useEffect(() => {
    if (!abierto) return undefined;
    function teclado(e) {
      if (e.key === 'Escape') alCerrar();
    }
    document.addEventListener('keydown', teclado);
    return () => document.removeEventListener('keydown', teclado);
  }, [abierto, alCerrar]);

  if (!abierto) return null;

  return (
    <div className="modal-fondo" onMouseDown={(e) => e.target === e.currentTarget && alCerrar()}>
      <div className="modal" role="dialog" aria-modal="true" aria-label={titulo} style={{ maxWidth: ancho }}>
        <div className="modal-cabecera">
          <h2>{titulo}</h2>
          <button type="button" className="boton-icono" onClick={alCerrar} aria-label="Cerrar">
            ×
          </button>
        </div>
        <div className="modal-cuerpo">{children}</div>
      </div>
    </div>
  );
}
