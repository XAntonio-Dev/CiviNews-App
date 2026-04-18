# CiviNews — El futuro de las noticias locales

**Autor:** Antonio Javier del Río Ramos  
**Titulación:** 2º DAM — Desarrollo de Aplicaciones Multiplataforma  
**Tipo:** Trabajo de Fin de Grado (TFG)  
**Versión actual:** `1.1.0`

CiviNews es una aplicación Android para reportar y gestionar incidencias ciudadanas a nivel local. Los vecinos pueden publicar avisos geolocalizados (baches, alumbrado, eventos, bulos) y los administradores disponen de un panel para revisar y moderar ese contenido antes de que aparezca en el feed público.

---

## Índice

1. [Cómo funciona](#cómo-funciona)
2. [Arquitectura](#arquitectura)
3. [Pantallas y funcionalidades](#pantallas-y-funcionalidades)
4. [Ciclo de vida de una incidencia](#ciclo-de-vida-de-una-incidencia)
5. [Stack tecnológico](#stack-tecnológico)
6. [Instalación](#instalación)
7. [Credenciales de prueba](#credenciales-de-prueba)

---

## Cómo funciona

Un ciudadano detecta un problema en su barrio, abre la app, selecciona la categoría, marca el punto exacto en el mapa y adjunta una foto. Ese reporte llega al panel del administrador con estado pendiente. Si lo aprueba, aparece en el feed de todos los usuarios. Si lo rechaza, se elimina.

Las categorías no están escritas en el código. Se cargan desde la base de datos a través del endpoint `/canales`, por lo que si se añade una nueva en el servidor, la interfaz de creación la muestra automáticamente sin necesidad de actualizar la app.

---

## Arquitectura

```
┌─────────────────────────────────────────────────────────┐
│                     CLIENTE ANDROID                      │
│                                                          │
│   FeedScreen       AddReportScreen       AdminScreen     │
│   (ciudadano)       + MapBox SDK         (moderación)    │
│                                                          │
│              MVVM + Clean Architecture                   │
│              Dagger Hilt (inyección de dependencias)     │
└─────────────────────────┬───────────────────────────────┘
                          │ Retrofit (HTTP/JSON)
                          ▼
┌─────────────────────────────────────────────────────────┐
│                    BACKEND — FastAPI                     │
│                                                          │
│   Routers           Services            Schemas (DTO)    │
│   /noticias         Auth / Roles        Pydantic v2      │
│                                                          │
│                   SQLAlchemy ORM                         │
└──────────┬──────────────────────────┬───────────────────┘
           │                          │
           ▼                          ▼
    PostgreSQL                  Cloudinary CDN
    (volumen Docker)            (imágenes y multimedia)
```

El backend está separado en dos capas: `models.py` para los modelos de base de datos y `schemas.py` para los objetos de transferencia de datos (DTO). Así el frontend nunca recibe directamente la estructura interna de la BD.

Las imágenes se suben a Cloudinary en lugar de guardarlas en el servidor local. Principalmente porque llenar el disco del servidor con fotos es un problema real en cuanto haya algo de volumen, y un CDN resuelve eso de raíz además de que las imágenes cargan más rápido en el móvil.

Las contraseñas se hashean con Bcrypt usando el prefijo `$2b$`, compatible con `passlib`. No hay ninguna contraseña en texto plano en ningún punto del sistema.

---

## Pantallas y funcionalidades

### Pantalla ciudadana

| Funcionalidad | Descripción |
|---|---|
| Feed dinámico | Lista de reportes aprobados, actualizada desde la API |
| Filtrado por categorías | Chips generados desde el endpoint `/canales`, no hardcodeados |
| Creación de reportes | Categoría + descripción + foto + punto en el mapa |
| Estados de interfaz | Loading, Error, NoData y Success diferenciados |
| Temas | Modo claro y oscuro con Material Design 3 |
| Fechas relativas | "Hace 5 minutos", "Hace 2 horas" con `java.time` |

### Panel de administración

| Funcionalidad | Descripción |
|---|---|
| Lista de pendientes | Reportes sin revisar, accesibles solo con rol admin |
| Aprobar reporte | Cambia el estado a `aprobada` y lo publica en el feed |
| Rechazar reporte | Elimina el registro físicamente de la base de datos |
| Control de acceso | Los endpoints de moderación están protegidos por rol |

### Módulo de mapas

| Funcionalidad | Descripción |
|---|---|
| Mapa interactivo | MapBox SDK integrado en la pantalla de creación |
| Selección de ubicación | Marcador arrastrable que captura latitud y longitud exactas |
| Persistencia | Las coordenadas se guardan en PostgreSQL con cada reporte |

Guardar coordenadas reales en vez de solo texto abre la puerta a funcionalidades futuras como mapas de calor por zona o rutas de intervención.

---

## Ciclo de vida de una incidencia

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

Se usa `PATCH` en lugar de `PUT` para el cambio de estado porque solo se modifica un campo del recurso, no tiene sentido mandar el objeto entero para eso.

---

## Stack tecnológico

### Frontend — Android

| Componente | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| UI | Jetpack Compose — Material Design 3 |
| Arquitectura | MVVM / MVI + Clean Architecture |
| Inyección de dependencias | Dagger Hilt |
| Red | Retrofit + Gson |
| Imágenes | Coil (caché, placeholders, estados de error) |
| Mapas | MapBox SDK |
| Fechas | `java.time` |

### Backend — API REST

| Componente | Tecnología |
|---|---|
| Framework | Python + FastAPI |
| Validación y DTOs | Pydantic v2 (`schemas.py`) |
| ORM | SQLAlchemy |
| Base de datos | PostgreSQL |
| Autenticación | JWT + Bcrypt (`passlib`, prefijo `$2b$`) |
| Almacenamiento multimedia | Cloudinary CDN |

### Infraestructura

| Componente | Tecnología |
|---|---|
| Contenedores | Docker + Docker Compose |
| Persistencia | Volúmenes PostgreSQL |
| Datos iniciales | Script `init.sql` |

### Endpoints disponibles

```
POST   /register                    →  Registro de nuevos usuarios
POST   /login                       →  Login y obtención de token JWT
GET    /canales                     →  Listado de categorías desde BD
GET    /noticias                    →  Feed público (solo aprobadas)
POST   /noticias                    →  Crear reporte
GET    /noticias/pendientes         →  Reportes sin revisar [Admin]
PATCH  /noticias/{id}/estado        →  Aprobar reporte [Admin]
DELETE /noticias/{id}               →  Eliminar reporte rechazado [Admin]
```

## Instalación

### Requisitos

- Docker Desktop instalado y corriendo.
- Emulador Android en Android Studio o dispositivo físico en la misma red.
- Conexión a internet en el emulador (MapBox y Cloudinary la necesitan).

Probado en Linux y Windows 10/11 con WSL2.

---

### 1. Levantar el backend

```bash
cd Civinews-backend

# Solo si quieres partir de cero y borrar datos anteriores
# OJO: esto elimina todos los datos del volumen de PostgreSQL
sudo docker compose down -v

# Levanta los contenedores en segundo plano
sudo docker compose up -d
```

Al arrancar, Docker ejecuta `init.sql` automáticamente: crea las tablas, las categorías y los usuarios de prueba. Cuando termine, la documentación de la API estará en `http://localhost:8000/docs`.

### 2. Instalar la app

1. Abre el emulador en Android Studio.
2. Arrastra el archivo `CiviNews.apk` (raíz del proyecto) sobre el emulador.
3. Abre la app. Está configurada para apuntar al backend local.

Si usas un dispositivo físico, tiene que estar en la misma red Wi-Fi que el servidor. En ese caso también hay que cambiar la IP base en el cliente Retrofit.

---

## Credenciales de prueba

| Rol | Email | Contraseña | Acceso |
|---|---|---|---|
| Administrador | `admin@civinews.com` | `123456` | Panel de moderación + todas las funciones |
| Ciudadano | `test@test.com` | `123456` | Feed y creación de reportes |

La base de datos viene con reportes de ejemplo en las categorías Bulos, Infraestructuras y Gasto Público para que se pueda probar el flujo completo de moderación desde el primer arranque.

---

*CiviNews — TFG 2025/2026 · Antonio Javier del Río Ramos*
