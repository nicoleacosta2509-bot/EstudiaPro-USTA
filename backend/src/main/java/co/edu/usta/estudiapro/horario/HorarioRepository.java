package co.edu.usta.estudiapro.horario;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HorarioRepository extends JpaRepository<Horario, Long> {

  List<Horario> findByUsuarioId(Long usuarioId);

  List<Horario> findByUsuarioIdAndDia(Long usuarioId, DiaSemana dia);

  Optional<Horario> findByIdAndUsuarioId(Long id, Long usuarioId);

  boolean existsByAsignaturaId(Long asignaturaId);
}
