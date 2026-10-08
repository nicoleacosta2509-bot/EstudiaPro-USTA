// Etiqueta + control + mensaje de error debajo (RC-01: el error dice qué corregir)
export default function Campo({ etiqueta, id, error, ayuda, obligatorio, children }) {
  return (
    <div className={`campo ${error ? 'campo-con-error' : ''}`}>
      <label htmlFor={id}>
        {etiqueta}
        {obligatorio && <span className="obligatorio"> *</span>}
      </label>
      {children}
      {error ? <p className="campo-error">{error}</p> : ayuda && <p className="campo-ayuda">{ayuda}</p>}
    </div>
  );
}
