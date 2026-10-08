package co.edu.usta.estudiapro;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class AuthIntegracionTest extends BaseIntegracionTest {

  @Test
  void registroLoginYConsultaDelUsuario() throws Exception {
    Map<String, String> datos =
        Map.of("nombre", "Laura", "email", "Laura.Rojas@USTA.edu.co", "password", "clave123");
    mvc.perform(
            post("/api/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(datos)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.usuario.email").value("laura.rojas@usta.edu.co"));

    // El correo se guarda en minúsculas, así que se puede entrar escribiéndolo distinto
    String body =
        mvc.perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        json.writeValueAsString(
                            Map.of("email", "laura.rojas@usta.edu.co", "password", "clave123"))))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    String token = leer(body).get("token").asText();

    mvc.perform(conToken(get("/api/auth/yo"), token, null))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nombre").value("Laura"));

    // Correo repetido
    mvc.perform(
            post("/api/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(datos)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.mensaje").value("Ya existe una cuenta con ese correo."));
  }

  @Test
  void loginConClaveIncorrectaDa401() throws Exception {
    nuevoUsuario();
    mvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    json.writeValueAsString(
                        Map.of("email", "nadie@usta.edu.co", "password", "noesesta"))))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.mensaje").value("Correo o contraseña incorrectos."));
  }

  @Test
  void registroInvalidoIndicaCadaCampo() throws Exception {
    mvc.perform(
            post("/api/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    json.writeValueAsString(
                        Map.of("nombre", "", "email", "no-es-correo", "password", "123"))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.campos.nombre").exists())
        .andExpect(jsonPath("$.campos.email").value("El correo no tiene un formato válido."))
        .andExpect(jsonPath("$.campos.password").exists());
  }

  @Test
  void sinTokenNoSePuedeConsultarNada() throws Exception {
    mvc.perform(get("/api/tareas"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.mensaje").exists());
    mvc.perform(get("/api/panel").header("Authorization", "Bearer token-falso"))
        .andExpect(status().isUnauthorized());
  }
}
