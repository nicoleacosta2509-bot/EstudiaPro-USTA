package co.edu.usta.estudiapro.recordatorio;

import co.edu.usta.estudiapro.comun.NoEncontradoException;
import co.edu.usta.estudiapro.usuario.UsuarioRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecordatorioService {

  private final RecordatorioRepository recordatorios;
  private final UsuarioRepository usuarios;
  private final Clock clock;

  public RecordatorioService(
      RecordatorioRepository recordatorios, UsuarioRepository usuarios, Clock clock) {
    this.recordatorios = recordatorios;
    this.usuarios = usuarios;
    this.clock = clock;
  }

  /**
   * Deja el recordatorio de una actividad al día con sus datos. Si no tiene anticipación o la
   * actividad ya está terminada, el recordatorio se borra.
   */
  @Transactional
  public void sincronizar(
      Long usuarioId,
      TipoRecordatorio tipo,
      Long referenciaId,
      String titulo,
      LocalDateTime fechaEvento,
      Integer minutosAntes,
      boolean activa) {
    if (minutosAntes == null || !activa) {
      recordatorios.eliminarDe(tipo, referenciaId);
      return;
    }
    Recordatorio r =
        recordatorios
            .findByTipoAndReferenciaId(tipo, referenciaId)
            .orElseGet(
                () -> new Recordatorio(usuarios.getReferenceById(usuarioId), tipo, referenciaId));
    LocalDateTime aviso = fechaEvento.minusMinutes(minutosAntes);
    // Si cambió la hora de aviso, se vuelve a mostrar aunque ya se hubiera atendido
    if (!aviso.equals(r.getFechaAviso())) {
      r.setAtendido(false);
    }
    r.setTitulo(titulo);
    r.setFechaEvento(fechaEvento);
    r.setFechaAviso(aviso);
    recordatorios.save(r);
  }

  @Transactional
  public void eliminar(TipoRecordatorio tipo, Long referenciaId) {
    recordatorios.eliminarDe(tipo, referenciaId);
  }

  @Transactional(readOnly = true)
  public List<RecordatorioDto> proximos(Long usuarioId) {
    return recordatorios.proximos(usuarioId, LocalDateTime.now(clock)).stream()
        .map(RecordatorioDto::de)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<RecordatorioDto> pendientes(Long usuarioId) {
    return recordatorios.pendientes(usuarioId, LocalDateTime.now(clock)).stream()
        .map(RecordatorioDto::de)
        .toList();
  }

  @Transactional
  public void atender(Long usuarioId, Long id) {
    Recordatorio r =
        recordatorios
            .findByIdAndUsuarioId(id, usuarioId)
            .orElseThrow(() -> new NoEncontradoException("El recordatorio no existe."));
    r.setAtendido(true);
  }
}
