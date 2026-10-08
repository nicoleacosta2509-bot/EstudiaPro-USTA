package co.edu.usta.estudiapro.asignatura;

import co.edu.usta.estudiapro.usuario.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// Materia que cursa el estudiante; agrupa sus tareas, exámenes y clases
@Entity
@Table(name = "asignaturas")
public class Asignatura {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  @Column(nullable = false, length = 80)
  private String nombre;

  @Column(length = 80)
  private String docente;

  @Column(nullable = false, length = 7)
  private String color;

  protected Asignatura() {}

  public Asignatura(Usuario usuario) {
    this.usuario = usuario;
  }

  public Long getId() {
    return id;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getDocente() {
    return docente;
  }

  public void setDocente(String docente) {
    this.docente = docente;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }
}
