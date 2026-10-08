package co.edu.usta.estudiapro;

import java.time.Clock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;

// Sin usuario en memoria de Spring: la sesión se maneja solo con JWT
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class EstudiaProApplication {

  public static void main(String[] args) {
    SpringApplication.run(EstudiaProApplication.class, args);
  }

  // Reloj único de la aplicación: así las validaciones de fechas usan siempre la misma hora
  @Bean
  public Clock clock() {
    return Clock.systemDefaultZone();
  }
}
