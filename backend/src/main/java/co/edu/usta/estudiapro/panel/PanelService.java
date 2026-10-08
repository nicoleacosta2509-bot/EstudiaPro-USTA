package co.edu.usta.estudiapro.panel;

import co.edu.usta.estudiapro.examen.EstadoExamen;
import co.edu.usta.estudiapro.examen.Examen;
import co.edu.usta.estudiapro.examen.ExamenRepository;
import co.edu.usta.estudiapro.horario.DiaSemana;
import co.edu.usta.estudiapro.horario.HorarioService;
import co.edu.usta.estudiapro.panel.PanelDto.Proxima;
import co.edu.usta.estudiapro.recordatorio.TipoRecordatorio;
import co.edu.usta.estudiapro.tarea.EstadoTarea;
import co.edu.usta.estudiapro.tarea.Tarea;
import co.edu.usta.estudiapro.tarea.TareaRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PanelService {

  private static final int DIAS_PROXIMAS = 7;
  private static final int DIAS_EXAMENES = 14;

  private final TareaRepository tareas;
  private final ExamenRepository examenes;
  private final HorarioService horarios;
  private final Clock clock;

  public PanelService(
      TareaRepository tareas, ExamenRepository examenes, HorarioService horarios, Clock clock) {
    this.tareas = tareas;
    this.examenes = examenes;
    this.horarios = horarios;
    this.clock = clock;
  }

  @Transactional(readOnly = true)
  public PanelDto resumen(Long usuarioId) {
    LocalDateTime ahora = LocalDateTime.now(clock);
    List<Tarea> lista = tareas.findByUsuarioIdOrderByFechaEntregaAsc(usuarioId);
    List<Examen> exs = examenes.findByUsuarioIdOrderByFechaAsc(usuarioId);

    long pendientes = contar(lista, EstadoTarea.PENDIENTE);
    long enProceso = contar(lista, EstadoTarea.EN_PROCESO);
    long terminadas = contar(lista, EstadoTarea.TERMINADA);
    long vencidas =
        lista.stream()
            .filter(t -> t.getEstado() != EstadoTarea.TERMINADA)
            .filter(t -> t.getFechaEntrega().isBefore(ahora))
            .count();
    // Porcentaje de tareas terminadas sobre el total
    int avance = lista.isEmpty() ? 0 : (int) Math.round(terminadas * 100.0 / lista.size());
    long examenesProximos =
        exs.stream()
            .filter(e -> e.getEstado() == EstadoExamen.PENDIENTE)
            .filter(e -> entre(e.getFecha(), ahora, ahora.plusDays(DIAS_EXAMENES)))
            .count();

    LocalDateTime limite = ahora.plusDays(DIAS_PROXIMAS);
    List<Proxima> proximas = new ArrayList<>();
    lista.stream()
        .filter(t -> t.getEstado() != EstadoTarea.TERMINADA)
        .filter(t -> entre(t.getFechaEntrega(), ahora, limite))
        .forEach(t -> proximas.add(proxima(t)));
    exs.stream()
        .filter(e -> e.getEstado() == EstadoExamen.PENDIENTE)
        .filter(e -> entre(e.getFecha(), ahora, limite))
        .forEach(e -> proximas.add(proxima(e)));
    proximas.sort(Comparator.comparing(Proxima::fecha));

    return new PanelDto(
        pendientes,
        enProceso,
        terminadas,
        vencidas,
        avance,
        examenesProximos,
        proximas,
        horarios.delDia(usuarioId, DiaSemana.de(ahora.getDayOfWeek())));
  }

  private static long contar(List<Tarea> lista, EstadoTarea estado) {
    return lista.stream().filter(t -> t.getEstado() == estado).count();
  }

  private static boolean entre(LocalDateTime fecha, LocalDateTime desde, LocalDateTime hasta) {
    return !fecha.isBefore(desde) && !fecha.isAfter(hasta);
  }

  private static Proxima proxima(Tarea t) {
    return new Proxima(
        TipoRecordatorio.TAREA,
        t.getId(),
        t.getTitulo(),
        t.getAsignatura() == null ? null : t.getAsignatura().getNombre(),
        t.getAsignatura() == null ? null : t.getAsignatura().getColor(),
        t.getFechaEntrega(),
        t.getEstado().name());
  }

  private static Proxima proxima(Examen e) {
    return new Proxima(
        TipoRecordatorio.EXAMEN,
        e.getId(),
        e.getTitulo(),
        e.getAsignatura() == null ? null : e.getAsignatura().getNombre(),
        e.getAsignatura() == null ? null : e.getAsignatura().getColor(),
        e.getFecha(),
        e.getEstado().name());
  }
}
