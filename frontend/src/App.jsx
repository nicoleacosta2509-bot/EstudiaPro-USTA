import { Navigate, Route, Routes } from 'react-router-dom';
import Layout from './components/Layout';
import RutaPrivada from './components/RutaPrivada';
import { useAuth } from './context/AuthContext';
import Asignaturas from './pages/Asignaturas';
import Examenes from './pages/Examenes';
import Horario from './pages/Horario';
import Login from './pages/Login';
import Panel from './pages/Panel';
import Recordatorios from './pages/Recordatorios';
import Registro from './pages/Registro';
import Tareas from './pages/Tareas';

// Login y registro no se muestran si ya hay sesión
function SoloSinSesion({ children }) {
  const { usuario, cargando } = useAuth();
  if (cargando) return <div className="cargando-pantalla">Cargando…</div>;
  return usuario ? <Navigate to="/" replace /> : children;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<SoloSinSesion><Login /></SoloSinSesion>} />
      <Route path="/registro" element={<SoloSinSesion><Registro /></SoloSinSesion>} />
      <Route path="/" element={<RutaPrivada><Layout /></RutaPrivada>}>
        <Route index element={<Panel />} />
        <Route path="tareas" element={<Tareas />} />
        <Route path="examenes" element={<Examenes />} />
        <Route path="horario" element={<Horario />} />
        <Route path="asignaturas" element={<Asignaturas />} />
        <Route path="recordatorios" element={<Recordatorios />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
