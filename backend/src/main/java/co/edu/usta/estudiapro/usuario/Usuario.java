package co.edu.usta.estudiapro.usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

// Estudiante dueño de sus tareas, exámenes, horarios y recordatorios
@Entity
@Table(name = "usuarios")
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 80)
  private String nombre;

  @Column(nullable = false, unique = true, length = 120)
  private String email;

  @Column(nullable = false)
  private String passwordHash;

  @Column(nullable = false)
  private LocalDateTime creadoEn;

  protected Usuario() {}

  public Usuario(String nombre, String email, String passwordHash, LocalDateTime creadoEn) {
    this.nombre = nombre;
    this.email = email;
    this.passwordHash = passwordHash;
    this.creadoEn = creadoEn;
  }

  public Long getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }

  public String getEmail() {
    return email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public LocalDateTime getCreadoEn() {
    return creadoEn;
  }
}
