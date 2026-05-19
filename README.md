# CiviNews

> Plataforma móvil para el reporte y gestión de incidencias ciudadanas locales.

![Kotlin](https://img.shields.io/badge/Kotlin-B125EA?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![FastAPI](https://img.shields.io/badge/FastAPI-009688?style=for-the-badge&logo=fastapi&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Render](https://img.shields.io/badge/Render-46E3B7?style=for-the-badge&logo=render&logoColor=black)
![Supabase](https://img.shields.io/badge/Supabase-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white)

---

**Autor:** Antonio Javier del Río Ramos  
**Titulación:** 2.º DAM — Desarrollo de Aplicaciones Multiplataforma  
**Centro:** IES Portada Alta, Málaga  
**Curso:** 2025/2026  
**Versión:** `1.1.0`

---

## Tabla de contenidos

1. [Qué es CiviNews](#qué-es-civinews)
2. [Demo](#demo)
3. [Arquitectura](#arquitectura)
4. [Cómo funciona](#cómo-funciona)
5. [Pantallas](#pantallas)
6. [Ciclo de vida de un reporte](#ciclo-de-vida-de-un-reporte)
7. [Stack tecnológico](#stack-tecnológico)
8. [Endpoints de la API](#endpoints-de-la-api)
9. [Instalación](#instalación)
10. [Credenciales de prueba](#credenciales-de-prueba)
11. [Roadmap](#roadmap)

---

## Qué es CiviNews

CiviNews nació de una necesidad concreta: los vecinos de un barrio se enteran de lo que pasa a su alrededor tarde, mal y a través de grupos de WhatsApp donde la información se pierde entre memes. Las alternativas que existen o son demasiado generalistas, o acumulan ruido, o tienen problemas serios de privacidad.

La idea es sencilla: un ciudadano ve un bache, un contenedor ardiendo o una avería en el alumbrado, abre la app, lo reporta con una foto y las coordenadas exactas, y eso le llega directamente al administrador para que lo valide y lo publique. Sin burocracia, sin formularios de papel, sin esperar a que alguien lo suba a Twitter.

El sistema garantiza que el usuario es anónimo de cara al público, pero rastreable internamente en caso de abuso. Y todo el contenido pasa por moderación antes de aparecer en el feed, lo que evita que la plataforma se convierta en otro foco de desinformación.

---

## Demo

Aquí se puede ver el flujo completo de la aplicación, desde el registro hasta la moderación de un reporte:

**[Ver video demo — Flujo completo CiviNews](https://drive.google.com/file/d/1Gn0yUp2xkn2x1jBLPChh4m9QyffiyG1S/view?usp=sharing)**

---

## Arquitectura

El proyecto separa completamente el cliente del servidor. No hay acoplamiento entre ambas capas, lo que significa que el día que se quiera hacer una versión web o de escritorio, el backend no hay que tocarlo.

```
┌─────────────────────────────────────────────────────────────┐
│                      CLIENTE ANDROID                        │
│                                                             │
│    FeedScreen        AddReportScreen        AdminScreen     │
│    (ciudadano)        + MapBox SDK          (moderación)    │
│                                                             │
│               MVVM + Clean Architecture                     │
│               Dagger Hilt — inyección de dependencias       │
└──────────────────────────┬──────────────────────────────────┘
                           │ Retrofit (HTTP/JSON)
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                     BACKEND — FastAPI                       │
│                                                             │
│    Routers            Services            Schemas (DTO)     │
│    /noticias          Auth / Roles        Pydantic v2       │
│                                                             │
│                    SQLAlchemy ORM                           │
└───────────────┬─────────────────────────┬───────────────────┘
                │                         │
                ▼                         ▼
         PostgreSQL                 Cloudinary CDN
         (Supabase)                 (imágenes)
```

El backend se divide en dos capas bien diferenciadas: `models.py` para los modelos de base de datos y `schemas.py` para los objetos de transferencia de datos. El frontend nunca recibe la estructura interna de la base de datos, solo lo que el schema decide exponer.

Las imágenes se suben a Cloudinary en lugar de guardarlas en el servidor. La razón es práctica: llenar el disco de un servidor con fotos es un problema real en cuanto haya algo de tráfico, y un CDN resuelve eso de raíz además de que las imágenes cargan más rápido en el móvil.

Las contraseñas se hashean con Bcrypt usando el prefijo `$2b$`, compatible con `passlib`. No hay ninguna contraseña en texto plano en ningún punto del sistema.

---

## Cómo funciona

Un ciudadano detecta un problema en su barrio, abre la app, selecciona la categoría, marca el punto exacto en el mapa y adjunta una foto. Ese reporte llega al panel del administrador con estado `pendiente`. Si lo aprueba, aparece en el feed de todos los usuarios. Si lo rechaza, se elimina físicamente de la base de datos.

Las categorías no están escritas en el código. Se cargan desde la base de datos a través del endpoint `/canales`, por lo que si se añade una nueva en el servidor, la interfaz de creación la muestra automáticamente sin necesidad de actualizar la app.

---

## Pantallas

### Vista ciudadana

| Funcionalidad | Descripción |
|---|---|
| Feed dinámico | Lista de reportes aprobados cargada desde la API |
| Filtrado por categoría | Chips generados desde `/canales`, no hardcodeados |
| Creación de reportes | Categoría + título + descripción + foto + punto en el mapa |
| Estados de interfaz | Loading, Error, NoData y Success diferenciados |
| Tema claro y oscuro | Soporte nativo con Material Design 3 |
| Fechas relativas | "Hace 5 minutos", "Hace 2 horas" mediante `java.time` |
| Mis Avisos | Seguimiento en tiempo real del estado de los reportes propios |

### Panel de administración

| Funcionalidad | Descripción |
|---|---|
| Cola de pendientes | Reportes sin revisar, accesibles solo con rol admin |
| Aprobar reporte | Cambia el estado a `aprobada` y lo publica en el feed |
| Rechazar reporte | Elimina el registro físicamente de la base de datos |
| Control de acceso | Los endpoints de moderación están protegidos por rol |

### Módulo de mapas

| Funcionalidad | Descripción |
|---|---|
| Mapa interactivo | MapBox SDK integrado en la pantalla de creación |
| Búsqueda de direcciones | Geocodificación predictiva en tiempo real |
| Selección de ubicación | Marcador arrastrable que captura latitud y longitud exactas |
| Vista de detalle | Mini-mapa integrado en la pantalla de detalle del reporte |

### Perfil y privacidad

| Funcionalidad | Descripción |
|---|---|
| Cambio de alias | Con límite de una vez cada 14 días para evitar spam |
| Cambio de contraseña | Desde la propia pantalla de perfil |
| Danger Zone | Borrado permanente de cuenta con `ON DELETE CASCADE` en PostgreSQL |

Guardar coordenadas reales en vez de solo texto abre la puerta a funcionalidades futuras como mapas de calor por zona o rutas de intervención.

---

## Ciclo de vida de un reporte

```
  CIUDADANO                    SISTEMA                    ADMINISTRADOR
      │                           │                             │
      │  Abre AddReportScreen     │                             │
      ├──────────────────────────►│                             │
      │                           │  GET /canales               │
      │◄──────────────────────────┤  (carga categorías)         │
      │  Rellena el formulario    │                             │
      │  y marca punto en mapa    │                             │
      │  POST /noticias           │                             │
      ├──────────────────────────►│                             │
      │                           │  Estado: PENDIENTE          │
      │                           │  Imagen → Cloudinary        │
      │                           ├────────────────────────────►│
      │                           │                             │  GET /noticias/pendientes
      │                           │◄────────────────────────────┤
      │                           │                             │
      │                           │         ┌───────────────────┤ Aprueba
      │                           │         │  PATCH /{id}/estado│
      │                           │◄────────┘                   │
      │  Aparece en el feed       │  Estado: APROBADA           │
      │◄──────────────────────────┤                             │
      │                           │         ┌───────────────────┤ Rechaza
      │                           │         │  DELETE /{id}      │
      │                           │◄────────┘                   │
      │                           │  Registro eliminado         │
```

Se usa `PATCH` en lugar de `PUT` para el cambio de estado porque solo se modifica un campo del recurso. No tiene sentido mandar el objeto entero para eso.

---

## Stack tecnológico

### Frontend — Android

| Componente | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| UI | Jetpack Compose — Material Design 3 |
| Arquitectura | MVVM + Clean Architecture |
| Inyección de dependencias | Dagger Hilt |
| Red | Retrofit 2 + OkHttp |
| Imágenes | Coil (caché, placeholders, estados de error) |
| Mapas y geocodificación | MapBox SDK |
| Concurrencia | Kotlin Coroutines |
| Fechas | `java.time` |

### Backend — API REST

| Componente | Tecnología |
|---|---|
| Framework | Python + FastAPI |
| Validación y DTOs | Pydantic v2 |
| ORM | SQLAlchemy |
| Base de datos | PostgreSQL |
| Autenticación | JWT + Bcrypt (`passlib`, prefijo `$2b$`) |
| Almacenamiento multimedia | Cloudinary CDN |
| Tests | pytest + httpx |

### Infraestructura

| Componente | Tecnología |
|---|---|
| Contenedores (local) | Docker + Docker Compose |
| Hosting API (producción) | Render |
| Hosting BD (producción) | Supabase (PostgreSQL gestionado) |
| Datos iniciales | Script `init.sql` |

> **Nota sobre Render:** el plan gratuito suspende el servicio tras 15 minutos de inactividad. La primera petición después de un periodo sin uso puede tardar unos segundos en responder por el arranque en frío.

---

## Endpoints de la API

```
POST   /register                  →  Registro de nuevos usuarios
POST   /login                     →  Login y obtención de token JWT
GET    /canales                   →  Listado de categorías desde la BD
GET    /noticias                  →  Feed público (solo aprobadas)
POST   /noticias                  →  Crear reporte (autenticado)
GET    /noticias/pendientes       →  Reportes sin revisar [Admin]
PATCH  /noticias/{id}/estado      →  Aprobar reporte [Admin]
DELETE /noticias/{id}             →  Eliminar reporte rechazado [Admin]
```

La documentación interactiva completa está disponible en `/docs` una vez levantado el servidor (Swagger/OpenAPI autogenerado por FastAPI).

---

## Instalación

### Requisitos previos

- Docker Desktop instalado y corriendo
- Android Studio con un emulador configurado, o dispositivo físico Android 8.0+
- Conexión a internet activa (MapBox y Cloudinary la necesitan)

Probado en Linux y Windows 10/11 con WSL2.

---

### 1. Levantar el backend en local

```bash
cd Civinews-backend

# Solo si quieres partir de cero y limpiar datos anteriores
# Esto elimina el volumen de PostgreSQL con todos sus datos
sudo docker compose down -v

# Levanta los contenedores en segundo plano (API + PostgreSQL)
sudo docker compose up -d
```

Al arrancar, Docker ejecuta `init.sql` automáticamente: crea las tablas, las categorías y los usuarios de prueba. Cuando termine, la documentación de la API estará disponible en:

```
http://localhost:8000/docs
```

---

### 2. Despliegue en producción (Render + Supabase)

**Base de datos (Supabase)**

Crear una instancia PostgreSQL en Supabase, obtener la cadena de conexión desde el panel e inicializar el esquema ejecutando los scripts SQL del proyecto desde el editor integrado o desde DBeaver.

**API (Render)**

Conectar el repositorio de GitHub en Render y configurar el comando de inicio:

```bash
uvicorn main:app --host 0.0.0.0 --port 10000
```

Las variables de entorno sensibles (clave JWT, cadena de conexión, credenciales de Cloudinary) se declaran como Environment Variables en el panel de Render, nunca en el código fuente. Cada push a la rama principal desencadena un redespliegue automático.

---

### 3. Instalar la app Android

**Opción A — Emulador**

1. Abre Android Studio y lanza el emulador
2. Arrastra el archivo `Demo_CiviNews_TFG_AntonioDelRio.apk` sobre la ventana del emulador
3. Abre la app

**Opción B — Dispositivo físico**

El dispositivo tiene que estar en la misma red Wi-Fi que el servidor si se usa el entorno local. En ese caso también hay que actualizar la IP base en el cliente Retrofit antes de compilar.

Si el backend ya está desplegado en Render, esto no aplica: la app apunta directamente al dominio de producción.

---

## Credenciales de prueba

| Rol | Email | Contraseña | Acceso |
|---|---|---|---|
| Administrador | `admin@civinews.com` | `123456` | Panel de moderación + todas las funciones |
| Ciudadano | `test@test.com` | `123456` | Feed y creación de reportes |

La base de datos viene con reportes de ejemplo en las categorías Alerta Bulos, Gasto Público e Infraestructuras para que se pueda probar el flujo completo de moderación desde el primer arranque.

---

## Roadmap

- [ ] Despliegue en Google Play Store
- [ ] Ampliación del panel de administración: asignación de roles y baneo directo de usuarios
- [ ] Reactivar el autocompletado predictivo de direcciones en el mapa (pausado para optimizar el consumo de tokens de la API de MapBox)
- [ ] Cobertura de tests automatizados completa con pytest
- [ ] Documentación técnica del código fuente del cliente Android
- [ ] Integración de IA para análisis de patrones en las incidencias reportadas

---

*CiviNews — TFG 2025/2026 · Antonio Javier del Río Ramos · IES Portada Alta, Málaga*
