import { useState } from 'react';
import { api } from '../api/cliente';
import { useRecordatorios } from '../context/RecordatoriosContext';
import { OPCIONES_RECORDATORIO, PRIORIDADES } from '../utils/constantes';
import { ahoraParaInput, esPasada, paraInput } from '../utils/fechas';
import Campo from './Campo';

// Formulario para crear (tarea = null) o editar una tarea
export default function TareaForm({ tarea, asignaturas, alGuardar, alCancelar }) {
  const esNueva = !tarea;
  const [valores, setValores] = useState({
    titulo: tarea?.titulo ?? '',
    descripcion: tarea?.descripcion ?? '',
    asignaturaId: tarea?.asignaturaId ? String(tarea.asignaturaId) : '',
    fechaEntrega: paraInput(tarea?.fechaEntrega),
    prioridad: tarea?.prioridad ?? 'MEDIA',
    recordatorioMinutos: tarea?.recordatorioMinutos ? String(tarea.recordatorioMinutos) : '',
  });
  const [errores, setErrores] = useState({});
  const [errorGeneral, setErrorGeneral] = useState('');
  const [enviando, setEnviando] = useState(false);
  const { refrescar } = useRecordatorios();

  function cambiar(e) {
    setValores({ ...valores, [e.target.name]: e.target.value });
  }

  // Validación en el navegador antes de enviar (RC-03)
  function validar() {
    const nuevos = {};
    if (!valores.titulo.trim()) nuevos.titulo = 'El título es obligatorio.';
    else if (valores.titulo.length > 120) nuevos.titulo = 'El título no puede tener más de 120 caracteres.';
    if (valores.descripcion.length > 1000) nuevos.descripcion = 'La descripción no puede tener más de 1000 caracteres.';
    if (!valores.fechaEntrega) nuevos.fechaEntrega = 'Indica la fecha y hora de entrega.';
    else if (esNueva && esPasada(valores.fechaEntrega)) nuevos.fechaEntrega = 'La fecha de entrega no puede estar en el pasado.';
    return nuevos;
  }

  async function enviar(e) {
    e.preventDefault();
    const nuevos = validar();
    setErrores(nuevos);
    setErrorGeneral('');
    if (Object.keys(nuevos).length > 0) return;

    const cuerpo = {
      titulo: valores.titulo.trim(),
      descripcion: valores.descripcion.trim() || null,
      asignaturaId: valores.asignaturaId ? Number(valores.asignaturaId) : null,
      fechaEntrega: valores.fechaEntrega,
      prioridad: valores.prioridad,
      recordatorioMinutos: valores.recordatorioMinutos ? Number(valores.recordatorioMinutos) : null,
    };

    setEnviando(true);
    try {
      if (esNueva) await api.post('/tareas', cuerpo);
      else await api.put(`/tareas/${tarea.id}`, cuerpo);
      refrescar();
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
      <Campo etiqueta="Título" id="titulo" error={errores.titulo} obligatorio>
        <input id="titulo" name="titulo" maxLength={120} value={valores.titulo} onChange={cambiar} autoFocus />
      </Campo>
      <Campo etiqueta="Descripción" id="descripcion" error={errores.descripcion}>
        <textarea id="descripcion" name="descripcion" rows={3} maxLength={1000} value={valores.descripcion} onChange={cambiar} />
      </Campo>
      <div className="fila-campos">
        <Campo etiqueta="Asignatura" id="asignaturaId" error={errores.asignaturaId}>
          <select id="asignaturaId" name="asignaturaId" value={valores.asignaturaId} onChange={cambiar}>
            <option value="">Sin asignatura</option>
            {asignaturas.map((a) => (
              <option key={a.id} value={a.id}>{a.nombre}</option>
            ))}
          </select>
        </Campo>
        <Campo etiqueta="Prioridad" id="prioridad" error={errores.prioridad}>
          <select id="prioridad" name="prioridad" value={valores.prioridad} onChange={cambiar}>
            {PRIORIDADES.map((p) => (
              <option key={p.valor} value={p.valor}>{p.etiqueta}</option>
            ))}
          </select>
        </Campo>
      </div>
      <div className="fila-campos">
        <Campo etiqueta="Fecha de entrega" id="fechaEntrega" error={errores.fechaEntrega} obligatorio>
          <input
            id="fechaEntrega"
            name="fechaEntrega"
            type="datetime-local"
            min={esNueva ? ahoraParaInput() : undefined}
            value={valores.fechaEntrega}
            onChange={cambiar}
          />
        </Campo>
        <Campo etiqueta="Recordatorio" id="recordatorioMinutos" error={errores.recordatorioMinutos}>
          <select id="recordatorioMinutos" name="recordatorioMinutos" value={valores.recordatorioMinutos} onChange={cambiar}>
            {OPCIONES_RECORDATORIO.map((o) => (
              <option key={o.valor} value={o.valor}>{o.etiqueta}</option>
            ))}
          </select>
        </Campo>
      </div>
      <div className="acciones-formulario">
        <button type="button" className="boton boton-secundario" onClick={alCancelar}>Cancelar</button>
        <button type="submit" className="boton" disabled={enviando}>
          {enviando ? 'Guardando…' : esNueva ? 'Crear tarea' : 'Guardar cambios'}
        </button>
      </div>
    </form>
  );
}
