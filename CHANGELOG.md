# Changelog

Todos los cambios relevantes de **CiviNews** están documentados en este archivo.

Este proyecto sigue [Semantic Versioning](https://semver.org/spec/v2.0.0.html) y el formato está basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

---

## [1.2.0] — 2026-06-04

> Versión de cierre de curso. El foco de esta iteración ha estado en tres frentes: llevar la cobertura de tests al 100% con una batería E2E completa, dar al administrador control total sobre los usuarios de la plataforma y blindar la API para un despliegue real en producción. También se ha añadido un sistema de recuperación de contraseñas funcional con Mailtrap Sandbox y se han cerrado bugs de validación interna que habían sobrevivido desde versiones anteriores.

---

### Backend — API REST (FastAPI)

#### Added

- **Suite de tests E2E con cobertura del 100% (`pytest --cov=main`):** 22 tests que cubren de forma exhaustiva toda la lógica de negocio y seguridad del sistema. Escenarios validados: registro y login completos, límites antispam con respuesta `429 Too Many Requests`, protección CORS, integridad de perfiles de usuario y borrado en cascada al eliminar una cuenta. Cada test opera sobre una base de datos aislada que se inicializa y destruye en cada ejecución para garantizar la independencia entre pruebas.
- **Mailtrap Sandbox — recuperación de contraseñas:** Integración del servicio Mailtrap Sandbox en el nuevo endpoint `POST /recuperar-password`. Permite probar el flujo completo de envío de emails transaccionales (token de recuperación incluido) en un entorno seguro y sin coste real. Las credenciales de Mailtrap se gestionan por variables de entorno, igual que el resto de secretos del sistema.
- **Dashboard de administración — gestión de usuarios:** Tres nuevos endpoints protegidos por rol de administrador que cierran el ciclo de control sobre la plataforma:
  - `GET /usuarios` — listado completo de usuarios registrados con soporte de búsqueda.
  - `PATCH /usuarios/{id}/rol` — asignación o revocación del rol de administrador sobre cualquier cuenta.
  - `DELETE /usuarios/{id}` — baneo permanente de un usuario, con borrado en cascada de todos sus reportes asociados. Mismo mecanismo `ON DELETE CASCADE` ya implementado en PostgreSQL.
- **Configuración estricta de CORS (`allow_origins=[]`):** `CORSMiddleware` configurado para bloquear por defecto cualquier origen no autorizado. Imprescindible para el despliegue en producción: sin esta restricción, la API sería accesible desde cualquier dominio externo a través del navegador.

#### Fixed

- **Comunicación interna entre contenedores Docker (red `civinews_tester`):** Corregida la configuración de red en `docker-compose.yml` para que la API y la base de datos de tests se comuniquen por nombre de servicio en lugar de por dirección IP local. Elimina fallos de conexión intermitentes al ejecutar la suite de tests dentro del entorno Docker.
- **Validación de `old_password` en el cambio de contraseña:** Resuelto un error de lógica en el endpoint de actualización de contraseña que en ciertos casos permitía que la validación de la contraseña actual devolviera un resultado incorrecto. El flujo ahora compara correctamente el hash almacenado antes de autorizar cualquier cambio.

---

## [1.1.0] — 2026-04-18

> Versión final del TFG. El grueso del trabajo de esta iteración ha estado en tres frentes: cerrar el ciclo completo de moderación en el backend y dejar la app lista para desplegarse en producción real. También se ha dado un repaso serio a la privacidad, al diseño y a varios bugs que habían sobrevivido desde la versión anterior.

---

### Frontend — Android (Jetpack Compose)

#### Added

- **Modo claro y oscuro (Material Design 3):** La interfaz se adapta automáticamente a las preferencias del sistema. No es cosmético: reduce la fatiga visual en uso prolongado y es uno de los estándares que se esperan hoy en día en cualquier app nativa.
- **`AddReportScreen`:** Pantalla completa de creación de reportes. Incluye selector de categoría dinámico (cargado desde la API), campo de título, descripción, subida de imagen y el módulo de geolocalización con MapBox integrado.
- **Geolocalización:** Buscador de direcciones por texto (Mapbox Geocoding). Latitud y longitud se capturan con precisión y se envían al servidor junto al reporte.
- **`AdminScreen`:** Panel de moderación protegido por rol. Solo accesible si `is_admin == true`. Muestra la cola de reportes pendientes con toda la información necesaria para tomar una decisión, y expone dos acciones directas por tarjeta: aprobar o rechazar.
- **Estados reactivos en `AdminScreen`:** `Loading`, `Success`, `NoData` y `Error` implementados para que el administrador sepa en todo momento qué está pasando, incluso cuando la cola está vacía o hay un problema de red.
- **`ReportDetailScreen`:** Vista de detalle de un reporte con mini-mapa inmersivo integrado que sitúa la incidencia en contexto sin necesidad de salir de la app.
- **`MisAvisosScreen`:** Sección personal donde el ciudadano puede ver el estado actualizado de todos sus reportes (`pendiente`, `aprobada`, `rechazada`), cerrando el ciclo de feedback entre el usuario y la administración.
- **Vistas legales y de soporte:** Pantallas de Política de Privacidad, Términos y Condiciones y Centro de Ayuda (FAQ) renderizadas como overlays, ocultando la barra de navegación inferior para forzar el foco en el contenido y evitar fugas de navegación.
- **Danger Zone (Perfil):** Botón de borrado permanente de cuenta. Al confirmar, ejecuta un `DELETE` en el servidor que dispara el `ON DELETE CASCADE` en PostgreSQL, eliminando el usuario y todos sus datos asociados. Cumplimiento estricto del RGPD.
- **Cambio de alias con límite temporal:** El usuario puede editar su nombre visible desde el perfil, pero solo una vez cada 14 días para evitar spam o suplantación.
- Formateo de fechas relativas con `java.time`: "Hace 5 minutos", "Hace 2 horas", etc.
- Navegación condicional tras el login según el rol: los administradores aterrizan en `AdminMainScreen` y los ciudadanos en `UserMainScreen`, gestionado desde el propio `NavHost` anidado.

#### Changed

- **Refactorización de `AuthScreen`:** Se unificaron los flujos de login y registro en una sola pantalla con un toggle selector en la parte superior. Antes eran dos pantallas separadas. Reduce la carga cognitiva y elimina una pantalla de navegación innecesaria.
- **Categorías dinámicas en `AddReportScreen`:** Las categorías ya no están escritas en el código. Se cargan desde el endpoint `/canales` en cada apertura de la pantalla. Si el servidor añade una categoría nueva, la app la muestra automáticamente sin necesitar actualización.
- **Integración de Coil mejorada:** Añadido soporte completo para placeholders mientras carga la imagen, estados de error con imagen de fallback y gestión explícita de caché para no recargar imágenes que ya se han descargado.

---

### Backend — API REST (FastAPI)

#### Added

- **`GET /noticias/pendientes`:** Devuelve todos los reportes con estado `pendiente`. Endpoint protegido: solo accesible con token JWT de un usuario con `is_admin = true`. Devuelve 403 si el rol no es suficiente.
- **`PATCH /noticias/{id}/estado`:** Actualización parcial del estado de un reporte. Se usa `PATCH` y no `PUT` porque solo se modifica un campo, no el objeto entero. Requiere rol de administrador.
- **`DELETE /noticias/{id}`:** Eliminación física del registro de la base de datos. Los reportes rechazados no se marcan como rechazados, se borran. Requiere rol de administrador.
- **Sistema de roles (`is_admin`):** La distinción entre administrador y ciudadano se gestiona con un booleano en la tabla `usuarios`. FastAPI lo extrae del token JWT en cada petición a través de `Depends(get_current_user)` y lo evalúa antes de ejecutar cualquier operación de moderación.
- **Borrado en cascada (RGPD):** La relación entre `usuarios` y `noticias` tiene definida la restricción `ON DELETE CASCADE` en PostgreSQL. Al borrar un usuario, todas sus publicaciones desaparecen también. No quedan datos huérfanos.
- **Suite de tests automatizados con pytest:** Cobertura de los tres bloques principales del sistema:
  - *Autenticación:* registro, login, obtención de JWT y escenarios de error (401, 400, 422).
  - *Operaciones de usuario autenticado:* creación de noticias, validación de campos y control de acceso.
  - *Panel de administración:* acceso sin permisos (403), aprobación con `PATCH`, borrado con `DELETE` y error sobre IDs inexistentes (404).
  - Cada test sigue el patrón `arrange → act → assert` sobre una base de datos aislada que se inicializa y destruye en cada ejecución.

#### Changed

- **Separación de `models.py` y `schemas.py`:** Los modelos de SQLAlchemy (estructura interna de la BD) y los schemas de Pydantic v2 (lo que se expone al cliente) están ahora en archivos separados. El frontend nunca recibe la estructura real de la base de datos, solo los DTOs definidos en `schemas.py`.
- **Hash de contraseñas estandarizado:** Migrado a Bcrypt con prefijo `$2b$` usando `passlib`. Compatible con la mayoría de herramientas del ecosistema Python y sin contraseñas en texto plano en ningún punto del sistema.

#### Fixed

- **Bug `422 Unprocessable Entity` en `/noticias/pendientes`:** FastAPI interpretaba `/pendientes` como un parámetro dinámico `/{id}` porque la ruta estática estaba declarada después de la dinámica en el router. Solucionado reordenando las rutas para que las estáticas tengan precedencia sobre las parametrizadas.

---

### Infraestructura y base de datos

#### Added

- **Integración con Cloudinary CDN:** Las imágenes de los reportes se suben a Cloudinary en lugar de guardarse en el disco del servidor. La integración ha requerido implementar una función auxiliar con `suspendCancellableCoroutine` en Kotlin para adaptar la API de callbacks de Cloudinary al ecosistema de Coroutines, devolviendo la `secure_url` definitiva de forma asíncrona sin bloquear el hilo principal.
- **Preparación para despliegue en producción:** El proyecto está listo para desplegarse en Render (API REST) y Supabase (PostgreSQL gestionado). Las variables sensibles se gestionan por `.env` localmente y por Environment Variables en el panel de Render en producción. El repositorio nunca versiona credenciales.
- **Script `init.sql` ampliado:** Incluye la creación de tablas, la inserción de categorías iniciales (Alerta Bulos, Infraestructuras, Gasto Público) y dos usuarios de prueba preconfigurados (administrador y ciudadano) para poder probar el flujo completo desde el primer arranque.

#### Changed

- **Optimización de `docker-compose.yml`:** Configurados los volúmenes de PostgreSQL para garantizar la persistencia de datos entre reinicios del contenedor en el entorno local de desarrollo.

---

## [1.0.0] — 2026-03-29

> Primera entrega funcional. El objetivo de esta versión era tener el stack completo conectado de extremo a extremo aunque fuera con funcionalidades básicas. Frontend, backend y base de datos hablando entre sí antes de añadir complejidad.

### Added

- Arquitectura base cliente-servidor: app Android (Jetpack Compose) + API REST (FastAPI) + base de datos relacional (PostgreSQL), todo orquestado con Docker Compose.
- Feed de noticias con filtrado por categorías (estáticas en esta versión, dinámicas desde la 1.1.0).
- Sistema de autenticación con JWT: registro, login y validación de token en el cliente.
- Pantalla de perfil básica con opción de cerrar sesión.
- Configuración inicial de contenedores Docker con `docker-compose.yml`.
- Documentación automática de la API disponible en `/docs` (Swagger/OpenAPI).

---

*CiviNews — TFG 2025/2026 · Antonio Javier del Río Ramos · IES Portada Alta, Málaga*
