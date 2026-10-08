package co.edu.usta.estudiapro.tarea;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TareaRepository extends JpaRepository<Tarea, Long> {

  List<Tarea> findByUsuarioIdOrderByFechaEntregaAsc(Long usuarioId);

  Optional<Tarea> findByIdAndUsuarioId(Long id, Long usuarioId);

  boolean existsByAsignaturaId(Long asignaturaId);
}
