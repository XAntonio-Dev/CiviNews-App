# Changelog

All notable changes to **CiviNews** will be documented in this file.

This project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html) and the format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

---

## [1.1.0] — 2026-04-18

> Segunda iteración del proyecto. El foco de esta entrega ha sido la moderación de contenido, la geolocalización y la reestructuración del backend hacia una arquitectura más limpia.

### Frontend — Android (Jetpack Compose)

#### Added
- **`AddReportScreen`**: pantalla de creación de reportes con mapa interactivo mediante el MapBox SDK.
- Sistema de selección de ubicación por marcador arrastrable con captura de latitud y longitud exactas.
- **`AdminScreen`**: panel de administración y moderación de contenido ciudadano.
- Estados de interfaz reactivos en el panel de administración: `Loading`, `Success`, `NoData` y `Error`.
- Controles de evento para la aprobación (`onApprove`) y eliminación (`onDelete`) de reportes en tiempo real.
- Navegación condicional tras el login en función del rol del usuario.
- Formateo de fechas relativas con `java.time` (p. ej. "Hace 5 minutos").
- Integración de Coil con soporte para placeholders y gestión de caché de imágenes.

#### Changed
- Refactorización de `AuthScreen` para unificar los flujos de login y registro.
- Carga de categorías desde el endpoint `/canales`: la pantalla de creación ya no tiene categorías hardcodeadas.

---

### Backend — Python (FastAPI)

#### Added
- Nuevos endpoints RESTful:
  - `POST /register` — registro de nuevos usuarios.
  - `GET /canales` — listado dinámico de categorías desde base de datos.
  - `POST /noticias` — creación de avisos con soporte para coordenadas geográficas.
  - `GET /noticias/pendientes` — listado de contenido pendiente para el panel de moderación.
  - `PATCH /noticias/{id}/estado` — actualización parcial del estado para validación de contenido.
  - `DELETE /noticias/{id}` — eliminación física de registros rechazados.
- Sistema de roles y permisos: distinción de privilegios entre `Administrador` y `Ciudadano`.

#### Changed
- Separación de `models.py` y `schemas.py`: los objetos de transferencia de datos (DTO) están ahora desacoplados de los modelos de base de datos.
- Estandarización del hash de contraseñas con Bcrypt (prefijo `$2b$`) compatible con `passlib`.

---

### Infraestructura y base de datos

#### Added
- Integración con Cloudinary CDN para el almacenamiento y servicio de imágenes.
- Script `init.sql` ampliado con pre-populación de categorías, usuarios administradores y casos de prueba (Bulos, Infraestructuras y Gasto Público).

#### Changed
- Optimización de `docker-compose.yml` para garantizar la persistencia de datos mediante volúmenes de PostgreSQL.

---

## [1.0.0] — 2026-03-29

> Entrega inicial. Estructura base del proyecto con el stack completo conectado de extremo a extremo.

### Added
- Arquitectura base cliente-servidor (Android + FastAPI + PostgreSQL).
- Feed de noticias con filtrado por categorías estáticas.
- Sistema de login básico con validación de credenciales contra la base de datos.
- Configuración inicial de contenedores Docker.

---

*CiviNews — TFG 2025/2026 · Antonio Javier del Río Ramos*
