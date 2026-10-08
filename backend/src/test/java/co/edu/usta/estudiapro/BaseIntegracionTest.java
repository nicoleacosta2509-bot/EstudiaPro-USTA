package co.edu.usta.estudiapro;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

// Base común: arranca la app con H2 en memoria y ayuda a crear usuarios y peticiones con token
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegracionTest {

  protected static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

  @Autowired protected MockMvc mvc;
  @Autowired protected ObjectMapper json;

  // Registra un estudiante nuevo (correo único) y devuelve su token
  protected String nuevoUsuario() throws Exception {
    String email = "est-" + UUID.randomUUID() + "@usta.edu.co";
    String body =
        mvc.perform(
                post("/api/auth/registro")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        json.writeValueAsString(
                            Map.of("nombre", "Estudiante", "email", email, "password", "secreto1"))))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return json.readTree(body).get("token").asText();
  }

  protected MockHttpServletRequestBuilder conToken(
      MockHttpServletRequestBuilder req, String token, Object cuerpo) throws Exception {
    req.header("Authorization", "Bearer " + token);
    if (cuerpo != null) {
      req.contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(cuerpo));
    }
    return req;
  }

  protected JsonNode leer(String body) throws Exception {
    return json.readTree(body);
  }

  protected static String enDias(int dias) {
    return LocalDateTime.now().plusDays(dias).format(FECHA);
  }
}
