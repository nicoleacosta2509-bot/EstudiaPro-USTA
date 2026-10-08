package co.edu.usta.estudiapro;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class TareaIntegracionTest extends BaseIntegracionTest {

  private Map<String, Object> tarea(String titulo, String fecha) {
    Map<String, Object> t = new HashMap<>();
    t.put("titulo", titulo);
    t.put("fechaEntrega", fecha);
    return t;
  }

  private long crear(String token, Map<String, Object> t) throws Exception {
    String body =
        mvc.perform(conToken(post("/api/tareas"), token, t))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return leer(body).get("id").asLong();
  }

  // RC-04: lo que se registra, edita o cambia de estado se mantiene al volver a consultarlo
  @Test
  void crearEditarYCambiarEstadoSeConserva() throws Exception {
    String token = nuevoUsuario();
    long id = crear(token, tarea("Informe de laboratorio", enDias(3)));

    Map<String, Object> editada = tarea("Informe de laboratorio final", enDias(4));
    editada.put("prioridad", "ALTA");
    mvc.perform(conToken(put("/api/tareas/" + id), token, editada)).andExpect(status().isOk());
    mvc.perform(conToken(patch("/api/tareas/" + id + "/estado"), token, Map.of("estado", "EN_PROCESO")))
        .andExpect(status().isOk());

    mvc.perform(conToken(get("/api/tareas"), token, null))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].titulo").value("Informe de laboratorio final"))
        .andExpect(jsonPath("$[0].prioridad").value("ALTA"))
        .andExpect(jsonPath("$[0].estado").value("EN_PROCESO"))
        .andExpect(jsonPath("$[0].vencida").value(false));

    mvc.perform(conToken(get("/api/tareas?estado=TERMINADA"), token, null))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  // RC-03: no se guarda información incompleta ni con fechas inválidas
  @Test
  void rechazaDatosIncompletosYFechasInvalidas() throws Exception {
    String token = nuevoUsuario();

    mvc.perform(conToken(post("/api/tareas"), token, Map.of("descripcion", "sin título")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.campos.titulo").value("Escribe el título de la tarea."))
        .andExpect(jsonPath("$.campos.fechaEntrega").value("Indica la fecha de entrega."));

    mvc.perform(conToken(post("/api/tareas"), token, tarea("Tarea vieja", enDias(-1))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.campos.fechaEntrega").exists());

    mvc.perform(conToken(post("/api/tareas"), token, tarea("Fecha rara", "31/12/2026")))
        .andExpect(status().isBadRequest());

    mvc.perform(conToken(get("/api/tareas"), token, null)).andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void unEstudianteNoVeNiTocaLasTareasDeOtro() throws Exception {
    String ana = nuevoUsuario();
    String beto = nuevoUsuario();
    long id = crear(ana, tarea("Tarea de Ana", enDias(2)));

    mvc.perform(conToken(get("/api/tareas"), beto, null)).andExpect(jsonPath("$", hasSize(0)));
    mvc.perform(conToken(delete("/api/tareas/" + id), beto, null))
        .andExpect(status().isNotFound());
    mvc.perform(conToken(get("/api/tareas"), ana, null)).andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void elRecordatorioSigueElEstadoDeLaTarea() throws Exception {
    String token = nuevoUsuario();
    // Entrega en 10 horas con aviso 1 día antes: el aviso ya está pendiente
    Map<String, Object> t =
        tarea("Entrega urgente", java.time.LocalDateTime.now().plusHours(10).format(FECHA));
    t.put("recordatorioMinutos", 1440);
    long id = crear(token, t);

    mvc.perform(conToken(get("/api/recordatorios/pendientes"), token, null))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].tipo").value("TAREA"))
        .andExpect(jsonPath("$[0].referenciaId").value(id));

    // Al terminar la tarea el aviso desaparece
    mvc.perform(conToken(patch("/api/tareas/" + id + "/estado"), token, Map.of("estado", "TERMINADA")))
        .andExpect(status().isOk());
    mvc.perform(conToken(get("/api/recordatorios/pendientes"), token, null))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void panelResumeElAvance() throws Exception {
    String token = nuevoUsuario();
    long a = crear(token, tarea("Una", enDias(1)));
    crear(token, tarea("Dos", enDias(2)));
    mvc.perform(conToken(patch("/api/tareas/" + a + "/estado"), token, Map.of("estado", "TERMINADA")))
        .andExpect(status().isOk());

    mvc.perform(conToken(get("/api/panel"), token, null))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tareasPendientes").value(1))
        .andExpect(jsonPath("$.tareasTerminadas").value(1))
        .andExpect(jsonPath("$.avance").value(50))
        .andExpect(jsonPath("$.proximas", hasSize(1)))
        .andExpect(jsonPath("$.proximas[0].titulo").value("Dos"));
  }
}
