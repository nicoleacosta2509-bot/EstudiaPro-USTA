import { createContext, useCallback, useContext, useEffect, useState } from 'react';
import { alSesionExpirada, api, guardarToken, leerToken } from '../api/cliente';

const AuthContext = createContext(null);

// Guarda quién inició sesión y ofrece login, registro y salir
export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(null);
  const [cargando, setCargando] = useState(true);

  const salir = useCallback(() => {
    guardarToken(null);
    setUsuario(null);
  }, []);

  useEffect(() => {
    // Si el backend responde 401 se cierra la sesión y RutaPrivada manda a /login
    alSesionExpirada(() => setUsuario(null));

    // Al abrir la app, si hay token guardado se pregunta quién es el usuario
    if (!leerToken()) {
      setCargando(false);
      return;
    }
    api
      .get('/auth/yo')
      .then(setUsuario)
      .catch(() => guardarToken(null))
      .finally(() => setCargando(false));
  }, []);

  async function login(email, password) {
    const respuesta = await api.post('/auth/login', { email, password });
    guardarToken(respuesta.token);
    setUsuario(respuesta.usuario);
  }

  async function registro(nombre, email, password) {
    const respuesta = await api.post('/auth/registro', { nombre, email, password });
    guardarToken(respuesta.token);
    setUsuario(respuesta.usuario);
  }

  return (
    <AuthContext.Provider value={{ usuario, cargando, login, registro, salir }}>{children}</AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
