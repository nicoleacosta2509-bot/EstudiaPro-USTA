import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api/cliente';
import ConfirmarModal from '../components/ConfirmarModal';
import HorarioForm from '../components/HorarioForm';
import Modal from '../components/Modal';
import { DIAS } from '../utils/constantes';
import { formatearHora, horaAMinutos } from '../utils/fechas';

const ALTO_HORA = 52; // píxeles que ocupa una hora en la cuadrícula

export default function Horario() {
  const [bloques, setBloques] = useState([]);
  const [asignaturas, setAsignaturas] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const [editando, setEditando] = useState(undefined); // undefined = cerrado, null = nuevo
  const [porEliminar, setPorEliminar] = useState(null);

  const cargar = useCallback(async () => {
    try {
      const [listaBloques, listaAsignaturas] = await Promise.all([api.get('/horarios'), api.get('/asignaturas')]);
      setBloques(listaBloques);
      setAsignaturas(listaAsignaturas);
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
    await api.eliminar(`/horarios/${porEliminar.id}`);
    setEditando(undefined);
    await cargar();
  }

  // Domingo solo aparece si hay clases ese día
  const dias = DIAS.filter((d) => d.valor !== 'DOMINGO' || bloques.some((b) => b.dia === 'DOMINGO'));

  // Rango de horas: por defecto 7 a 18, se amplía si hay clases antes o después
  let horaInicial = 7;
  let horaFinal = 18;
  bloques.forEach((b) => {
    horaInicial = Math.min(horaInicial, Math.floor(horaAMinutos(b.horaInicio) / 60));
    horaFinal = Math.max(horaFinal, Math.ceil(horaAMinutos(b.horaFin) / 60));
  });
  const horas = [];
  for (let h = horaInicial; h < horaFinal; h++) horas.push(h);

  return (
    <>
      <div className="encabezado-pagina">
        <h1>Horario</h1>
        <button type="button" className="boton" onClick={() => setEditando(null)} disabled={asignaturas.length === 0}>
          + Agregar clase
        </button>
      </div>

      {error && <div className="alerta alerta-error">{error}</div>}
      {!cargando && asignaturas.length === 0 && (
        <div className="alerta alerta-info">
          Para armar tu horario primero registra tus materias en <Link to="/asignaturas">Asignaturas</Link>.
        </div>
      )}
      {cargando && <p className="texto-suave">Cargando horario…</p>}

      {!cargando && (
        <>
          {/* Cuadrícula semanal (pantallas medianas y grandes) */}
          <div className="horario-contenedor">
            <div className="horario" style={{ gridTemplateColumns: `56px repeat(${dias.length}, minmax(110px, 1fr))` }}>
              <div className="horario-esquina" />
              {dias.map((d) => (
                <div key={d.valor} className="horario-dia">{d.etiqueta}</div>
              ))}

              <div className="horario-horas" style={{ height: horas.length * ALTO_HORA }}>
                {horas.map((h) => (
                  <span key={h} style={{ top: (h - horaInicial) * ALTO_HORA }}>{`${String(h).padStart(2, '0')}:00`}</span>
                ))}
              </div>
              {dias.map((d) => (
                <div key={d.valor} className="horario-columna" style={{ height: horas.length * ALTO_HORA, backgroundSize: `100% ${ALTO_HORA}px` }}>
                  {bloques
                    .filter((b) => b.dia === d.valor)
                    .map((b) => {
                      const inicio = horaAMinutos(b.horaInicio) - horaInicial * 60;
                      const duracion = horaAMinutos(b.horaFin) - horaAMinutos(b.horaInicio);
                      return (
                        <button
                          key={b.id}
                          type="button"
                          className="bloque-clase"
                          style={{ top: (inicio / 60) * ALTO_HORA, height: (duracion / 60) * ALTO_HORA - 2, background: b.color }}
                          onClick={() => setEditando(b)}
                          title={`${b.asignaturaNombre} ${formatearHora(b.horaInicio)}–${formatearHora(b.horaFin)}`}
                        >
                          <strong>{b.asignaturaNombre}</strong>
                          <span>{formatearHora(b.horaInicio)}–{formatearHora(b.horaFin)}</span>
                          {b.salon && <span>{b.salon}</span>}
                        </button>
                      );
                    })}
                </div>
              ))}
            </div>
          </div>

          {/* Lista por día (celular) */}
          <div className="horario-lista">
            {bloques.length === 0 && <div className="vacio">Aún no tienes clases en el horario.</div>}
            {dias.map((d) => {
              const delDia = bloques.filter((b) => b.dia === d.valor);
              if (delDia.length === 0) return null;
              return (
                <section key={d.valor} className="tarjeta">
                  <h2>{d.etiqueta}</h2>
                  <ul className="lista-simple">
                    {delDia.map((b) => (
                      <li key={b.id} onClick={() => setEditando(b)} className="clicable">
                        <span className="punto-color" style={{ background: b.color }} />
                        <div className="crece">
                          <strong>{b.asignaturaNombre}</strong>
                          <p className="texto-suave">
                            {formatearHora(b.horaInicio)} – {formatearHora(b.horaFin)}
                            {b.salon ? ` · ${b.salon}` : ''}
                          </p>
                        </div>
                      </li>
                    ))}
                  </ul>
                </section>
              );
            })}
          </div>
        </>
      )}

      <Modal titulo={editando ? 'Editar clase' : 'Agregar clase'} abierto={editando !== undefined} alCerrar={() => setEditando(undefined)}>
        <HorarioForm
          bloque={editando}
          asignaturas={asignaturas}
          alGuardar={() => {
            setEditando(undefined);
            cargar();
          }}
          alCancelar={() => setEditando(undefined)}
          alEliminar={setPorEliminar}
        />
      </Modal>

      <ConfirmarModal
        abierto={porEliminar !== null}
        titulo="Eliminar clase"
        mensaje={`¿Quitar ${porEliminar?.asignaturaNombre} del horario?`}
        alConfirmar={eliminar}
        alCancelar={() => setPorEliminar(null)}
      />
    </>
  );
}
