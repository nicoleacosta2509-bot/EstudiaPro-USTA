import { useCallback, useEffect, useState } from 'react';
import { api } from '../api/cliente';
import ConfirmarModal from '../components/ConfirmarModal';
import ExamenForm from '../components/ExamenForm';
import Modal from '../components/Modal';
import { ESTADOS_EXAMEN, etiquetaDe } from '../utils/constantes';
import { formatearFechaHora, tiempoRelativo } from '../utils/fechas';

const FILTROS = [
  { valor: 'PENDIENTE', etiqueta: 'Próximos' },
  { valor: 'PRESENTADO', etiqueta: 'Presentados' },
  { valor: '', etiqueta: 'Todos' },
];

export default function Examenes() {
  const [examenes, setExamenes] = useState([]);
  const [asignaturas, setAsignaturas] = useState([]);
  const [filtro, setFiltro] = useState('PENDIENTE');
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const [editando, setEditando] = useState(undefined); // undefined = cerrado, null = nuevo
  const [porEliminar, setPorEliminar] = useState(null);

  const cargar = useCallback(async () => {
    try {
      setExamenes(await api.get('/examenes'));
      setError('');
    } catch (e) {
      setError(e.message);
    } finally {
      setCargando(false);
    }
  }, []);

  useEffect(() => {
    cargar();
    api.get('/asignaturas').then(setAsignaturas).catch(() => setAsignaturas([]));
  }, [cargar]);

  async function cambiarEstado(examen, estado) {
    try {
      await api.patch(`/examenes/${examen.id}/estado`, { estado });
      await cargar();
    } catch (e) {
      setError(e.message);
    }
  }

  async function eliminar() {
    await api.eliminar(`/examenes/${porEliminar.id}`);
    await cargar();
  }

  const visibles = filtro ? examenes.filter((e) => e.estado === filtro) : examenes;

  return (
    <>
      <div className="encabezado-pagina">
        <h1>Exámenes</h1>
        <button type="button" className="boton" onClick={() => setEditando(null)}>+ Nuevo examen</button>
      </div>

      <div className="barra-filtros">
        <div className="selector-vista" role="group" aria-label="Filtrar exámenes">
          {FILTROS.map((f) => (
            <button key={f.etiqueta} type="button" className={filtro === f.valor ? 'activo' : ''} onClick={() => setFiltro(f.valor)}>
              {f.etiqueta}
            </button>
          ))}
        </div>
      </div>

      {error && <div className="alerta alerta-error">{error}</div>}
      {cargando && <p className="texto-suave">Cargando exámenes…</p>}
      {!cargando && visibles.length === 0 && <div className="vacio">No hay exámenes en esta lista.</div>}

      <div className="lista-tarjetas">
        {visibles.map((ex) => (
          <article key={ex.id} className="tarjeta tarjeta-item" style={{ borderLeftColor: ex.color || 'var(--borde-fuerte)' }}>
            <div className="fila-separada">
              <strong>{ex.titulo}</strong>
              <span className={`insignia estado-${ex.estado.toLowerCase()}`}>{etiquetaDe(ESTADOS_EXAMEN, ex.estado)}</span>
            </div>
            {ex.asignaturaNombre && <p className="texto-suave texto-pequeno">{ex.asignaturaNombre}</p>}
            <p className="texto-pequeno">
              {formatearFechaHora(ex.fecha)}
              {ex.estado === 'PENDIENTE' && <span className="texto-suave"> ({tiempoRelativo(ex.fecha)})</span>}
              {ex.lugar && <span className="texto-suave"> · {ex.lugar}</span>}
            </p>
            {ex.temas && <p className="descripcion">{ex.temas}</p>}
            <div className="acciones-item">
              <div className="crece" />
              {ex.estado === 'PENDIENTE' ? (
                <button type="button" className="boton boton-pequeno" onClick={() => cambiarEstado(ex, 'PRESENTADO')}>Marcar presentado</button>
              ) : (
                <button type="button" className="boton boton-secundario boton-pequeno" onClick={() => cambiarEstado(ex, 'PENDIENTE')}>Volver a pendiente</button>
              )}
              <button type="button" className="boton-texto" onClick={() => setEditando(ex)}>Editar</button>
              <button type="button" className="boton-texto texto-peligro" onClick={() => setPorEliminar(ex)}>Eliminar</button>
            </div>
          </article>
        ))}
      </div>

      <Modal titulo={editando ? 'Editar examen' : 'Nuevo examen'} abierto={editando !== undefined} alCerrar={() => setEditando(undefined)}>
        <ExamenForm
          examen={editando}
          asignaturas={asignaturas}
          alGuardar={() => {
            setEditando(undefined);
            cargar();
          }}
          alCancelar={() => setEditando(undefined)}
        />
      </Modal>

      <ConfirmarModal
        abierto={porEliminar !== null}
        titulo="Eliminar examen"
        mensaje={`¿Seguro que quieres eliminar «${porEliminar?.titulo}»? Esta acción no se puede deshacer.`}
        alConfirmar={eliminar}
        alCancelar={() => setPorEliminar(null)}
      />
    </>
  );
}
