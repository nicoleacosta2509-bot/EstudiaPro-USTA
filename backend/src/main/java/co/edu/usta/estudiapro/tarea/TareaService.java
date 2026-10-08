package co.edu.usta.estudiapro.tarea;

import co.edu.usta.estudiapro.asignatura.AsignaturaService;
import co.edu.usta.estudiapro.comun.DatoInvalidoException;
import co.edu.usta.estudiapro.comun.NoEncontradoException;
import co.edu.usta.estudiapro.recordatorio.RecordatorioService;
import co.edu.usta.estudiapro.recordatorio.TipoRecordatorio;
import co.edu.usta.estudiapro.tarea.TareaDtos.TareaDto;
import co.edu.usta.estudiapro.tarea.TareaDtos.TareaRequest;
import co.edu.usta.estudiapro.usuario.UsuarioRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TareaService {

  private final TareaRepository tareas;
  private final UsuarioRepository usuarios;
  private final AsignaturaService asignaturas;
  private final RecordatorioService recordatorios;
  private final Clock clock;

  public TareaService(
      TareaRepository tareas,
      UsuarioRepository usuarios,
      AsignaturaService asignaturas,
      RecordatorioService recordatorios,
      Clock clock) {
    this.tareas = tareas;
    this.usuarios = usuarios;
    this.asignaturas = asignaturas;
    this.recordatorios = recordatorios;
    this.clock = clock;
  }

  // Filtros opcionales: si llegan null se ignoran
  @Transactional(readOnly = true)
  public List<TareaDto> listar(Long usuarioId, EstadoTarea estado, Long asignaturaId) {
    LocalDateTime ahora = LocalDateTime.now(clock);
    return tareas.findByUsuarioIdOrderByFechaEntregaAsc(usuarioId).stream()
        .filter(t -> estado == null || t.getEstado() == estado)
        .filter(
            t ->
                asignaturaId == null
                    || (t.getAsignatura() != null
                        && asignaturaId.equals(t.getAsignatura().getId())))
        .map(t -> TareaDto.de(t, ahora))
        .toList();
  }

  @Transactional
  public TareaDto crear(Long usuarioId, TareaRequest req) {
    LocalDateTime ahora = LocalDateTime.now(clock);
    if (req.fechaEntrega().isBefore(ahora)) {
      throw new DatoInvalidoException(
          "fechaEntrega", "La fecha de entrega no puede estar en el pasado.");
    }
    Tarea t = new Tarea(usuarios.getReferenceById(usuarioId));
    aplicar(usuarioId, t, req);
    tareas.save(t);
    sincronizarRecordatorio(usuarioId, t);
    return TareaDto.de(t, ahora);
  }

  @Transactional
  public TareaDto actualizar(Long usuarioId, Long id, TareaRequest req) {
    LocalDateTime ahora = LocalDateTime.now(clock);
    Tarea t = buscar(usuarioId, id);
    // Se puede editar una tarea vencida sin tocar su fecha, pero no moverla a otra fecha pasada
    if (!Objects.equals(t.getFechaEntrega(), req.fechaEntrega())
        && req.fechaEntrega().isBefore(ahora)) {
      throw new DatoInvalidoException(
          "fechaEntrega", "La nueva fecha de entrega no puede estar en el pasado.");
    }
    aplicar(usuarioId, t, req);
    sincronizarRecordatorio(usuarioId, t);
    return TareaDto.de(t, ahora);
  }

  @Transactional
  public TareaDto cambiarEstado(Long usuarioId, Long id, EstadoTarea estado) {
    Tarea t = buscar(usuarioId, id);
    t.setEstado(estado);
    sincronizarRecordatorio(usuarioId, t);
    return TareaDto.de(t, LocalDateTime.now(clock));
  }

  @Transactional
  public void eliminar(Long usuarioId, Long id) {
    Tarea t = buscar(usuarioId, id);
    recordatorios.eliminar(TipoRecordatorio.TAREA, t.getId());
    tareas.delete(t);
  }

  private void aplicar(Long usuarioId, Tarea t, TareaRequest req) {
    t.setTitulo(req.titulo().trim());
    t.setDescripcion(
        req.descripcion() == null || req.descripcion().isBlank() ? null : req.descripcion().trim());
    t.setAsignatura(
        req.asignaturaId() == null ? null : asignaturas.buscar(usuarioId, req.asignaturaId()));
    t.setFechaEntrega(req.fechaEntrega());
    t.setPrioridad(req.prioridad() == null ? Prioridad.MEDIA : req.prioridad());
    t.setRecordatorioMinutos(req.recordatorioMinutos());
  }

  // Una tarea terminada ya no necesita aviso
  private void sincronizarRecordatorio(Long usuarioId, Tarea t) {
    recordatorios.sincronizar(
        usuarioId,
        TipoRecordatorio.TAREA,
        t.getId(),
        t.getTitulo(),
        t.getFechaEntrega(),
        t.getRecordatorioMinutos(),
        t.getEstado() != EstadoTarea.TERMINADA);
  }

  private Tarea buscar(Long usuarioId, Long id) {
    return tareas
        .findByIdAndUsuarioId(id, usuarioId)
        .orElseThrow(() -> new NoEncontradoException("La tarea no existe."));
  }
}
