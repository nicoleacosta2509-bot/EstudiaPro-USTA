package co.edu.usta.estudiapro.comun;

import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

// Convierte las excepciones en respuestas claras para el usuario (RC-01)
@RestControllerAdvice
public class ManejadorErrores {

  private static final Logger log = LoggerFactory.getLogger(ManejadorErrores.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> validacion(MethodArgumentNotValidException ex) {
    Map<String, String> campos = new LinkedHashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(e -> campos.putIfAbsent(e.getField(), e.getDefaultMessage()));
    return ResponseEntity.badRequest()
        .body(new ApiError("Revisa los campos marcados antes de guardar.", campos));
  }

  @ExceptionHandler(DatoInvalidoException.class)
  public ResponseEntity<ApiError> datoInvalido(DatoInvalidoException ex) {
    return ResponseEntity.badRequest()
        .body(new ApiError(ex.getMessage(), Map.of(ex.getCampo(), ex.getMessage())));
  }

  // JSON mal formado, fecha con formato incorrecto o un valor que no está en la lista
  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    MethodArgumentTypeMismatchException.class
  })
  public ResponseEntity<ApiError> formato(Exception ex) {
    return ResponseEntity.badRequest()
        .body(ApiError.de("Algún dato tiene un formato inválido. Revisa fechas, horas y opciones."));
  }

  @ExceptionHandler(NoEncontradoException.class)
  public ResponseEntity<ApiError> noEncontrado(NoEncontradoException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError.de(ex.getMessage()));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ApiError> rutaInexistente(NoResourceFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError.de("La ruta no existe."));
  }

  @ExceptionHandler(ConflictoException.class)
  public ResponseEntity<ApiError> conflicto(ConflictoException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.de(ex.getMessage()));
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiError> credenciales(BadCredentialsException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiError.de("Correo o contraseña incorrectos."));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> inesperado(Exception ex) {
    log.error("Error no controlado", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiError.de("Ocurrió un error inesperado. Intenta de nuevo."));
  }
}
