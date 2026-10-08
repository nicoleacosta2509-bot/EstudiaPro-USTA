package co.edu.usta.estudiapro.recordatorio;

import co.edu.usta.estudiapro.seguridad.UsuarioActual;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recordatorios")
public class RecordatorioController {

  private final RecordatorioService servicio;

  public RecordatorioController(RecordatorioService servicio) {
    this.servicio = servicio;
  }

  @GetMapping
  public List<RecordatorioDto> proximos() {
    return servicio.proximos(UsuarioActual.id());
  }

  // El frontend lo consulta cada minuto para mostrar los avisos
  @GetMapping("/pendientes")
  public List<RecordatorioDto> pendientes() {
    return servicio.pendientes(UsuarioActual.id());
  }

  @PatchMapping("/{id}/atendido")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void atender(@PathVariable Long id) {
    servicio.atender(UsuarioActual.id(), id);
  }
}
