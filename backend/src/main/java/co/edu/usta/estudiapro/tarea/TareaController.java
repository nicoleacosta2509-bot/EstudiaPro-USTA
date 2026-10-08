package co.edu.usta.estudiapro.tarea;

import co.edu.usta.estudiapro.seguridad.UsuarioActual;
import co.edu.usta.estudiapro.tarea.TareaDtos.CambioEstadoRequest;
import co.edu.usta.estudiapro.tarea.TareaDtos.TareaDto;
import co.edu.usta.estudiapro.tarea.TareaDtos.TareaRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

  private final TareaService servicio;

  public TareaController(TareaService servicio) {
    this.servicio = servicio;
  }

  @GetMapping
  public List<TareaDto> listar(
      @RequestParam(required = false) EstadoTarea estado,
      @RequestParam(required = false) Long asignaturaId) {
    return servicio.listar(UsuarioActual.id(), estado, asignaturaId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TareaDto crear(@Valid @RequestBody TareaRequest req) {
    return servicio.crear(UsuarioActual.id(), req);
  }

  @PutMapping("/{id}")
  public TareaDto actualizar(@PathVariable Long id, @Valid @RequestBody TareaRequest req) {
    return servicio.actualizar(UsuarioActual.id(), id, req);
  }

  @PatchMapping("/{id}/estado")
  public TareaDto cambiarEstado(
      @PathVariable Long id, @Valid @RequestBody CambioEstadoRequest req) {
    return servicio.cambiarEstado(UsuarioActual.id(), id, req.estado());
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void eliminar(@PathVariable Long id) {
    servicio.eliminar(UsuarioActual.id(), id);
  }
}
