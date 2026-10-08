package co.edu.usta.estudiapro.recordatorio;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record RecordatorioDto(
    Long id,
    TipoRecordatorio tipo,
    Long referenciaId,
    String titulo,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fechaEvento,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fechaAviso,
    boolean atendido) {

  public static RecordatorioDto de(Recordatorio r) {
    return new RecordatorioDto(
        r.getId(),
        r.getTipo(),
        r.getReferenciaId(),
        r.getTitulo(),
        r.getFechaEvento(),
        r.getFechaAviso(),
        r.isAtendido());
  }
}
