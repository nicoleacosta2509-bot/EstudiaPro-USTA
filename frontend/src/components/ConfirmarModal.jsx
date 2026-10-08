import { useState } from 'react';
import Modal from './Modal';

// Pide confirmación antes de una acción que no se puede deshacer (por ejemplo, eliminar)
export default function ConfirmarModal({ abierto, titulo, mensaje, textoBoton = 'Eliminar', alConfirmar, alCancelar }) {
  const [enviando, setEnviando] = useState(false);
  const [error, setError] = useState('');

  function cerrar() {
    setError('');
    alCancelar();
  }

  async function confirmar() {
    setEnviando(true);
    setError('');
    try {
      await alConfirmar();
      cerrar();
    } catch (e) {
      // Por ejemplo, un 409 al eliminar una asignatura que tiene tareas
      setError(e.message);
    } finally {
      setEnviando(false);
    }
  }

  return (
    <Modal titulo={titulo} abierto={abierto} alCerrar={cerrar} ancho={420}>
      <p>{mensaje}</p>
      {error && <div className="alerta alerta-error">{error}</div>}
      <div className="acciones-formulario">
        <button type="button" className="boton boton-secundario" onClick={cerrar}>
          Cancelar
        </button>
        <button type="button" className="boton boton-peligro" onClick={confirmar} disabled={enviando}>
          {enviando ? 'Procesando…' : textoBoton}
        </button>
      </div>
    </Modal>
  );
}
