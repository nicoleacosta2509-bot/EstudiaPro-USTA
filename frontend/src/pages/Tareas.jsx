import { useCallback, useEffect, useState } from 'react';
import { api } from '../api/cliente';
import ConfirmarModal from '../components/ConfirmarModal';
import Modal from '../components/Modal';
import TareaForm from '../components/TareaForm';
import { ESTADOS_TAREA, PRIORIDADES, etiquetaDe } from '../utils/constantes';
import { formatearFechaHora, tiempoRelativo } from '../utils/fechas';

const CLAVE_VISTA = 'estudiapro_vista_tareas';

function leerVista() {
  try {
    return localStorage.getItem(CLAVE_VISTA) || 'lista';
  } catch {
    return 'lista';
  }
}

export default function Tareas() {
  const [tareas, setTareas] = useState([]);
  const [asignaturas, setAsignaturas] = useState([]);
  const [filtros, setFiltros] = useState({ estado: '', asignaturaId: '' });
  const [vista, setVista] = useState(leerVista);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const [editando, setEditando] = useState(undefined); // undefined = cerrado, null = nueva
  const [porEliminar, setPorEliminar] = useState(null);

  const cargar = useCallback(async () => {
    const parametros = new URLSearchParams();
    // En el tablero se ven todas las columnas, por eso no se filtra por estado
    if (filtros.estado && vista === 'lista') parametros.set('estado', filtros.estado);
    if (filtros.asignaturaId) parametros.set('asignaturaId', filtros.asignaturaId);
    const consulta = parametros.toString();
    try {
      setTareas(await api.get(`/tareas${consulta ? `?${consulta}` : ''}`));
      setError('');
    } catch (e) {
      setError(e.message);
    } finally {
      setCargando(false);
    }
  }, [filtros, vista]);

  useEffect(() => {
    cargar();
  }, [cargar]);

  useEffect(() => {
    api.get('/asignaturas').then(setAsignaturas).catch(() => setAsignaturas([]));
  }, []);

  function cambiarVista(nueva) {
    setVista(nueva);
    try {
      localStorage.setItem(CLAVE_VISTA, nueva);
    } catch {
      // No pasa nada si no se puede recordar la vista
    }
  }

  async function cambiarEstado(tarea, estado) {
    try {
      await api.patch(`/tareas/${tarea.id}/estado`, { estado });
      await cargar();
    } catch (e) {
      setError(e.message);
    }
  }

  async function eliminar() {
    await api.eliminar(`/tareas/${porEliminar.id}`);
    await cargar();
  }

  function guardado() {
    setEditando(undefined);
    cargar();
  }

  return (
    <>
      <div className="encabezado-pagina">
        <h1>Tareas</h1>
        <button type="button" className="boton" onClick={() => setEditando(null)}>+ Nueva tarea</button>
      </div>

      <div className="barra-filtros">
        <div className="selector-vista" role="group" aria-label="Vista">
          <button type="button" className={vista === 'lista' ? 'activo' : ''} onClick={() => cambiarVista('lista')}>Lista</button>
          <button type="button" className={vista === 'tablero' ? 'activo' : ''} onClick={() => cambiarVista('tablero')}>Tablero</button>
        </div>
        {vista === 'lista' && (
          <select aria-label="Filtrar por estado" value={filtros.estado} onChange={(e) => setFiltros({ ...filtros, estado: e.target.value })}>
            <option value="">Todos los estados</option>
            {ESTADOS_TAREA.map((e) => (
              <option key={e.valor} value={e.valor}>{e.etiqueta}</option>
            ))}
          </select>
        )}
        <select aria-label="Filtrar por asignatura" value={filtros.asignaturaId} onChange={(e) => setFiltros({ ...filtros, asignaturaId: e.target.value })}>
          <option value="">Todas las asignaturas</option>
          {asignaturas.map((a) => (
            <option key={a.id} value={a.id}>{a.nombre}</option>
          ))}
        </select>
      </div>

      {error && <div className="alerta alerta-error">{error}</div>}
      {cargando && <p className="texto-suave">Cargando tareas…</p>}

      {!cargando && vista === 'lista' && (
        tareas.length === 0 ? (
          <div className="vacio">No hay tareas con estos filtros. Crea una con «Nueva tarea».</div>
        ) : (
          <div className="lista-tarjetas">
            {tareas.map((t) => (
              <TarjetaTarea key={t.id} tarea={t} alEditar={setEditando} alEliminar={setPorEliminar} alCambiarEstado={cambiarEstado} />
            ))}
          </div>
        )
      )}

      {!cargando && vista === 'tablero' && (
        <div className="tablero">
          {ESTADOS_TAREA.map((columna) => {
            const delEstado = tareas.filter((t) => t.estado === columna.valor);
            return (
              <section key={columna.valor} className="columna-tablero">
                <h2>
                  {columna.etiqueta} <span className="texto-suave">({delEstado.length})</span>
                </h2>
                {delEstado.length === 0 && <p className="texto-suave texto-pequeno">Sin tareas.</p>}
                {delEstado.map((t) => (
                  <TarjetaTarea key={t.id} tarea={t} compacta alEditar={setEditando} alEliminar={setPorEliminar} alCambiarEstado={cambiarEstado} />
                ))}
              </section>
            );
          })}
        </div>
      )}

      <Modal titulo={editando ? 'Editar tarea' : 'Nueva tarea'} abierto={editando !== undefined} alCerrar={() => setEditando(undefined)}>
        <TareaForm tarea={editando} asignaturas={asignaturas} alGuardar={guardado} alCancelar={() => setEditando(undefined)} />
      </Modal>

      <ConfirmarModal
        abierto={porEliminar !== null}
        titulo="Eliminar tarea"
        mensaje={`¿Seguro que quieres eliminar «${porEliminar?.titulo}»? Esta acción no se puede deshacer.`}
        alConfirmar={eliminar}
        alCancelar={() => setPorEliminar(null)}
      />
    </>
  );
}

// Orden de los estados para los botones de mover
const ORDEN = ESTADOS_TAREA.map((e) => e.valor);

function TarjetaTarea({ tarea, compacta, alEditar, alEliminar, alCambiarEstado }) {
  const posicion = ORDEN.indexOf(tarea.estado);
  const anterior = ORDEN[posicion - 1];
  const siguiente = ORDEN[posicion + 1];

  return (
    <article className={`tarjeta tarjeta-item ${tarea.vencida ? 'vencida' : ''}`} style={{ borderLeftColor: tarea.color || 'var(--borde-fuerte)' }}>
      <div className="fila-separada">
        <strong className={tarea.estado === 'TERMINADA' ? 'tachado' : ''}>{tarea.titulo}</strong>
        <span className={`insignia prioridad-${tarea.prioridad?.toLowerCase()}`}>{etiquetaDe(PRIORIDADES, tarea.prioridad)}</span>
      </div>
      {tarea.asignaturaNombre && <p className="texto-suave texto-pequeno">{tarea.asignaturaNombre}</p>}
      {!compacta && tarea.descripcion && <p className="descripcion">{tarea.descripcion}</p>}
      <p className={`texto-pequeno ${tarea.vencida ? 'texto-peligro' : 'texto-suave'}`}>
        {tarea.vencida ? 'Vencida · ' : 'Entrega: '}
        {formatearFechaHora(tarea.fechaEntrega)} ({tiempoRelativo(tarea.fechaEntrega)})
      </p>
      <div className="acciones-item">
        {!compacta && <span className={`insignia estado-${tarea.estado.toLowerCase()}`}>{etiquetaDe(ESTADOS_TAREA, tarea.estado)}</span>}
        <div className="crece" />
        {anterior && (
          <button type="button" className="boton boton-secundario boton-pequeno" onClick={() => alCambiarEstado(tarea, anterior)} title={`Mover a ${etiquetaDe(ESTADOS_TAREA, anterior)}`}>
            ← {etiquetaDe(ESTADOS_TAREA, anterior)}
          </button>
        )}
        {siguiente && (
          <button type="button" className="boton boton-pequeno" onClick={() => alCambiarEstado(tarea, siguiente)} title={`Mover a ${etiquetaDe(ESTADOS_TAREA, siguiente)}`}>
            {etiquetaDe(ESTADOS_TAREA, siguiente)} →
          </button>
        )}
        <button type="button" className="boton-texto" onClick={() => alEditar(tarea)}>Editar</button>
        <button type="button" className="boton-texto texto-peligro" onClick={() => alEliminar(tarea)}>Eliminar</button>
      </div>
    </article>
  );
}
