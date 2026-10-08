import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Campo from '../components/Campo';
import { useAuth } from '../context/AuthContext';

const PATRON_EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export default function Registro() {
  const { registro } = useAuth();
  const navegar = useNavigate();
  const [valores, setValores] = useState({ nombre: '', email: '', password: '', confirmar: '' });
  const [errores, setErrores] = useState({});
  const [errorGeneral, setErrorGeneral] = useState('');
  const [enviando, setEnviando] = useState(false);

  function cambiar(e) {
    setValores({ ...valores, [e.target.name]: e.target.value });
  }

  function validar() {
    const nuevos = {};
    if (!valores.nombre.trim()) nuevos.nombre = 'Escribe tu nombre.';
    if (!valores.email.trim()) nuevos.email = 'Escribe tu correo.';
    else if (!PATRON_EMAIL.test(valores.email.trim())) nuevos.email = 'El correo no tiene un formato válido (ejemplo: nombre@correo.com).';
    if (valores.password.length < 6) nuevos.password = 'La contraseña debe tener al menos 6 caracteres.';
    if (valores.confirmar !== valores.password) nuevos.confirmar = 'Las contraseñas no coinciden.';
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
      await registro(valores.nombre.trim(), valores.email.trim(), valores.password);
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
        <h1 className="titulo-acceso">Crear cuenta</h1>
        {errorGeneral && <div className="alerta alerta-error">{errorGeneral}</div>}
        <Campo etiqueta="Nombre" id="nombre" error={errores.nombre} obligatorio>
          <input id="nombre" name="nombre" autoComplete="name" maxLength={80} value={valores.nombre} onChange={cambiar} />
        </Campo>
        <Campo etiqueta="Correo" id="email" error={errores.email} obligatorio>
          <input id="email" name="email" type="email" autoComplete="email" value={valores.email} onChange={cambiar} />
        </Campo>
        <Campo etiqueta="Contraseña" id="password" error={errores.password} ayuda="Mínimo 6 caracteres." obligatorio>
          <input id="password" name="password" type="password" autoComplete="new-password" value={valores.password} onChange={cambiar} />
        </Campo>
        <Campo etiqueta="Confirmar contraseña" id="confirmar" error={errores.confirmar} obligatorio>
          <input id="confirmar" name="confirmar" type="password" autoComplete="new-password" value={valores.confirmar} onChange={cambiar} />
        </Campo>
        <button type="submit" className="boton ancho-completo" disabled={enviando}>
          {enviando ? 'Creando cuenta…' : 'Registrarme'}
        </button>
        <p className="texto-centro">
          ¿Ya tienes cuenta? <Link to="/login">Inicia sesión</Link>
        </p>
      </form>
    </div>
  );
}
