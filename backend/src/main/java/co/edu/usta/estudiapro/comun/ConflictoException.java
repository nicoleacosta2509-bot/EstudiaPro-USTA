package co.edu.usta.estudiapro.comun;

// 409: la operacion choca con datos que ya existen (correo repetido, clases que se cruzan...)
public class ConflictoException extends RuntimeException {

  public ConflictoException(String mensaje) {
    super(mensaje);
  }
}
