# Estándares del proyecto

Proyecto: EstudiaPro · Versión 1.0 · 7 de octubre de 2026
Responsable: Nicole Tatiana Acosta Tellez (análisis, desarrollo, pruebas y documentación)
Interlocutora principal: Laura Valentina Rojas (validación con usuaria)

EstudiaPro lo desarrolla una sola persona, así que no hay un compañero que revise cada cambio. Por eso los
acuerdos de este archivo se apoyan en tres cosas que no dependen de la memoria: las pruebas automáticas, una
lista de revisión que se aplica a cada *pull request* y la validación con la interlocutora para todo lo que se
ve en pantalla. Si algo de aquí no se puede comprobar abriendo el repositorio, se reescribe o se quita.

Este archivo se cambia como cualquier otro: por *pull request* hacia `dev`.

---

## 1. Estilo y nombres

**Guías que se siguen**

| Parte | Lenguaje y herramientas | Guía |
|---|---|---|
| Backend | Java 21, Spring Boot 3.3, Maven | [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html): 2 espacios, 100 columnas |
| Frontend | JavaScript, React 18, Vite 5 | Componentes en funciones con *hooks*, un componente por archivo, sin librerías de interfaz |

**Idioma del código**

- El dominio va en español, igual que el acta: `Tarea`, `Examen`, `Horario`, `Asignatura`, `Recordatorio`.
  Los términos técnicos del framework se dejan como son (`controller`, `repository`, `token`).
- Mensajes de error, comentarios y mensajes de commit: en español, con tildes.
- Lo que ve el usuario dice qué corregir, no solo que algo falló (RC-01).

**Tres reglas propias de nombres**

1. Las entidades van en singular (`Tarea`); las rutas REST, en plural y minúscula (`/api/tareas`, `/api/examenes`).
2. Una prueba se llama como una frase que dice qué pasa: `rechazaDatosIncompletosYFechasInvalidas`,
   `unEstudianteNoVeNiTocaLasTareasDeOtro`.
3. Las variables de entorno van en mayúsculas (`JWT_SECRET`, `DB_URL`, `CORS_ORIGINS`). Las que debe ver el
   navegador empiezan por `VITE_`. En el repositorio solo viven valores de ejemplo.

---

## 2. Commits y ramas

**Formato del mensaje** (Conventional Commits)

```
tipo(alcance): qué se hace, en infinitivo y en minúscula
```

- Primera línea de 72 caracteres como máximo, sin punto final.
- Si el porqué no cabe en el título, va en el cuerpo, después de una línea en blanco.
- Un commit es un cambio que se explica en una frase. Si hace falta una "y", probablemente son dos.
- Ejemplo real del repositorio: `test(backend): pruebas de integración de RC-03, RC-04 y seguridad`.

**Tipos permitidos**

| Tipo | Cuándo |
|---|---|
| `feat` | funcionalidad nueva |
| `fix` | corrección de un error |
| `docs` | solo documentación |
| `style` | formato, sin cambiar comportamiento |
| `refactor` | reorganizar código sin cambiar comportamiento |
| `test` | pruebas |
| `chore` | configuración, dependencias, integración de ramas |

El alcance es el módulo que se toca: `auth`, `seguridad`, `tareas`, `examenes`, `horario`, `asignaturas`,
`recordatorios`, `panel`, `backend`, `frontend`, `readme`, `estandares`.

**Ramas**

| Rama | Para qué |
|---|---|
| `main` | lo estable y entregable; solo recibe `dev` |
| `dev` | donde se junta lo que ya pasó la lista de revisión |
| `nicole` | el trabajo diario |

- Los commits se hacen en `nicole` y llegan a `dev` por *pull request*. No se hace commit directo en `main` ni en `dev`.
- Al cerrar un sprint, `dev` se integra en `main` y se crea la etiqueta de versión. Los commits de integración
  usan `chore(merge)` y `chore(release)`.
- El historial de `main` y `dev` no se reescribe.

---

## 3. Definition of Ready

Una historia entra al sprint solo si cumple todo esto. Se revisa al planear la semana.

1. Existe como *issue* en GitHub, escrita como «como estudiante quiero [acción] para [beneficio]».
2. Tiene criterios de aceptación escritos como casos: «entrada → resultado esperado», con los códigos de
   respuesta cuando es una API.
3. Se puede terminar en menos de 4 horas de trabajo. Si no, se parte (el acta estima de 4 a 6 horas de
   desarrollo por semana).
4. Está dentro del alcance del acta. Si es una función nueva, ya se evaluó contra las exclusiones (riesgo de
   crecimiento del alcance).
5. Si toca backend y frontend a la vez, la ruta, el método, el cuerpo y los códigos de respuesta están escritos
   en el *issue* antes de empezar.
6. Si cambia algo que la interlocutora va a usar, se sabe cuándo se le va a mostrar.

Si no cumple, no entra al sprint: se completa y entra en el siguiente.

---

## 4. Definition of Done

Una historia está terminada cuando cumple estas condiciones. En la columna de la derecha está cómo lo
comprueba alguien que no estuvo cuando se hizo.

| # | Condición | Cómo se comprueba |
|---|---|---|
| 1 | Las pruebas del backend pasan. | `cd backend && mvn test` termina sin fallos. |
| 2 | Cada criterio de aceptación que toca la API tiene al menos una prueba automática. | En `backend/src/test` hay una prueba cuyo nombre describe ese criterio, y pasa. |
| 3 | El frontend compila. | `cd frontend && npm run build` termina con código 0. |
| 4 | Si el cambio es de interfaz, el *pull request* trae una captura y los pasos para verlo. | El *pull request* incluye la imagen y los pasos con `npm run dev`. |
| 5 | Los formularios validan antes de enviar y muestran el error junto al campo (RC-01, RC-03). | Se envía el formulario vacío y con una fecha pasada; cada campo muestra su mensaje. |
| 6 | Si cambia un endpoint, una variable de entorno o un comando, el README cambia en el mismo cambio. | El diff del *pull request* incluye `README.md`, `backend/README.md` o `frontend/README.md`. |
| 7 | El *pull request* pasó la lista de revisión de la sección 5. | La lista aparece marcada en la descripción del *pull request*. |
| 8 | Si la historia cambia algo que usa la interlocutora, ella lo vio. | Su observación queda anotada en el *issue* o en el acta de validación. |

Los puntos 4, 5, 6 y 8 solo aplican cuando el cambio los toca. Los demás aplican siempre.

---

## 5. Política de revisión

**Quién revisa.** Al ser una sola persona, la revisión se hace al día siguiente de terminar el cambio, no el
mismo día, con la lista de abajo. Lo que tiene interfaz lo valida además la interlocutora en la demo semanal.

**Lista de revisión** (se copia en cada *pull request*):

- [ ] `mvn test` y `npm run build` pasan.
- [ ] No hay contraseñas, llaves, tokens ni archivos `.env` en el diff.
- [ ] Toda consulta nueva filtra por el estudiante autenticado (nadie ve datos de otro).
- [ ] Lo nuevo en el backend tiene una prueba que lo cubre.
- [ ] Los mensajes de error dicen qué corregir.
- [ ] El README está al día si cambió un endpoint, una variable o un comando.
- [ ] Los mensajes de commit siguen la convención.

**Lo que bloquea** (el cambio no se integra hasta resolverlo): cualquier casilla sin marcar, algo que antes
funcionaba y dejó de funcionar, o conflictos con `dev` sin resolver.

**Lo que no bloquea** (se anota como mejora en un *issue*): nombres que igual cumplen la guía, refactors que
no cambian el comportamiento, redacción de comentarios y optimizaciones que nadie ha medido.

---

## 6. Cuando esto no se pueda cumplir

| Escenario | Qué se hace |
|---|---|
| Semana de parciales y no hay tiempo para revisar al día siguiente. | Se revisa antes de integrar en `main`, nunca después. Lo que no alcanzó a revisarse se queda en `dev`. |
| La interlocutora no puede en la semana de la demo. | Se le envían las capturas y los pasos, y se agenda la validación para la semana siguiente (riesgo del acta). |
| La noche antes de una entrega falla una prueba. | Se corrige o se deja fuera; no se entrega a medias. |
| No hay PostgreSQL a mano. | Las pruebas corren con H2 en memoria, así que la DoD se puede cumplir igual. |
| El cambio es de una línea (un error de digitación). | Pasa por *pull request* igual; solo aplican los puntos de la DoD que lo toquen. |

---

## 7. Lo que todavía no se cumple

Mejor escribirlo que fingir:

- No hay formateador automático configurado (Spotless o Prettier); el estilo se revisa con la lista.
- El frontend no tiene pruebas automáticas. Por eso el punto 4 de la DoD pide captura y pasos.
- Falta registrar la prueba en Microsoft Edge (RC-05) y medir los tiempos de respuesta de RC-02.
- La versión 1.0 inicial y la integración de este documento se hicieron sin *issues* ni *pull requests*,
  porque estos acuerdos todavía no existían. De aquí en adelante se trabaja con ellos.

---

## 8. Aceptación

| Nombre completo | Aceptación | Fecha |
|---|---|---|
| Nicole Tatiana Acosta Tellez | Conozco y acepto estos estándares. | 7 de octubre de 2026 |
