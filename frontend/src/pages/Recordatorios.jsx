import { useCallback, useEffect, useState } from 'react';
import { api } from '../api/cliente';
import { useRecordatorios } from '../context/RecordatoriosContext';
import { esPasada, formatearFechaHora, tiempoRelativo } from '../utils/fechas';

export default function Recordatorios() {
  const [lista, setLista] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const { atender, permiso, pedirPermiso } = useRecordatorios();

  const cargar = useCallback(async () => {
    try {
      setLista(await api.get('/recordatorios'));
      setError('');
    } catch (e) {
      setError(e.message);
    } finally {
      setCargando(false);
    }
  }, []);

  useEffect(() => {
    cargar();
  }, [cargar]);

  async function marcarAtendido(id) {
    try {
      await atender(id);
      await cargar();
    } catch (e) {
      setError(e.message);
    }
  }

  return (
    <>
      <div className="encabezado-pagina">
        <h1>Recordatorios</h1>
      </div>
      <p className="texto-suave">
        Los recordatorios se crean al guardar una tarea o un examen con la opción «Recordatorio». La app revisa cada minuto si toca avisarte.
      </p>

      {permiso === 'default' && (
        <div className="alerta alerta-info fila-separada">
          <span>Activa los avisos del navegador para enterarte aunque estés en otra pestaña.</span>
          <button type="button" className="boton boton-pequeno" onClick={pedirPermiso}>Activar avisos</button>
        </div>
      )}
      {permiso === 'denied' && (
        <div className="alerta alerta-info">Los avisos del navegador están bloqueados. Puedes activarlos desde la configuración del sitio en el navegador.</div>
      )}

      {error && <div className="alerta alerta-error">{error}</div>}
      {cargando && <p className="texto-suave">Cargando recordatorios…</p>}
      {!cargando && lista.length === 0 && <div className="vacio">No tienes recordatorios próximos.</div>}

      <div className="lista-tarjetas">
        {lista.map((r) => {
          const tocaAvisar = esPasada(r.fechaAviso);
          return (
            <article key={r.id} className={`tarjeta tarjeta-item ${tocaAvisar ? 'resaltada' : ''}`}>
              <div className="fila-separada">
                <div>
                  <span className={`insignia insignia-${r.tipo === 'EXAMEN' ? 'examen' : 'tarea'}`}>{r.tipo === 'EXAMEN' ? 'Examen' : 'Tarea'}</span>
                  <strong>{r.titulo}</strong>
                </div>
                {tocaAvisar && <span className="insignia estado-en_proceso">Por atender</span>}
              </div>
              <p className="texto-pequeno">
                Aviso: {formatearFechaHora(r.fechaAviso)} <span className="texto-suave">({tiempoRelativo(r.fechaAviso)})</span>
              </p>
              <p className="texto-pequeno texto-suave">
                {r.tipo === 'EXAMEN' ? 'Examen' : 'Entrega'}: {formatearFechaHora(r.fechaEvento)}
              </p>
              <div className="acciones-item">
                <div className="crece" />
                <button type="button" className="boton boton-secundario boton-pequeno" onClick={() => marcarAtendido(r.id)}>
                  Marcar como atendido
                </button>
              </div>
            </article>
          );
        })}
      </div>
    </>
  );
}
