# CiviNews — El futuro de las noticias locales

**Autor:** Antonio Javier del Río Ramos
**Curso:** 2º DAM (Desarrollo de Aplicaciones Multiplataforma)
**Proyecto:** Trabajo de Fin de Grado (TFG) — Entrega 1

CiviNews es una plataforma móvil para la gestión, visualización y reporte de incidencias y eventos ciudadanos a nivel local. Este repositorio corresponde a la primera entrega del proyecto e incluye el código completo del frontend (Android), el backend (FastAPI) y la configuración de base de datos.

---

## Funcionalidades implementadas (Entrega 1)

Esta primera fase cubre el flujo base de la aplicación con todo el stack conectado de extremo a extremo:

- **Autenticación:** Pantalla de login conectada al backend con validación de credenciales contra la base de datos.
- **Feed dinámico:** Listado de reportes obtenido en tiempo real desde la API REST.
- **Filtrado por categorías:** Sistema de chips para filtrar por "Seguridad", "Tráfico", "Eventos" y "Todos".
- **Gestión de imágenes:** Carga asíncrona mediante URLs con caché y manejo de estados de error y carga (Coil).
- **Estados de UI:** Pantallas diferenciadas para carga (`LoadingScreen`), error de conexión (`ErrorScreen`) y ausencia de datos (`NoDataScreen`).
- **Temas:** Modo claro y oscuro integrados con Material Design 3.
- **Internacionalización:** Todos los textos extraídos a `strings.xml`.

---

## Stack tecnológico

**Frontend (Android)**
- Lenguaje: Kotlin
- UI: Jetpack Compose (Material 3)
- Arquitectura: MVVM / MVI + Clean Architecture
- Inyección de dependencias: Dagger Hilt
- Red y multimedia: Retrofit, Gson, Coil

**Backend (API REST)**
- Framework: Python + FastAPI
- ORM: SQLAlchemy
- Base de datos: PostgreSQL

**Infraestructura**
- Contenedores: Docker y Docker Compose

---

## Estructura del repositorio
```
CiviNews/               → Código fuente del frontend (Android Studio)
Civinews-backend/       → Código fuente de la API REST (Python) + docker-compose.yml + init.sql
showcase/               → Vídeos de demostración y capturas de pantalla
CiviNews_Entrega1.apk   → APK precompilado para evaluación
```

---

## Instalación y ejecución

El entorno completo está dockerizado para facilitar la evaluación. La aplicación móvil viene precompilada y apunta al servidor local.

### 1. Levantar el backend y la base de datos

Desde la raíz del proyecto, accede a la carpeta del backend y lanza los servicios:
```bash
cd Civinews-backend
sudo docker compose up -d
```

Esto descargará las imágenes necesarias, levantará PostgreSQL, ejecutará el script `init.sql` con los datos iniciales y arrancará la API. Puedes verificar que funciona correctamente accediendo a `http://localhost:8000/docs`.

### 2. Instalar y probar la aplicación

Con el backend ya en marcha, no es necesario compilar el proyecto desde Android Studio:

1. Abre un emulador en Android Studio.
2. Arrastra el archivo `CiviNews_Entrega1.apk` (en la raíz del repositorio) sobre el emulador para instalarlo.
3. Abre la app CiviNews. La aplicación ya está configurada para comunicarse con el backend local.

### Credenciales de prueba
```
Email:      test@test.com
Contraseña: 1234
```
