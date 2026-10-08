package co.edu.usta.estudiapro.asignatura;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsignaturaRepository extends JpaRepository<Asignatura, Long> {

  List<Asignatura> findByUsuarioIdOrderByNombreAsc(Long usuarioId);

  Optional<Asignatura> findByIdAndUsuarioId(Long id, Long usuarioId);

  boolean existsByUsuarioIdAndNombreIgnoreCase(Long usuarioId, String nombre);

  boolean existsByUsuarioIdAndNombreIgnoreCaseAndIdNot(Long usuarioId, String nombre, Long id);
}
