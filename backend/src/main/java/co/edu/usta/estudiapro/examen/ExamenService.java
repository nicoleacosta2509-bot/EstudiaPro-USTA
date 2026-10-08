package co.edu.usta.estudiapro.examen;

import co.edu.usta.estudiapro.asignatura.AsignaturaService;
import co.edu.usta.estudiapro.comun.DatoInvalidoException;
import co.edu.usta.estudiapro.comun.NoEncontradoException;
import co.edu.usta.estudiapro.examen.ExamenDtos.ExamenDto;
import co.edu.usta.estudiapro.examen.ExamenDtos.ExamenRequest;
import co.edu.usta.estudiapro.recordatorio.RecordatorioService;
import co.edu.usta.estudiapro.recordatorio.TipoRecordatorio;
import co.edu.usta.estudiapro.usuario.UsuarioRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExamenService {

  private final ExamenRepository examenes;
  private final UsuarioRepository usuarios;
  private final AsignaturaService asignaturas;
  private final RecordatorioService recordatorios;
  private final Clock clock;

  public ExamenService(
      ExamenRepository examenes,
      UsuarioRepository usuarios,
      AsignaturaService asignaturas,
      RecordatorioService recordatorios,
      Clock clock) {
    this.examenes = examenes;
    this.usuarios = usuarios;
    this.asignaturas = asignaturas;
    this.recordatorios = recordatorios;
    this.clock = clock;
  }

  @Transactional(readOnly = true)
  public List<ExamenDto> listar(Long usuarioId) {
    return examenes.findByUsuarioIdOrderByFechaAsc(usuarioId).stream()
        .map(ExamenDto::de)
        .toList();
  }

  @Transactional
  public ExamenDto crear(Long usuarioId, ExamenRequest req) {
    if (req.fecha().isBefore(LocalDateTime.now(clock))) {
      throw new DatoInvalidoException("fecha", "La fecha del examen no puede estar en el pasado.");
    }
    Examen e = new Examen(usuarios.getReferenceById(usuarioId));
    aplicar(usuarioId, e, req);
    examenes.save(e);
    sincronizarRecordatorio(usuarioId, e);
    return ExamenDto.de(e);
  }

  @Transactional
  public ExamenDto actualizar(Long usuarioId, Long id, ExamenRequest req) {
    Examen e = buscar(usuarioId, id);
    if (!Objects.equals(e.getFecha(), req.fecha())
        && req.fecha().isBefore(LocalDateTime.now(clock))) {
      throw new DatoInvalidoException(
          "fecha", "La nueva fecha del examen no puede estar en el pasado.");
    }
    aplicar(usuarioId, e, req);
    sincronizarRecordatorio(usuarioId, e);
    return ExamenDto.de(e);
  }

  @Transactional
  public ExamenDto cambiarEstado(Long usuarioId, Long id, EstadoExamen estado) {
    Examen e = buscar(usuarioId, id);
    e.setEstado(estado);
    sincronizarRecordatorio(usuarioId, e);
    return ExamenDto.de(e);
  }

  @Transactional
  public void eliminar(Long usuarioId, Long id) {
    Examen e = buscar(usuarioId, id);
    recordatorios.eliminar(TipoRecordatorio.EXAMEN, e.getId());
    examenes.delete(e);
  }

  private void aplicar(Long usuarioId, Examen e, ExamenRequest req) {
    e.setTitulo(req.titulo().trim());
    e.setTemas(vacioANull(req.temas()));
    e.setAsignatura(
        req.asignaturaId() == null ? null : asignaturas.buscar(usuarioId, req.asignaturaId()));
    e.setFecha(req.fecha());
    e.setLugar(vacioANull(req.lugar()));
    e.setRecordatorioMinutos(req.recordatorioMinutos());
  }

  // Un examen ya presentado no necesita aviso
  private void sincronizarRecordatorio(Long usuarioId, Examen e) {
    recordatorios.sincronizar(
        usuarioId,
        TipoRecordatorio.EXAMEN,
        e.getId(),
        e.getTitulo(),
        e.getFecha(),
        e.getRecordatorioMinutos(),
        e.getEstado() == EstadoExamen.PENDIENTE);
  }

  private Examen buscar(Long usuarioId, Long id) {
    return examenes
        .findByIdAndUsuarioId(id, usuarioId)
        .orElseThrow(() -> new NoEncontradoException("El examen no existe."));
  }

  private static String vacioANull(String s) {
    return s == null || s.isBlank() ? null : s.trim();
  }
}
