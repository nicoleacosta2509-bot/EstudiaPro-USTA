// Cliente para hablar con el backend: agrega el token y traduce los errores

const BASE = import.meta.env.VITE_API_URL || '';
const CLAVE_TOKEN = 'estudiapro_token';

export function leerToken() {
  try {
    return localStorage.getItem(CLAVE_TOKEN);
  } catch {
    return null;
  }
}

export function guardarToken(token) {
  try {
    if (token) localStorage.setItem(CLAVE_TOKEN, token);
    else localStorage.removeItem(CLAVE_TOKEN);
  } catch {
    // Si el navegador bloquea el almacenamiento, la sesión dura solo mientras la página esté abierta
  }
}

// Error con el mensaje para el usuario y los errores por campo
export class ErrorApi extends Error {
  constructor(mensaje, estado, campos = {}) {
    super(mensaje);
    this.estado = estado;
    this.campos = campos;
  }
}

// El AuthContext registra aquí qué hacer cuando el token ya no sirve
let alExpirarSesion = null;
export function alSesionExpirada(funcion) {
  alExpirarSesion = funcion;
}

function mensajePorEstado(estado) {
  if (estado === 401) return 'Tu sesión terminó. Vuelve a iniciar sesión.';
  if (estado === 403) return 'No tienes permiso para hacer esta acción.';
  if (estado === 404) return 'No se encontró el registro. Puede que ya se haya eliminado.';
  if (estado === 409) return 'La acción no se pudo completar por un conflicto con otros datos.';
  if (estado >= 500) return 'Ocurrió un error en el servidor. Intenta de nuevo en un momento.';
  return 'No se pudo completar la acción. Revisa los datos e intenta de nuevo.';
}

async function pedir(metodo, ruta, cuerpo) {
  const headers = {};
  if (cuerpo !== undefined) headers['Content-Type'] = 'application/json';
  const token = leerToken();
  if (token) headers.Authorization = `Bearer ${token}`;

  let respuesta;
  try {
    respuesta = await fetch(`${BASE}/api${ruta}`, {
      method: metodo,
      headers,
      body: cuerpo !== undefined ? JSON.stringify(cuerpo) : undefined,
    });
  } catch {
    throw new ErrorApi('No se pudo conectar con el servidor. Revisa que el backend esté encendido.', 0);
  }

  // Se lee como texto porque algunas respuestas (204) no traen cuerpo
  const texto = await respuesta.text();
  let datos = null;
  if (texto) {
    try {
      datos = JSON.parse(texto);
    } catch {
      datos = null;
    }
  }

  if (!respuesta.ok) {
    // Token vencido o inválido: se cierra la sesión (en el login el 401 es por credenciales malas)
    if (respuesta.status === 401 && token && !ruta.startsWith('/auth/')) {
      guardarToken(null);
      if (alExpirarSesion) alExpirarSesion();
    }
    throw new ErrorApi(datos?.mensaje || mensajePorEstado(respuesta.status), respuesta.status, datos?.campos || {});
  }
  return datos;
}

export const api = {
  get: (ruta) => pedir('GET', ruta),
  post: (ruta, cuerpo) => pedir('POST', ruta, cuerpo ?? {}),
  put: (ruta, cuerpo) => pedir('PUT', ruta, cuerpo ?? {}),
  patch: (ruta, cuerpo) => pedir('PATCH', ruta, cuerpo),
  eliminar: (ruta) => pedir('DELETE', ruta),
};
