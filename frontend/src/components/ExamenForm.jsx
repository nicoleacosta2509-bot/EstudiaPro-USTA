import { useState } from 'react';
import { api } from '../api/cliente';
import { useRecordatorios } from '../context/RecordatoriosContext';
import { OPCIONES_RECORDATORIO } from '../utils/constantes';
import { ahoraParaInput, esPasada, paraInput } from '../utils/fechas';
import Campo from './Campo';

// Formulario para crear (examen = null) o editar un examen
export default function ExamenForm({ examen, asignaturas, alGuardar, alCancelar }) {
  const esNuevo = !examen;
  const [valores, setValores] = useState({
    titulo: examen?.titulo ?? '',
    temas: examen?.temas ?? '',
    asignaturaId: examen?.asignaturaId ? String(examen.asignaturaId) : '',
    fecha: paraInput(examen?.fecha),
    lugar: examen?.lugar ?? '',
    recordatorioMinutos: examen?.recordatorioMinutos ? String(examen.recordatorioMinutos) : '',
  });
  const [errores, setErrores] = useState({});
  const [errorGeneral, setErrorGeneral] = useState('');
  const [enviando, setEnviando] = useState(false);
  const { refrescar } = useRecordatorios();

  function cambiar(e) {
    setValores({ ...valores, [e.target.name]: e.target.value });
  }

  function validar() {
    const nuevos = {};
    if (!valores.titulo.trim()) nuevos.titulo = 'El título es obligatorio (por ejemplo, «Parcial 2»).';
    else if (valores.titulo.length > 120) nuevos.titulo = 'El título no puede tener más de 120 caracteres.';
    if (valores.temas.length > 1000) nuevos.temas = 'Los temas no pueden tener más de 1000 caracteres.';
    if (valores.lugar.length > 120) nuevos.lugar = 'El lugar no puede tener más de 120 caracteres.';
    if (!valores.fecha) nuevos.fecha = 'Indica la fecha y hora del examen.';
    else if (esNuevo && esPasada(valores.fecha)) nuevos.fecha = 'La fecha del examen no puede estar en el pasado.';
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
      temas: valores.temas.trim() || null,
      asignaturaId: valores.asignaturaId ? Number(valores.asignaturaId) : null,
      fecha: valores.fecha,
      lugar: valores.lugar.trim() || null,
      recordatorioMinutos: valores.recordatorioMinutos ? Number(valores.recordatorioMinutos) : null,
    };

    setEnviando(true);
    try {
      if (esNuevo) await api.post('/examenes', cuerpo);
      else await api.put(`/examenes/${examen.id}`, cuerpo);
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
      <Campo etiqueta="Temas" id="temas" error={errores.temas} ayuda="Qué entra en el examen.">
        <textarea id="temas" name="temas" rows={3} maxLength={1000} value={valores.temas} onChange={cambiar} />
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
        <Campo etiqueta="Lugar" id="lugar" error={errores.lugar}>
          <input id="lugar" name="lugar" maxLength={120} placeholder="Salón, edificio…" value={valores.lugar} onChange={cambiar} />
        </Campo>
      </div>
      <div className="fila-campos">
        <Campo etiqueta="Fecha y hora" id="fecha" error={errores.fecha} obligatorio>
          <input id="fecha" name="fecha" type="datetime-local" min={esNuevo ? ahoraParaInput() : undefined} value={valores.fecha} onChange={cambiar} />
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
          {enviando ? 'Guardando…' : esNuevo ? 'Crear examen' : 'Guardar cambios'}
        </button>
      </div>
    </form>
  );
}
