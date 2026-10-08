import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Campo from '../components/Campo';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const { login } = useAuth();
  const navegar = useNavigate();
  const [valores, setValores] = useState({ email: '', password: '' });
  const [errores, setErrores] = useState({});
  const [errorGeneral, setErrorGeneral] = useState('');
  const [enviando, setEnviando] = useState(false);

  function cambiar(e) {
    setValores({ ...valores, [e.target.name]: e.target.value });
  }

  function validar() {
    const nuevos = {};
    if (!valores.email.trim()) nuevos.email = 'Escribe tu correo.';
    if (!valores.password) nuevos.password = 'Escribe tu contraseña.';
    return nuevos;
  }

  async function enviar(e) {
    e.preventDefault();
    const nuevos = validar();
    setErrores(nuevos);
    setErrorGeneral('');
    if (Object.keys(nuevos).length > 0) return;

    setEnviando(true);
    try {
      await login(valores.email.trim(), valores.password);
      navegar('/');
    } catch (error) {
      setErrores(error.campos);
      setErrorGeneral(error.message);
    } finally {
      setEnviando(false);
    }
  }

  return (
    <div className="pantalla-acceso">
      <form className="tarjeta-acceso" onSubmit={enviar} noValidate>
        <div className="marca marca-grande">
          <span className="marca-logo">E</span>
          <span>EstudiaPro</span>
        </div>
        <p className="texto-suave">Organiza tus tareas, exámenes y horarios en un solo lugar.</p>
        {errorGeneral && <div className="alerta alerta-error">{errorGeneral}</div>}
        <Campo etiqueta="Correo" id="email" error={errores.email}>
          <input id="email" name="email" type="email" autoComplete="email" value={valores.email} onChange={cambiar} />
        </Campo>
        <Campo etiqueta="Contraseña" id="password" error={errores.password}>
          <input id="password" name="password" type="password" autoComplete="current-password" value={valores.password} onChange={cambiar} />
        </Campo>
        <button type="submit" className="boton ancho-completo" disabled={enviando}>
          {enviando ? 'Ingresando…' : 'Iniciar sesión'}
        </button>
        <p className="texto-centro">
          ¿No tienes cuenta? <Link to="/registro">Regístrate</Link>
        </p>
      </form>
    </div>
  );
}
