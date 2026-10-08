package co.edu.usta.estudiapro.recordatorio;

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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

// Aviso de una tarea o examen próximo. Se crea y actualiza solo al guardar la actividad.
@Entity
@Table(
    name = "recordatorios",
    uniqueConstraints = @UniqueConstraint(columnNames = {"tipo", "referencia_id"}))
public class Recordatorio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private TipoRecordatorio tipo;

  // Id de la tarea o del examen, según el tipo
  @Column(name = "referencia_id", nullable = false)
  private Long referenciaId;

  @Column(nullable = false, length = 120)
  private String titulo;

  @Column(nullable = false)
  private LocalDateTime fechaEvento;

  @Column(nullable = false)
  private LocalDateTime fechaAviso;

  @Column(nullable = false)
  private boolean atendido;

  protected Recordatorio() {}

  public Recordatorio(Usuario usuario, TipoRecordatorio tipo, Long referenciaId) {
    this.usuario = usuario;
    this.tipo = tipo;
    this.referenciaId = referenciaId;
  }

  public Long getId() {
    return id;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public TipoRecordatorio getTipo() {
    return tipo;
  }

  public Long getReferenciaId() {
    return referenciaId;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public LocalDateTime getFechaEvento() {
    return fechaEvento;
  }

  public void setFechaEvento(LocalDateTime fechaEvento) {
    this.fechaEvento = fechaEvento;
  }

  public LocalDateTime getFechaAviso() {
    return fechaAviso;
  }

  public void setFechaAviso(LocalDateTime fechaAviso) {
    this.fechaAviso = fechaAviso;
  }

  public boolean isAtendido() {
    return atendido;
  }

  public void setAtendido(boolean atendido) {
    this.atendido = atendido;
  }
}
