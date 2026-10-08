import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api/cliente';
import { useAuth } from '../context/AuthContext';
import { ESTADOS_EXAMEN, ESTADOS_TAREA, etiquetaDe } from '../utils/constantes';
import { formatearFechaHora, formatearFechaLarga, formatearHora, tiempoRelativo } from '../utils/fechas';

export default function Panel() {
  const { usuario } = useAuth();
  const [panel, setPanel] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    api.get('/panel').then(setPanel).catch((e) => setError(e.message));
  }, []);

  if (error) return <div className="alerta alerta-error">{error}</div>;
  if (!panel) return <p className="texto-suave">Cargando tu resumen…</p>;

  const primerNombre = usuario.nombre.split(' ')[0];

  return (
    <>
      <div className="encabezado-pagina">
        <div>
          <h1>Hola, {primerNombre}</h1>
          <p className="texto-suave primera-mayuscula">{formatearFechaLarga()}</p>
        </div>
      </div>

      <section className="resumen">
        <Tarjeta titulo="Tareas pendientes" valor={panel.tareasPendientes} enlace="/tareas" />
        <Tarjeta titulo="En proceso" valor={panel.tareasEnProceso} enlace="/tareas" />
        <Tarjeta titulo="Vencidas" valor={panel.tareasVencidas} enlace="/tareas" alerta={panel.tareasVencidas > 0} />
        <Tarjeta titulo="Exámenes (14 días)" valor={panel.examenesProximos} enlace="/examenes" />
      </section>

      <section className="tarjeta">
        <div className="fila-separada">
          <h2>Avance de tareas</h2>
          <strong>{panel.avance}%</strong>
        </div>
        <div className="barra-avance" role="progressbar" aria-valuenow={panel.avance} aria-valuemin={0} aria-valuemax={100}>
          <div style={{ width: `${panel.avance}%` }} />
        </div>
        <p className="texto-suave">{panel.tareasTerminadas} {panel.tareasTerminadas === 1 ? "tarea terminada" : "tareas terminadas"}.</p>
      </section>

      <div className="dos-columnas">
        <section className="tarjeta">
          <h2>Próximos 7 días</h2>
          {panel.proximas.length === 0 && <p className="texto-suave">No tienes entregas ni exámenes esta semana.</p>}
          <ul className="lista-simple">
            {panel.proximas.map((p) => (
              <li key={`${p.tipo}-${p.id}`}>
                <span className="punto-color" style={{ background: p.color || 'var(--borde-fuerte)' }} />
                <div className="crece">
                  <span className={`insignia insignia-${p.tipo === 'EXAMEN' ? 'examen' : 'tarea'}`}>
                    {p.tipo === 'EXAMEN' ? 'Examen' : 'Tarea'}
                  </span>
                  <strong>{p.titulo}</strong>
                  <p className="texto-suave">
                    {p.asignaturaNombre ? `${p.asignaturaNombre} · ` : ''}
                    {formatearFechaHora(p.fecha)} ({tiempoRelativo(p.fecha)})
                  </p>
                </div>
                <span className="texto-suave texto-pequeno">
                  {etiquetaDe(p.tipo === 'EXAMEN' ? ESTADOS_EXAMEN : ESTADOS_TAREA, p.estado)}
                </span>
              </li>
            ))}
          </ul>
        </section>

        <section className="tarjeta">
          <h2>Clases de hoy</h2>
          {panel.clasesHoy.length === 0 && <p className="texto-suave">Hoy no tienes clases registradas.</p>}
          <ul className="lista-simple">
            {panel.clasesHoy.map((c) => (
              <li key={c.id}>
                <span className="punto-color" style={{ background: c.color }} />
                <div className="crece">
                  <strong>{c.asignaturaNombre}</strong>
                  <p className="texto-suave">
                    {formatearHora(c.horaInicio)} – {formatearHora(c.horaFin)}
                    {c.salon ? ` · ${c.salon}` : ''}
                  </p>
                </div>
              </li>
            ))}
          </ul>
          <Link to="/horario" className="enlace-pie">Ver horario completo</Link>
        </section>
      </div>
    </>
  );
}

function Tarjeta({ titulo, valor, enlace, alerta }) {
  return (
    <Link to={enlace} className={`tarjeta tarjeta-resumen ${alerta ? 'tarjeta-alerta' : ''}`}>
      <span className="texto-suave">{titulo}</span>
      <strong>{valor}</strong>
    </Link>
  );
}
