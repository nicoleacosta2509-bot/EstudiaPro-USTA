package co.edu.usta.estudiapro.asignatura;

import co.edu.usta.estudiapro.asignatura.AsignaturaDtos.AsignaturaDto;
import co.edu.usta.estudiapro.asignatura.AsignaturaDtos.AsignaturaRequest;
import co.edu.usta.estudiapro.comun.ConflictoException;
import co.edu.usta.estudiapro.comun.NoEncontradoException;
import co.edu.usta.estudiapro.examen.ExamenRepository;
import co.edu.usta.estudiapro.horario.HorarioRepository;
import co.edu.usta.estudiapro.tarea.TareaRepository;
import co.edu.usta.estudiapro.usuario.UsuarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsignaturaService {

  private static final String COLOR_POR_DEFECTO = "#4F6BED";

  private final AsignaturaRepository asignaturas;
  private final UsuarioRepository usuarios;
  private final TareaRepository tareas;
  private final ExamenRepository examenes;
  private final HorarioRepository horarios;

  public AsignaturaService(
      AsignaturaRepository asignaturas,
      UsuarioRepository usuarios,
      TareaRepository tareas,
      ExamenRepository examenes,
      HorarioRepository horarios) {
    this.asignaturas = asignaturas;
    this.usuarios = usuarios;
    this.tareas = tareas;
    this.examenes = examenes;
    this.horarios = horarios;
  }

  @Transactional(readOnly = true)
  public List<AsignaturaDto> listar(Long usuarioId) {
    return asignaturas.findByUsuarioIdOrderByNombreAsc(usuarioId).stream()
        .map(AsignaturaDto::de)
        .toList();
  }

  @Transactional
  public AsignaturaDto crear(Long usuarioId, AsignaturaRequest req) {
    String nombre = req.nombre().trim();
    if (asignaturas.existsByUsuarioIdAndNombreIgnoreCase(usuarioId, nombre)) {
      throw new ConflictoException("Ya tienes una asignatura llamada \"" + nombre + "\".");
    }
    Asignatura a = new Asignatura(usuarios.getReferenceById(usuarioId));
    aplicar(a, req);
    return AsignaturaDto.de(asignaturas.save(a));
  }

  @Transactional
  public AsignaturaDto actualizar(Long usuarioId, Long id, AsignaturaRequest req) {
    Asignatura a = buscar(usuarioId, id);
    String nombre = req.nombre().trim();
    if (asignaturas.existsByUsuarioIdAndNombreIgnoreCaseAndIdNot(usuarioId, nombre, id)) {
      throw new ConflictoException("Ya tienes una asignatura llamada \"" + nombre + "\".");
    }
    aplicar(a, req);
    return AsignaturaDto.de(a);
  }

  @Transactional
  public void eliminar(Long usuarioId, Long id) {
    Asignatura a = buscar(usuarioId, id);
    // No se borra en cascada para no perder tareas o exámenes sin que el estudiante lo note
    if (tareas.existsByAsignaturaId(id)
        || examenes.existsByAsignaturaId(id)
        || horarios.existsByAsignaturaId(id)) {
      throw new ConflictoException(
          "No se puede eliminar \""
              + a.getNombre()
              + "\" porque tiene tareas, exámenes o clases asociadas. Elimínalos o cámbialos de asignatura primero.");
    }
    asignaturas.delete(a);
  }

  // Lo usan los otros módulos para validar que la asignatura sea del estudiante
  @Transactional(readOnly = true)
  public Asignatura buscar(Long usuarioId, Long id) {
    return asignaturas
        .findByIdAndUsuarioId(id, usuarioId)
        .orElseThrow(() -> new NoEncontradoException("La asignatura no existe."));
  }

  private void aplicar(Asignatura a, AsignaturaRequest req) {
    a.setNombre(req.nombre().trim());
    a.setDocente(vacioANull(req.docente()));
    a.setColor(req.color() == null ? COLOR_POR_DEFECTO : req.color().toUpperCase());
  }

  private static String vacioANull(String s) {
    return s == null || s.isBlank() ? null : s.trim();
  }
}
