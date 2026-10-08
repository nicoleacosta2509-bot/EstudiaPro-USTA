"""Medición de RC-02: 10 peticiones por operación contra el backend local, tiempos en segundos."""
import json
import statistics
import sys
import time
import urllib.request
from datetime import datetime, timedelta

BASE = "http://localhost:8080/api"
N = 10


def pedir(metodo, ruta, token=None, cuerpo=None):
    datos = json.dumps(cuerpo).encode() if cuerpo is not None else None
    req = urllib.request.Request(BASE + ruta, data=datos, method=metodo)
    req.add_header("Content-Type", "application/json")
    if token:
        req.add_header("Authorization", "Bearer " + token)
    t0 = time.perf_counter()
    with urllib.request.urlopen(req) as r:
        cuerpo_resp = r.read()
    return time.perf_counter() - t0, (json.loads(cuerpo_resp) if cuerpo_resp else None)


# Esperar a que el backend esté arriba
for _ in range(90):
    try:
        _, login = pedir("POST", "/auth/login", cuerpo={"email": "demo@estudiapro.co", "password": "demo123"})
        break
    except Exception:
        time.sleep(2)
else:
    sys.exit("el backend no respondió")
token = login["token"]

# Calentamiento: la primera petición de cada ruta carga clases y no representa el uso normal
for ruta in ("/panel", "/tareas"):
    pedir("GET", ruta, token)

fecha = (datetime.now() + timedelta(days=3)).strftime("%Y-%m-%dT%H:%M")
creadas = []
resultados = {}


def medir(nombre, fn):
    tiempos = [fn(i) for i in range(N)]
    resultados[nombre] = tiempos


def crear(i):
    t, r = pedir("POST", "/tareas", token, {"titulo": f"Medición RC-02 #{i + 1}", "fechaEntrega": fecha})
    creadas.append(r["id"])
    return t


medir("Cargar el panel (GET /api/panel)", lambda i: pedir("GET", "/panel", token)[0])
medir("Listar tareas (GET /api/tareas)", lambda i: pedir("GET", "/tareas", token)[0])
medir("Crear una tarea (POST /api/tareas)", crear)
medir("Cambiar el estado (PATCH /api/tareas/{id}/estado)",
      lambda i: pedir("PATCH", f"/tareas/{creadas[i]}/estado", token, {"estado": "EN_PROCESO"})[0])

# Dejar la cuenta demo como estaba
for tid in creadas:
    pedir("DELETE", f"/tareas/{tid}", token)

print(json.dumps({k: {"promedio": round(statistics.mean(v), 4), "maximo": round(max(v), 4),
                      "minimo": round(min(v), 4), "n": len(v)} for k, v in resultados.items()},
                 ensure_ascii=False, indent=2))
