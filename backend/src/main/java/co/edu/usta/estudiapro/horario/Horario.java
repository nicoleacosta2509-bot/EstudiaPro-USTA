package co.edu.usta.estudiapro.horario;

import co.edu.usta.estudiapro.asignatura.Asignatura;
import co.edu.usta.estudiapro.usuario.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalTime;

// Un bloque de clase en la semana: asignatura, día y franja horaria
@Entity
@Table(name = "horarios")
public class Horario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "asignatura_id", nullable = false)
  private Asignatura asignatura;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private DiaSemana dia;

  @Column(nullable = false)
  private LocalTime horaInicio;

  @Column(nullable = false)
  private LocalTime horaFin;

  @Column(length = 60)
  private String salon;

  protected Horario() {}

  public Horario(Usuario usuario) {
    this.usuario = usuario;
  }

  public Long getId() {
    return id;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public Asignatura getAsignatura() {
    return asignatura;
  }

  public void setAsignatura(Asignatura asignatura) {
    this.asignatura = asignatura;
  }

  public DiaSemana getDia() {
    return dia;
  }

  public void setDia(DiaSemana dia) {
    this.dia = dia;
  }

  public LocalTime getHoraInicio() {
    return horaInicio;
  }

  public void setHoraInicio(LocalTime horaInicio) {
    this.horaInicio = horaInicio;
  }

  public LocalTime getHoraFin() {
    return horaFin;
  }

  public void setHoraFin(LocalTime horaFin) {
    this.horaFin = horaFin;
  }

  public String getSalon() {
    return salon;
  }

  public void setSalon(String salon) {
    this.salon = salon;
  }
}
