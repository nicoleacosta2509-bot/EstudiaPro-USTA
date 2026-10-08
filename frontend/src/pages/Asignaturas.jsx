import { useCallback, useEffect, useState } from 'react';
import { api } from '../api/cliente';
import Campo from '../components/Campo';
import ConfirmarModal from '../components/ConfirmarModal';
import Modal from '../components/Modal';
import { COLORES_ASIGNATURA } from '../utils/constantes';

export default function Asignaturas() {
  const [asignaturas, setAsignaturas] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const [editando, setEditando] = useState(undefined); // undefined = cerrado, null = nueva
  const [porEliminar, setPorEliminar] = useState(null);

  const cargar = useCallback(async () => {
    try {
      setAsignaturas(await api.get('/asignaturas'));
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

  async function eliminar() {
    // Si tiene tareas, exámenes u horarios el backend responde 409 y el modal muestra el mensaje
    await api.eliminar(`/asignaturas/${porEliminar.id}`);
    await cargar();
  }

  return (
    <>
      <div className="encabezado-pagina">
        <h1>Asignaturas</h1>
        <button type="button" className="boton" onClick={() => setEditando(null)}>+ Nueva asignatura</button>
      </div>
      <p className="texto-suave">Cada asignatura tiene un color que se usa en tareas, exámenes y el horario.</p>

      {error && <div className="alerta alerta-error">{error}</div>}
      {cargando && <p className="texto-suave">Cargando asignaturas…</p>}
      {!cargando && asignaturas.length === 0 && <div className="vacio">Aún no tienes asignaturas. Agrega las materias que estás viendo.</div>}

      <div className="rejilla-asignaturas">
        {asignaturas.map((a) => (
          <article key={a.id} className="tarjeta tarjeta-asignatura" style={{ borderTopColor: a.color }}>
            <strong>{a.nombre}</strong>
            <p className="texto-suave">{a.docente || 'Sin docente registrado'}</p>
            <div className="acciones-item">
              <div className="crece" />
              <button type="button" className="boton-texto" onClick={() => setEditando(a)}>Editar</button>
              <button type="button" className="boton-texto texto-peligro" onClick={() => setPorEliminar(a)}>Eliminar</button>
            </div>
          </article>
        ))}
      </div>

      <Modal titulo={editando ? 'Editar asignatura' : 'Nueva asignatura'} abierto={editando !== undefined} alCerrar={() => setEditando(undefined)} ancho={440}>
        <AsignaturaForm
          asignatura={editando}
          colorSugerido={COLORES_ASIGNATURA[asignaturas.length % COLORES_ASIGNATURA.length]}
          alGuardar={() => {
            setEditando(undefined);
            cargar();
          }}
          alCancelar={() => setEditando(undefined)}
        />
      </Modal>

      <ConfirmarModal
        abierto={porEliminar !== null}
        titulo="Eliminar asignatura"
        mensaje={`¿Seguro que quieres eliminar «${porEliminar?.nombre}»?`}
        alConfirmar={eliminar}
        alCancelar={() => setPorEliminar(null)}
      />
    </>
  );
}

function AsignaturaForm({ asignatura, colorSugerido, alGuardar, alCancelar }) {
  const esNueva = !asignatura;
  const [valores, setValores] = useState({
    nombre: asignatura?.nombre ?? '',
    docente: asignatura?.docente ?? '',
    color: asignatura?.color ?? colorSugerido,
  });
  const [errores, setErrores] = useState({});
  const [errorGeneral, setErrorGeneral] = useState('');
  const [enviando, setEnviando] = useState(false);

  function cambiar(e) {
    setValores({ ...valores, [e.target.name]: e.target.value });
  }

  function validar() {
    const nuevos = {};
    if (!valores.nombre.trim()) nuevos.nombre = 'El nombre de la asignatura es obligatorio.';
    else if (valores.nombre.length > 80) nuevos.nombre = 'El nombre no puede tener más de 80 caracteres.';
    if (valores.docente.length > 80) nuevos.docente = 'El nombre del docente no puede tener más de 80 caracteres.';
    if (!/^#[0-9a-fA-F]{6}$/.test(valores.color)) nuevos.color = 'Elige un color válido.';
    return nuevos;
  }

  async function enviar(e) {
    e.preventDefault();
    const nuevos = validar();
    setErrores(nuevos);
    setErrorGeneral('');
    if (Object.keys(nuevos).length > 0) return;

    const cuerpo = { nombre: valores.nombre.trim(), docente: valores.docente.trim() || null, color: valores.color };
    setEnviando(true);
    try {
      if (esNueva) await api.post('/asignaturas', cuerpo);
      else await api.put(`/asignaturas/${asignatura.id}`, cuerpo);
      alGuardar();
    } catch (error) {
      setErrores(error.campos);
      setErrorGeneral(error.message);
    } finally {
      setEnviando(false);
    }
  }

  return (
    <form onSubmit={enviar} noValidate>
      {errorGeneral && <div className="alerta alerta-error">{errorGeneral}</div>}
      <Campo etiqueta="Nombre" id="nombre" error={errores.nombre} obligatorio>
        <input id="nombre" name="nombre" maxLength={80} placeholder="Ej: Gerencia de Software" value={valores.nombre} onChange={cambiar} autoFocus />
      </Campo>
      <Campo etiqueta="Docente" id="docente" error={errores.docente}>
        <input id="docente" name="docente" maxLength={80} value={valores.docente} onChange={cambiar} />
      </Campo>
      <Campo etiqueta="Color" id="color" error={errores.color}>
        <div className="selector-color">
          {COLORES_ASIGNATURA.map((c) => (
            <button
              key={c}
              type="button"
              className={`muestra-color ${valores.color.toLowerCase() === c ? 'elegido' : ''}`}
              style={{ background: c }}
              onClick={() => setValores({ ...valores, color: c })}
              aria-label={`Color ${c}`}
            />
          ))}
          <input id="color" name="color" type="color" value={valores.color} onChange={cambiar} aria-label="Otro color" />
        </div>
      </Campo>
      <div className="acciones-formulario">
        <button type="button" className="boton boton-secundario" onClick={alCancelar}>Cancelar</button>
        <button type="submit" className="boton" disabled={enviando}>
          {enviando ? 'Guardando…' : esNueva ? 'Crear asignatura' : 'Guardar cambios'}
        </button>
      </div>
    </form>
  );
}
