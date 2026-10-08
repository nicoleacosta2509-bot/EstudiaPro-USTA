package co.edu.usta.estudiapro.examen;

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

// Parcial, quiz o examen final que el estudiante debe presentar
@Entity
@Table(name = "examenes")
public class Examen {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "asignatura_id")
  private Asignatura asignatura;

  @Column(nullable = false, length = 120)
  private String titulo;

  @Column(length = 1000)
  private String temas;

  @Column(nullable = false)
  private LocalDateTime fecha;

  @Column(length = 120)
  private String lugar;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 12)
  private EstadoExamen estado;

  private Integer recordatorioMinutos;

  protected Examen() {}

  public Examen(Usuario usuario) {
    this.usuario = usuario;
    this.estado = EstadoExamen.PENDIENTE;
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

  public String getTemas() {
    return temas;
  }

  public void setTemas(String temas) {
    this.temas = temas;
  }

  public LocalDateTime getFecha() {
    return fecha;
  }

  public void setFecha(LocalDateTime fecha) {
    this.fecha = fecha;
  }

  public String getLugar() {
    return lugar;
  }

  public void setLugar(String lugar) {
    this.lugar = lugar;
  }

  public EstadoExamen getEstado() {
    return estado;
  }

  public void setEstado(EstadoExamen estado) {
    this.estado = estado;
  }

  public Integer getRecordatorioMinutos() {
    return recordatorioMinutos;
  }

  public void setRecordatorioMinutos(Integer recordatorioMinutos) {
    this.recordatorioMinutos = recordatorioMinutos;
  }
}
