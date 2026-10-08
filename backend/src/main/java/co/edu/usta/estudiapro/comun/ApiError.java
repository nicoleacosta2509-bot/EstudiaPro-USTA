package co.edu.usta.estudiapro.comun;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

// Forma unica de los errores: un mensaje para el usuario y, si aplica, el error de cada campo
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(String mensaje, Map<String, String> campos) {

  public static ApiError de(String mensaje) {
    return new ApiError(mensaje, null);
  }
}
