package co.edu.usta.estudiapro.comun;

// 400: un campo pasa la validacion basica pero no tiene sentido (fecha pasada, hora fin antes de inicio...)
public class DatoInvalidoException extends RuntimeException {

  private final String campo;

  public DatoInvalidoException(String campo, String mensaje) {
    super(mensaje);
    this.campo = campo;
  }

  public String getCampo() {
    return campo;
  }
}
