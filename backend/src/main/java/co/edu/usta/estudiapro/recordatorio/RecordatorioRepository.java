package co.edu.usta.estudiapro.recordatorio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RecordatorioRepository extends JpaRepository<Recordatorio, Long> {

  Optional<Recordatorio> findByTipoAndReferenciaId(TipoRecordatorio tipo, Long referenciaId);

  Optional<Recordatorio> findByIdAndUsuarioId(Long id, Long usuarioId);

  @Modifying
  @Query("delete from Recordatorio r where r.tipo = :tipo and r.referenciaId = :referenciaId")
  void eliminarDe(TipoRecordatorio tipo, Long referenciaId);

  // Recordatorios sin atender de actividades que todavía no han pasado
  @Query(
      """
      select r from Recordatorio r
      where r.usuario.id = :usuarioId and r.atendido = false and r.fechaEvento >= :ahora
      order by r.fechaAviso
      """)
  List<Recordatorio> proximos(Long usuarioId, LocalDateTime ahora);

  // Los que ya llegaron a su hora de aviso
  @Query(
      """
      select r from Recordatorio r
      where r.usuario.id = :usuarioId and r.atendido = false
        and r.fechaAviso <= :ahora and r.fechaEvento >= :ahora
      order by r.fechaAviso
      """)
  List<Recordatorio> pendientes(Long usuarioId, LocalDateTime ahora);
}
