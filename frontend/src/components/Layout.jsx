import { useEffect, useRef, useState } from 'react';
import { NavLink, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { RecordatoriosProvider, useRecordatorios } from '../context/RecordatoriosContext';
import { formatearFechaHora } from '../utils/fechas';
import {
  IconoAsignatura,
  IconoCampana,
  IconoExamen,
  IconoHorario,
  IconoInicio,
  IconoMenu,
  IconoSalir,
  IconoTareas,
} from './Iconos';

const ENLACES = [
  { ruta: '/', texto: 'Inicio', icono: IconoInicio },
  { ruta: '/tareas', texto: 'Tareas', icono: IconoTareas },
  { ruta: '/examenes', texto: 'Exámenes', icono: IconoExamen },
  { ruta: '/horario', texto: 'Horario', icono: IconoHorario },
  { ruta: '/asignaturas', texto: 'Asignaturas', icono: IconoAsignatura },
  { ruta: '/recordatorios', texto: 'Recordatorios', icono: IconoCampana },
];

export default function Layout() {
  return (
    <RecordatoriosProvider>
      <Estructura />
    </RecordatoriosProvider>
  );
}

function Estructura() {
  const { usuario, salir } = useAuth();
  const [menuAbierto, setMenuAbierto] = useState(false);
  const ubicacion = useLocation();

  // En celular el menú se cierra al cambiar de página
  useEffect(() => setMenuAbierto(false), [ubicacion.pathname]);

  return (
    <div className="app">
      <aside className={`barra-lateral ${menuAbierto ? 'abierta' : ''}`}>
        <div className="marca">
          <span className="marca-logo">E</span>
          <span>EstudiaPro</span>
        </div>
        <nav>
          {ENLACES.map(({ ruta, texto, icono: Icono }) => (
            <NavLink key={ruta} to={ruta} end={ruta === '/'} className="enlace-menu">
              <Icono />
              <span>{texto}</span>
            </NavLink>
          ))}
        </nav>
      </aside>
      {menuAbierto && <div className="velo" onClick={() => setMenuAbierto(false)} />}

      <div className="contenido">
        <header className="cabecera">
          <button type="button" className="boton-icono solo-movil" onClick={() => setMenuAbierto(true)} aria-label="Abrir menú">
            <IconoMenu />
          </button>
          <div className="cabecera-espacio" />
          <Campana />
          <span className="nombre-usuario" title={usuario.email}>
            {usuario.nombre}
          </span>
          <button type="button" className="boton boton-secundario boton-pequeno" onClick={salir}>
            <IconoSalir />
            <span className="ocultar-movil">Salir</span>
          </button>
        </header>
        <main className="pagina">
          <Outlet />
        </main>
      </div>
      <AvisosFlotantes />
    </div>
  );
}

// Campana del header con los recordatorios que ya toca atender
function Campana() {
  const { pendientes, atender, permiso, pedirPermiso } = useRecordatorios();
  const [abierta, setAbierta] = useState(false);
  const contenedor = useRef(null);

  useEffect(() => {
    if (!abierta) return undefined;
    function clicFuera(e) {
      if (contenedor.current && !contenedor.current.contains(e.target)) setAbierta(false);
    }
    document.addEventListener('mousedown', clicFuera);
    return () => document.removeEventListener('mousedown', clicFuera);
  }, [abierta]);

  return (
    <div className="campana" ref={contenedor}>
      <button
        type="button"
        className="boton-icono"
        onClick={() => setAbierta(!abierta)}
        aria-label={`Recordatorios (${pendientes.length} por atender)`}
      >
        <IconoCampana />
        {pendientes.length > 0 && <span className="contador">{pendientes.length}</span>}
      </button>
      {abierta && (
        <div className="desplegable">
          <h3>Recordatorios</h3>
          {pendientes.length === 0 && <p className="texto-suave">No tienes recordatorios por atender.</p>}
          {pendientes.map((r) => (
            <div key={r.id} className="item-recordatorio">
              <div>
                <span className={`insignia insignia-${r.tipo === 'EXAMEN' ? 'examen' : 'tarea'}`}>
                  {r.tipo === 'EXAMEN' ? 'Examen' : 'Tarea'}
                </span>
                <strong>{r.titulo}</strong>
                <p className="texto-suave">{formatearFechaHora(r.fechaEvento)}</p>
              </div>
              <button type="button" className="boton boton-pequeno" onClick={() => atender(r.id)}>
                Entendido
              </button>
            </div>
          ))}
          {permiso === 'default' && (
            <button type="button" className="boton boton-secundario boton-pequeno ancho-completo" onClick={pedirPermiso}>
              Activar avisos del navegador
            </button>
          )}
          <NavLink to="/recordatorios" className="enlace-pie" onClick={() => setAbierta(false)}>
            Ver próximos recordatorios
          </NavLink>
        </div>
      )}
    </div>
  );
}

// Avisos que aparecen en la esquina cuando llega un recordatorio nuevo
function AvisosFlotantes() {
  const { avisos, atender, cerrarAviso } = useRecordatorios();
  if (avisos.length === 0) return null;
  return (
    <div className="avisos" role="status">
      {avisos.map((r) => (
        <div key={r.id} className="aviso">
          <div>
            <strong>{r.tipo === 'EXAMEN' ? 'Examen próximo' : 'Tarea próxima'}</strong>
            <p>{r.titulo}</p>
            <p className="texto-suave">{formatearFechaHora(r.fechaEvento)}</p>
          </div>
          <div className="aviso-acciones">
            <button type="button" className="boton boton-pequeno" onClick={() => atender(r.id)}>
              Entendido
            </button>
            <button type="button" className="boton-icono" onClick={() => cerrarAviso(r.id)} aria-label="Cerrar aviso">
              ×
            </button>
          </div>
        </div>
      ))}
    </div>
  );
}
