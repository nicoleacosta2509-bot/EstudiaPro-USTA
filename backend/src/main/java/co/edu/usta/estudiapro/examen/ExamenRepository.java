package co.edu.usta.estudiapro.examen;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamenRepository extends JpaRepository<Examen, Long> {

  List<Examen> findByUsuarioIdOrderByFechaAsc(Long usuarioId);

  Optional<Examen> findByIdAndUsuarioId(Long id, Long usuarioId);

  boolean existsByAsignaturaId(Long asignaturaId);
}
