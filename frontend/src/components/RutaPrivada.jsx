import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

// Solo deja pasar si hay sesión; si no, manda al login
export default function RutaPrivada({ children }) {
  const { usuario, cargando } = useAuth();
  if (cargando) return <div className="cargando-pantalla">Cargando…</div>;
  if (!usuario) return <Navigate to="/login" replace />;
  return children;
}
