package co.edu.usta.estudiapro.horario;

import java.time.DayOfWeek;

// El orden de declaración es el orden de la semana (se usa para ordenar el horario)
public enum DiaSemana {
  LUNES,
  MARTES,
  MIERCOLES,
  JUEVES,
  VIERNES,
  SABADO,
  DOMINGO;

  public static DiaSemana de(DayOfWeek dia) {
    return values()[dia.getValue() - 1];
  }
}
