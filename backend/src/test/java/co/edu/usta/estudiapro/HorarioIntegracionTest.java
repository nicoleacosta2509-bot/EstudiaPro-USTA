package co.edu.usta.estudiapro;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.junit.jupiter.api.Test;

class HorarioIntegracionTest extends BaseIntegracionTest {

  private long asignatura(String token, String nombre) throws Exception {
    String body =
        mvc.perform(conToken(post("/api/asignaturas"), token, Map.of("nombre", nombre)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return leer(body).get("id").asLong();
  }

  private Map<String, Object> clase(long asignaturaId, String dia, String inicio, String fin) {
    return Map.of("asignaturaId", asignaturaId, "dia", dia, "horaInicio", inicio, "horaFin", fin);
  }

  @Test
  void validaHorasYCruces() throws Exception {
    String token = nuevoUsuario();
    long redes = asignatura(token, "Redes");
    long bases = asignatura(token, "Bases de Datos");

    mvc.perform(conToken(post("/api/horarios"), token, clase(redes, "MARTES", "08:00", "10:00")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.asignaturaNombre").value("Redes"));

    // Hora de fin antes que la de inicio
    mvc.perform(conToken(post("/api/horarios"), token, clase(bases, "MIERCOLES", "12:00", "10:00")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.campos.horaFin").exists());

    // Se cruza con Redes
    mvc.perform(conToken(post("/api/horarios"), token, clase(bases, "MARTES", "09:00", "11:00")))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.mensaje", containsString("Redes")));

    // Justo cuando termina la otra clase sí se puede
    mvc.perform(conToken(post("/api/horarios"), token, clase(bases, "MARTES", "10:00", "12:00")))
        .andExpect(status().isCreated());

    mvc.perform(conToken(get("/api/horarios"), token, null)).andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  void noBorraUnaAsignaturaConClases() throws Exception {
    String token = nuevoUsuario();
    long redes = asignatura(token, "Redes");
    mvc.perform(conToken(post("/api/horarios"), token, clase(redes, "LUNES", "07:00", "09:00")))
        .andExpect(status().isCreated());

    mvc.perform(conToken(delete("/api/asignaturas/" + redes), token, null))
        .andExpect(status().isConflict());

    // Asignatura repetida
    mvc.perform(conToken(post("/api/asignaturas"), token, Map.of("nombre", "redes")))
        .andExpect(status().isConflict());
  }
}
