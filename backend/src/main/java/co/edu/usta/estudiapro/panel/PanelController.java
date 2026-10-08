package co.edu.usta.estudiapro.panel;

import co.edu.usta.estudiapro.seguridad.UsuarioActual;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/panel")
public class PanelController {

  private final PanelService servicio;

  public PanelController(PanelService servicio) {
    this.servicio = servicio;
  }

  @GetMapping
  public PanelDto resumen() {
    return servicio.resumen(UsuarioActual.id());
  }
}
