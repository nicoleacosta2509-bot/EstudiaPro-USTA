package co.edu.usta.estudiapro.asignatura;

import co.edu.usta.estudiapro.asignatura.AsignaturaDtos.AsignaturaDto;
import co.edu.usta.estudiapro.asignatura.AsignaturaDtos.AsignaturaRequest;
import co.edu.usta.estudiapro.seguridad.UsuarioActual;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/asignaturas")
public class AsignaturaController {

  private final AsignaturaService servicio;

  public AsignaturaController(AsignaturaService servicio) {
    this.servicio = servicio;
  }

  @GetMapping
  public List<AsignaturaDto> listar() {
    return servicio.listar(UsuarioActual.id());
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AsignaturaDto crear(@Valid @RequestBody AsignaturaRequest req) {
    return servicio.crear(UsuarioActual.id(), req);
  }

  @PutMapping("/{id}")
  public AsignaturaDto actualizar(@PathVariable Long id, @Valid @RequestBody AsignaturaRequest req) {
    return servicio.actualizar(UsuarioActual.id(), id, req);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void eliminar(@PathVariable Long id) {
    servicio.eliminar(UsuarioActual.id(), id);
  }
}
