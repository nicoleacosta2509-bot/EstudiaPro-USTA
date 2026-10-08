package co.edu.usta.estudiapro.panel;

import co.edu.usta.estudiapro.horario.HorarioDtos.HorarioDto;
import co.edu.usta.estudiapro.recordatorio.TipoRecordatorio;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

// Resumen para la pantalla de inicio
public record PanelDto(
    long tareasPendientes,
    long tareasEnProceso,
    long tareasTerminadas,
    long tareasVencidas,
    int avance,
    long examenesProximos,
    List<Proxima> proximas,
    List<HorarioDto> clasesHoy) {

  // Tarea o examen que vence en los próximos días
  public record Proxima(
      TipoRecordatorio tipo,
      Long id,
      String titulo,
      String asignaturaNombre,
      String color,
      @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fecha,
      String estado) {}
}
