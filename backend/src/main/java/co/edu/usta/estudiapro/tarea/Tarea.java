package co.edu.usta.estudiapro.tarea;

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
import java.time.LocalDateTime;

@Entity
@Table(name = "tareas")
public class Tarea {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  // Opcional: una tarea puede no ser de ninguna asignatura
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "asignatura_id")
  private Asignatura asignatura;

  @Column(nullable = false, length = 120)
  private String titulo;

  @Column(length = 1000)
  private String descripcion;

  @Column(nullable = false)
  private LocalDateTime fechaEntrega;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private Prioridad prioridad;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 12)
  private EstadoTarea estado;

  // Minutos antes de la entrega para avisar; null = sin recordatorio
  private Integer recordatorioMinutos;

  protected Tarea() {}

  public Tarea(Usuario usuario) {
    this.usuario = usuario;
    this.estado = EstadoTarea.PENDIENTE;
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

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public LocalDateTime getFechaEntrega() {
    return fechaEntrega;
  }

  public void setFechaEntrega(LocalDateTime fechaEntrega) {
    this.fechaEntrega = fechaEntrega;
  }

  public Prioridad getPrioridad() {
    return prioridad;
  }

  public void setPrioridad(Prioridad prioridad) {
    this.prioridad = prioridad;
  }

  public EstadoTarea getEstado() {
    return estado;
  }

  public void setEstado(EstadoTarea estado) {
    this.estado = estado;
  }

  public Integer getRecordatorioMinutos() {
    return recordatorioMinutos;
  }

  public void setRecordatorioMinutos(Integer recordatorioMinutos) {
    this.recordatorioMinutos = recordatorioMinutos;
  }
}
