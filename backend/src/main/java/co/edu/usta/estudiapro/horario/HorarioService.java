package co.edu.usta.estudiapro.horario;

import co.edu.usta.estudiapro.asignatura.AsignaturaService;
import co.edu.usta.estudiapro.comun.ConflictoException;
import co.edu.usta.estudiapro.comun.DatoInvalidoException;
import co.edu.usta.estudiapro.comun.NoEncontradoException;
import co.edu.usta.estudiapro.horario.HorarioDtos.HorarioDto;
import co.edu.usta.estudiapro.horario.HorarioDtos.HorarioRequest;
import co.edu.usta.estudiapro.usuario.UsuarioRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HorarioService {

  // Lunes primero y, dentro del día, por hora de inicio
  static final Comparator<Horario> ORDEN =
      Comparator.comparing(Horario::getDia).thenComparing(Horario::getHoraInicio);

  private final HorarioRepository horarios;
  private final UsuarioRepository usuarios;
  private final AsignaturaService asignaturas;

  public HorarioService(
      HorarioRepository horarios, UsuarioRepository usuarios, AsignaturaService asignaturas) {
    this.horarios = horarios;
    this.usuarios = usuarios;
    this.asignaturas = asignaturas;
  }

  @Transactional(readOnly = true)
  public List<HorarioDto> listar(Long usuarioId) {
    return horarios.findByUsuarioId(usuarioId).stream().sorted(ORDEN).map(HorarioDto::de).toList();
  }

  @Transactional(readOnly = true)
  public List<HorarioDto> delDia(Long usuarioId, DiaSemana dia) {
    return horarios.findByUsuarioIdAndDia(usuarioId, dia).stream()
        .sorted(ORDEN)
        .map(HorarioDto::de)
        .toList();
  }

  @Transactional
  public HorarioDto crear(Long usuarioId, HorarioRequest req) {
    Horario h = new Horario(usuarios.getReferenceById(usuarioId));
    aplicar(usuarioId, h, req);
    return HorarioDto.de(horarios.save(h));
  }

  @Transactional
  public HorarioDto actualizar(Long usuarioId, Long id, HorarioRequest req) {
    Horario h = buscar(usuarioId, id);
    aplicar(usuarioId, h, req);
    return HorarioDto.de(h);
  }

  @Transactional
  public void eliminar(Long usuarioId, Long id) {
    horarios.delete(buscar(usuarioId, id));
  }

  private void aplicar(Long usuarioId, Horario h, HorarioRequest req) {
    if (!req.horaFin().isAfter(req.horaInicio())) {
      throw new DatoInvalidoException(
          "horaFin", "La hora de fin debe ser posterior a la hora de inicio.");
    }
    validarCruce(usuarioId, h.getId(), req);
    h.setAsignatura(asignaturas.buscar(usuarioId, req.asignaturaId()));
    h.setDia(req.dia());
    h.setHoraInicio(req.horaInicio());
    h.setHoraFin(req.horaFin());
    h.setSalon(req.salon() == null || req.salon().isBlank() ? null : req.salon().trim());
  }

  // Dos clases se cruzan si una empieza antes de que la otra termine
  private void validarCruce(Long usuarioId, Long idActual, HorarioRequest req) {
    horarios.findByUsuarioIdAndDia(usuarioId, req.dia()).stream()
        .filter(o -> !Objects.equals(o.getId(), idActual))
        .filter(
            o ->
                o.getHoraInicio().isBefore(req.horaFin())
                    && req.horaInicio().isBefore(o.getHoraFin()))
        .findFirst()
        .ifPresent(
            o -> {
              throw new ConflictoException(
                  "Ese horario se cruza con "
                      + o.getAsignatura().getNombre()
                      + " ("
                      + o.getHoraInicio()
                      + " - "
                      + o.getHoraFin()
                      + ").");
            });
  }

  private Horario buscar(Long usuarioId, Long id) {
    return horarios
        .findByIdAndUsuarioId(id, usuarioId)
        .orElseThrow(() -> new NoEncontradoException("La clase no existe en tu horario."));
  }
}
