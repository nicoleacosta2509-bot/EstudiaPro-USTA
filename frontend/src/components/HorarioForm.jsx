import { useState } from 'react';
import { api } from '../api/cliente';
import { DIAS } from '../utils/constantes';
import { formatearHora, horaAMinutos } from '../utils/fechas';
import Campo from './Campo';

// Formulario de un bloque de clase del horario semanal
export default function HorarioForm({ bloque, asignaturas, alGuardar, alCancelar, alEliminar }) {
  const esNuevo = !bloque?.id;
  const [valores, setValores] = useState({
    asignaturaId: bloque?.asignaturaId ? String(bloque.asignaturaId) : asignaturas[0] ? String(asignaturas[0].id) : '',
    dia: bloque?.dia ?? 'LUNES',
    horaInicio: formatearHora(bloque?.horaInicio) || '07:00',
    horaFin: formatearHora(bloque?.horaFin) || '09:00',
    salon: bloque?.salon ?? '',
  });
  const [errores, setErrores] = useState({});
  const [errorGeneral, setErrorGeneral] = useState('');
  const [enviando, setEnviando] = useState(false);

  function cambiar(e) {
    setValores({ ...valores, [e.target.name]: e.target.value });
  }

  function validar() {
    const nuevos = {};
    if (!valores.asignaturaId) nuevos.asignaturaId = 'Elige la asignatura de la clase.';
    if (!valores.horaInicio) nuevos.horaInicio = 'Indica la hora de inicio.';
    if (!valores.horaFin) nuevos.horaFin = 'Indica la hora de fin.';
    else if (valores.horaInicio && horaAMinutos(valores.horaFin) <= horaAMinutos(valores.horaInicio)) {
      nuevos.horaFin = 'La hora de fin debe ser posterior a la hora de inicio.';
    }
    if (valores.salon.length > 60) nuevos.salon = 'El salón no puede tener más de 60 caracteres.';
    return nuevos;
  }

  async function enviar(e) {
    e.preventDefault();
    const nuevos = validar();
    setErrores(nuevos);
    setErrorGeneral('');
    if (Object.keys(nuevos).length > 0) return;

    const cuerpo = {
      asignaturaId: Number(valores.asignaturaId),
      dia: valores.dia,
      horaInicio: valores.horaInicio,
      horaFin: valores.horaFin,
      salon: valores.salon.trim() || null,
    };

    setEnviando(true);
    try {
      if (esNuevo) await api.post('/horarios', cuerpo);
      else await api.put(`/horarios/${bloque.id}`, cuerpo);
      alGuardar();
    } catch (error) {
      // 409 = se cruza con otra clase
      setErrores(error.campos);
      setErrorGeneral(error.message);
    } finally {
      setEnviando(false);
    }
  }

  return (
    <form onSubmit={enviar} noValidate>
      {errorGeneral && <div className="alerta alerta-error">{errorGeneral}</div>}
      <Campo etiqueta="Asignatura" id="asignaturaId" error={errores.asignaturaId} obligatorio>
        <select id="asignaturaId" name="asignaturaId" value={valores.asignaturaId} onChange={cambiar}>
          <option value="">Elige una asignatura</option>
          {asignaturas.map((a) => (
            <option key={a.id} value={a.id}>{a.nombre}</option>
          ))}
        </select>
      </Campo>
      <div className="fila-campos">
        <Campo etiqueta="Día" id="dia" error={errores.dia} obligatorio>
          <select id="dia" name="dia" value={valores.dia} onChange={cambiar}>
            {DIAS.map((d) => (
              <option key={d.valor} value={d.valor}>{d.etiqueta}</option>
            ))}
          </select>
        </Campo>
        <Campo etiqueta="Salón" id="salon" error={errores.salon}>
          <input id="salon" name="salon" maxLength={60} placeholder="Ej: Aula 302" value={valores.salon} onChange={cambiar} />
        </Campo>
      </div>
      <div className="fila-campos">
        <Campo etiqueta="Hora de inicio" id="horaInicio" error={errores.horaInicio} obligatorio>
          <input id="horaInicio" name="horaInicio" type="time" value={valores.horaInicio} onChange={cambiar} />
        </Campo>
        <Campo etiqueta="Hora de fin" id="horaFin" error={errores.horaFin} obligatorio>
          <input id="horaFin" name="horaFin" type="time" value={valores.horaFin} onChange={cambiar} />
        </Campo>
      </div>
      <div className="acciones-formulario">
        {!esNuevo && (
          <button type="button" className="boton-texto texto-peligro alinear-izquierda" onClick={() => alEliminar(bloque)}>
            Eliminar clase
          </button>
        )}
        <button type="button" className="boton boton-secundario" onClick={alCancelar}>Cancelar</button>
        <button type="submit" className="boton" disabled={enviando}>
          {enviando ? 'Guardando…' : esNuevo ? 'Agregar clase' : 'Guardar cambios'}
        </button>
      </div>
    </form>
  );
}
