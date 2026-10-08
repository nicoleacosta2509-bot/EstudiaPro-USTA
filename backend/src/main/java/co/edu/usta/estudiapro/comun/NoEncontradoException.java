package co.edu.usta.estudiapro.comun;

// 404: el registro no existe o no pertenece al estudiante que consulta
public class NoEncontradoException extends RuntimeException {

  public NoEncontradoException(String mensaje) {
    super(mensaje);
  }
}
