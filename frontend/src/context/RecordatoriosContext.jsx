import { createContext, useCallback, useContext, useEffect, useRef, useState } from 'react';
import { api } from '../api/cliente';
import { formatearFechaHora } from '../utils/fechas';

const RecordatoriosContext = createContext(null);
const INTERVALO_MS = 60000;

function permisoActual() {
  return 'Notification' in window ? Notification.permission : 'no-soportado';
}

// Aviso del sistema operativo, solo si el usuario dio permiso
function notificarNavegador(recordatorio) {
  if (permisoActual() !== 'granted') return;
  try {
    const tipo = recordatorio.tipo === 'EXAMEN' ? 'Examen' : 'Tarea';
    new Notification(`EstudiaPro: ${tipo}`, {
      body: `${recordatorio.titulo} (${formatearFechaHora(recordatorio.fechaEvento)})`,
      tag: `recordatorio-${recordatorio.id}`,
    });
  } catch {
    // Algunos navegadores no permiten crear notificaciones fuera de un service worker
  }
}

// Consulta cada minuto los recordatorios que ya toca avisar
export function RecordatoriosProvider({ children }) {
  const [pendientes, setPendientes] = useState([]);
  const [avisos, setAvisos] = useState([]);
  const [permiso, setPermiso] = useState(permisoActual);
  const vistos = useRef(new Set());

  const consultar = useCallback(async () => {
    try {
      const lista = await api.get('/recordatorios/pendientes');
      setPendientes(lista);
      const nuevos = lista.filter((r) => !vistos.current.has(r.id));
      nuevos.forEach((r) => {
        vistos.current.add(r.id);
        notificarNavegador(r);
      });
      if (nuevos.length > 0) setAvisos((anteriores) => [...anteriores, ...nuevos]);
    } catch {
      // Si falla la consulta se vuelve a intentar en el siguiente ciclo
    }
  }, []);

  useEffect(() => {
    consultar();
    const id = setInterval(consultar, INTERVALO_MS);
    return () => clearInterval(id);
  }, [consultar]);

  async function atender(id) {
    await api.patch(`/recordatorios/${id}/atendido`);
    setPendientes((lista) => lista.filter((r) => r.id !== id));
    setAvisos((lista) => lista.filter((r) => r.id !== id));
  }

  // Cierra el aviso flotante pero el recordatorio sigue en la campana
  function cerrarAviso(id) {
    setAvisos((lista) => lista.filter((r) => r.id !== id));
  }

  async function pedirPermiso() {
    if (!('Notification' in window)) return;
    const resultado = await Notification.requestPermission();
    setPermiso(resultado);
  }

  return (
    <RecordatoriosContext.Provider
      value={{ pendientes, avisos, permiso, atender, cerrarAviso, pedirPermiso, refrescar: consultar }}
    >
      {children}
    </RecordatoriosContext.Provider>
  );
}

export function useRecordatorios() {
  return useContext(RecordatoriosContext);
}
