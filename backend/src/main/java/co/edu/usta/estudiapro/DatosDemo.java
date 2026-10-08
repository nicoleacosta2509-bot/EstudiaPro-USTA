package co.edu.usta.estudiapro;

import co.edu.usta.estudiapro.asignatura.AsignaturaDtos.AsignaturaRequest;
import co.edu.usta.estudiapro.asignatura.AsignaturaService;
import co.edu.usta.estudiapro.examen.ExamenDtos.ExamenRequest;
import co.edu.usta.estudiapro.examen.ExamenService;
import co.edu.usta.estudiapro.horario.DiaSemana;
import co.edu.usta.estudiapro.horario.HorarioDtos.HorarioRequest;
import co.edu.usta.estudiapro.horario.HorarioService;
import co.edu.usta.estudiapro.tarea.EstadoTarea;
import co.edu.usta.estudiapro.tarea.Prioridad;
import co.edu.usta.estudiapro.tarea.TareaDtos.TareaRequest;
import co.edu.usta.estudiapro.tarea.TareaService;
import co.edu.usta.estudiapro.usuario.AuthDtos.RegistroRequest;
import co.edu.usta.estudiapro.usuario.AuthService;
import co.edu.usta.estudiapro.usuario.UsuarioRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

// Cuenta de ejemplo para la demostración: demo@estudiapro.co / demo123 (solo si la base está vacía)
@Component
@ConditionalOnProperty(name = "app.datos-demo", havingValue = "true")
public class DatosDemo implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(DatosDemo.class);

  private final UsuarioRepository usuarios;
  private final AuthService auth;
  private final AsignaturaService asignaturas;
  private final HorarioService horarios;
  private final TareaService tareas;
  private final ExamenService examenes;
  private final Clock clock;

  public DatosDemo(
      UsuarioRepository usuarios,
      AuthService auth,
      AsignaturaService asignaturas,
      HorarioService horarios,
      TareaService tareas,
      ExamenService examenes,
      Clock clock) {
    this.usuarios = usuarios;
    this.auth = auth;
    this.asignaturas = asignaturas;
    this.horarios = horarios;
    this.tareas = tareas;
    this.examenes = examenes;
    this.clock = clock;
  }

  @Override
  public void run(String... args) {
    if (usuarios.count() > 0) {
      return;
    }
    Long u =
        auth.registrar(new RegistroRequest("Estudiante Demo", "demo@estudiapro.co", "demo123"))
            .usuario()
            .id();

    Long gerencia =
        asignaturas.crear(u, new AsignaturaRequest("Gerencia de Software", null, "#4F6BED")).id();
    Long bases =
        asignaturas.crear(u, new AsignaturaRequest("Bases de Datos II", null, "#16A34A")).id();
    Long redes = asignaturas.crear(u, new AsignaturaRequest("Redes", null, "#EA580C")).id();
    Long estadistica =
        asignaturas.crear(u, new AsignaturaRequest("Estadística", null, "#9333EA")).id();

    clase(u, gerencia, DiaSemana.LUNES, 7, 9, "Aula 301");
    clase(u, bases, DiaSemana.LUNES, 10, 12, "Sala de sistemas 2");
    clase(u, redes, DiaSemana.MARTES, 8, 10, "Laboratorio de redes");
    clase(u, estadistica, DiaSemana.MIERCOLES, 14, 16, "Aula 205");
    clase(u, gerencia, DiaSemana.JUEVES, 7, 9, "Aula 301");
    clase(u, bases, DiaSemana.VIERNES, 10, 12, "Sala de sistemas 2");

    // Fechas relativas a hoy para que el panel siempre tenga algo que mostrar
    LocalDateTime hoy = LocalDateTime.now(clock).truncatedTo(ChronoUnit.HOURS);
    Long acta =
        tareas
            .crear(
                u,
                new TareaRequest(
                    "Acta de constitución",
                    "Alcance, objetivos, interesados, riesgos y roles.",
                    gerencia,
                    hoy.plusDays(1).withHour(23).withMinute(59),
                    Prioridad.ALTA,
                    1440))
            .id();
    tareas.cambiarEstado(u, acta, EstadoTarea.TERMINADA);
    Long modelo =
        tareas
            .crear(
                u,
                new TareaRequest(
                    "Modelo entidad-relación",
                    "Diagrama del proyecto final con las tablas normalizadas.",
                    bases,
                    hoy.plusHours(20),
                    Prioridad.ALTA,
                    1440))
            .id();
    tareas.cambiarEstado(u, modelo, EstadoTarea.EN_PROCESO);
    tareas.crear(
        u,
        new TareaRequest(
            "Informe de subredes",
            "Tabla de direccionamiento VLSM y topología en Packet Tracer.",
            redes,
            hoy.plusDays(4).withHour(18).withMinute(0),
            Prioridad.MEDIA,
            2880));
    tareas.crear(
        u,
        new TareaRequest(
            "Taller de probabilidad",
            "Ejercicios 1 al 15 del capítulo 4.",
            estadistica,
            hoy.plusDays(9).withHour(23).withMinute(59),
            Prioridad.BAJA,
            null));

    examenes.crear(
        u,
        new ExamenRequest(
            "Primer parcial",
            "Normalización, SQL avanzado y transacciones.",
            bases,
            hoy.plusDays(5).withHour(10).withMinute(0),
            "Sala de sistemas 2",
            1440));
    examenes.crear(
        u,
        new ExamenRequest(
            "Quiz de distribuciones",
            "Binomial, Poisson y normal.",
            estadistica,
            hoy.plusDays(12).withHour(14).withMinute(0),
            "Aula 205",
            2880));

    log.info("Datos de ejemplo creados: demo@estudiapro.co / demo123");
  }

  private void clase(Long u, Long asignatura, DiaSemana dia, int desde, int hasta, String salon) {
    horarios.crear(
        u,
        new HorarioRequest(asignatura, dia, LocalTime.of(desde, 0), LocalTime.of(hasta, 0), salon));
  }
}
