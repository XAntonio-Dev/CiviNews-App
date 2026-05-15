from fastapi import FastAPI, Depends, HTTPException, APIRouter
from sqlalchemy.orm import Session, joinedload
from typing import List
from uuid import UUID
from database import get_db
import models
import schemas
from schemas import NameUpdateSchema
from security import get_password_hash, verify_password, create_access_token
from dependencies import get_current_user, get_current_admin_user
from fastapi.middleware.cors import CORSMiddleware
from dependencies import get_current_user
from datetime import datetime, time

# Punto de entrada de la API. Aquí están registrados todos los endpoints de Civinews.

# ==========================================
# CORS
# ==========================================

app = FastAPI(title="Civinews API", version="0.1")

# Bloqueamos todo origen por ahora. Cuando despleguemos y añadamos un panel web hay que añadir el dominio aquí.
app.add_middleware(
    CORSMiddleware,
    allow_origins=[],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ==========================================
# RUTAS PÚBLICAS (No requieren Token)
# ==========================================

@app.post("/register",
    summary="Registro de usuario",
    description="Crea una nueva cuenta. Devuelve 400 si el email ya está en uso.")
def register(user_data: schemas.UserCreate, db: Session = Depends(get_db)):
    user_exists = db.query(models.User).filter(models.User.email == user_data.email).first()
    if user_exists: raise HTTPException(status_code=400, detail="El usuario ya existe")

    new_user = models.User(email=user_data.email, alias=user_data.username, password_hash=get_password_hash(user_data.password))
    db.add(new_user)
    db.commit()
    return {"status": "ok"}

@app.post("/login",
    summary="Inicio de sesión",
    description="Valida credenciales y devuelve el token JWT junto al rol del usuario para el enrutamiento del cliente.")
def login(credentials: schemas.LoginRequest, db: Session = Depends(get_db)):
    user = db.query(models.User).filter(models.User.email == credentials.email).first()
    if not user or not verify_password(credentials.password, user.password_hash):
        raise HTTPException(status_code=401, detail="Credenciales incorrectas")

    return {
        "access_token": create_access_token(data={"sub": user.email}),
        "token_type": "bearer",
        "user": {"username": user.alias, "email": user.email, "is_admin": user.is_admin}
    }

# ==========================================
# RUTAS CIUDADANOS (Requieren Token Normal)
# ==========================================

@app.get("/canales",
    response_model=List[schemas.CanalResponse],
    summary="Listado de canales",
    description="Devuelve todos los canales temáticos disponibles. El móvil los usa para pintar los chips del feed y el formulario de creación.")
def get_canales(db: Session = Depends(get_db), current_user: models.User = Depends(get_current_user)):
    return db.query(models.Canal).order_by(models.Canal.nombre).all()

@app.get("/noticias",
    response_model=List[schemas.ReportResponse],
    summary="Feed principal",
    description="Devuelve únicamente los reportes aprobados ordenados del más reciente al más antiguo. Los pendientes o rechazados no aparecen aquí.")
def get_noticias(db: Session = Depends(get_db), current_user: models.User = Depends(get_current_user)):
    return db.query(models.Noticia).filter(models.Noticia.estado == "aprobada").order_by(models.Noticia.fecha_creacion.desc()).all()

@app.post("/noticias",
    summary="Crear reporte",
    description="Publica un nuevo aviso ciudadano. Limitado a 3 envíos diarios por usuario para evitar spam antes de pasar por moderación. Devuelve 429 si se supera el límite.")
def create_noticia(
    report: schemas.ReportCreate,
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user)
):
    hoy_inicio = datetime.combine(datetime.utcnow().date(), time.min)
    reportes_hoy = db.query(models.Noticia).filter(
        models.Noticia.autor_id == current_user.id,
        models.Noticia.fecha_creacion >= hoy_inicio
    ).count()

    if reportes_hoy >= 3:
        raise HTTPException(
            status_code=429,
            detail="Has alcanzado el límite de 3 reportes diarios. ¡Vuelve mañana!"
        )

    canal = db.query(models.Canal).filter(models.Canal.nombre == report.categoria).first()
    if not canal:
        raise HTTPException(status_code=400, detail="Categoría no encontrada")

    nueva_noticia = models.Noticia(
        titulo=report.titulo,
        contenido=report.contenido,
        estado="pendiente",
        ubicacion=report.ubicacion_texto,
        latitud=report.latitud,
        longitud=report.longitud,
        imagen_url=report.imagen_url,
        canal_id=canal.id,
        autor_id=current_user.id
    )

    try:
        db.add(nueva_noticia)
        db.commit()
        db.refresh(nueva_noticia)
        return nueva_noticia
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"Error en la base de datos: {str(e)}")

@app.get("/noticias/mis-avisos",
    response_model=List[schemas.ReportResponse],
    summary="Mis avisos",
    description="Devuelve todos los reportes del usuario autenticado sin filtrar por estado, para que pueda hacer seguimiento de sus envíos.")
def get_mis_avisos(db: Session = Depends(get_db), current_user: models.User = Depends(get_current_user)):
    mis_noticias = db.query(models.Noticia)\
        .filter(models.Noticia.autor_id == current_user.id)\
        .order_by(models.Noticia.fecha_creacion.desc())\
        .all()
    return mis_noticias

@app.get("/noticias/{noticia_id}",
    response_model=schemas.ReportDetailResponse,
    summary="Detalle de reporte",
    description="Devuelve la información completa de un reporte incluyendo alias del autor y nombre del canal. Usa joinedload para resolverlo en una sola consulta a la BD.")
def get_noticia_detail(noticia_id: UUID, db: Session = Depends(get_db), current_user: models.User = Depends(get_current_user)):
    noticia = db.query(models.Noticia)\
        .options(joinedload(models.Noticia.autor), joinedload(models.Noticia.canal))\
        .filter(models.Noticia.id == noticia_id)\
        .first()

    if not noticia:
        raise HTTPException(status_code=404, detail="Aviso no encontrado")

    return {
        "id": noticia.id,
        "titulo": noticia.titulo,
        "contenido": noticia.contenido,
        "imagen_url": noticia.imagen_url,
        "estado": noticia.estado,
        "ubicacion": noticia.ubicacion,
        "latitud": noticia.latitud,
        "longitud": noticia.longitud,
        "fecha_creacion": noticia.fecha_creacion,
        "autor_nombre": noticia.autor.alias if noticia.autor else "Usuario Anónimo",
        "canal_nombre": noticia.canal.nombre if noticia.canal else "Sin categoría"
    }

# ==========================================
# RUTAS ADMINISTRADOR (Requieren Token Admin)
# ==========================================

@app.get("/noticias/pendientes",
    response_model=List[schemas.ReportResponse],
    summary="Cola de moderación",
    description="Devuelve los reportes pendientes de revisión ordenados del más antiguo al más reciente. Solo accesible para administradores.")
def get_noticias_pendientes(db: Session = Depends(get_db), admin_user: models.User = Depends(get_current_admin_user)):
    return db.query(models.Noticia).filter(models.Noticia.estado == "pendiente").order_by(models.Noticia.fecha_creacion.desc()).all()

@app.patch("/noticias/{noticia_id}/estado",
    summary="Moderar reporte",
    description="Actualiza el estado de un reporte a aprobada o rechazada desde el panel de administración.")
def update_noticia_estado(noticia_id: UUID, status_data: schemas.StatusUpdate, db: Session = Depends(get_db), admin_user: models.User = Depends(get_current_admin_user)):
    noticia = db.query(models.Noticia).filter(models.Noticia.id == noticia_id).first()
    if not noticia: raise HTTPException(status_code=404, detail="Noticia no encontrada")
    noticia.estado = status_data.estado
    db.commit()
    return {"status": "ok"}

@app.delete("/noticias/{noticia_id}",
    summary="Eliminar reporte",
    description="Borrado físico de un reporte de la base de datos. Sin papelera, se va para siempre.")
def delete_noticia(noticia_id: UUID, db: Session = Depends(get_db), admin_user: models.User = Depends(get_current_admin_user)):
    noticia = db.query(models.Noticia).filter(models.Noticia.id == noticia_id).first()
    if not noticia: raise HTTPException(status_code=404, detail="Noticia no encontrada")
    db.delete(noticia)
    db.commit()
    return {"status": "deleted"}

# ==========================================
# RUTAS USUARIO (Requieren Token Normal)
# ==========================================

@app.get("/users/me",
    summary="Perfil del usuario",
    description="Devuelve los datos del usuario autenticado. El móvil lo usa para pintar la pantalla de perfil.")
def get_user_profile(current_user: models.User = Depends(get_current_user)):
    return {
        "id": current_user.id,
        "name": current_user.alias,
        "email": current_user.email,
        "is_admin": current_user.is_admin
    }

@app.patch("/users/me/name",
    summary="Cambiar alias",
    description="Actualiza el alias público del usuario. Tiene un cooldown de 14 días entre cambios para evitar cambios continuos de identidad.")
def update_user_name(
    name_update: NameUpdateSchema,
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    new_name = name_update.name.strip()
    if not new_name:
        raise HTTPException(status_code=400, detail="El nombre no puede estar vacío")

    if current_user.fecha_ultimo_cambio_alias:
        dias_pasados = (datetime.utcnow() - current_user.fecha_ultimo_cambio_alias).days
        if dias_pasados < 14:
            dias_restantes = 14 - dias_pasados
            raise HTTPException(
                status_code=429,
                detail=f"Debes esperar {dias_restantes} días para volver a cambiar tu alias."
            )

    current_user.alias = new_name
    current_user.fecha_ultimo_cambio_alias = datetime.utcnow()
    db.commit()
    db.refresh(current_user)

    return {
        "id": current_user.id,
        "name": current_user.alias,
        "email": current_user.email,
        "is_admin": current_user.is_admin
    }

@app.patch("/users/me/password",
    summary="Cambiar contraseña",
    description="Cambia la contraseña verificando primero que la actual es correcta. Devuelve 400 si no coincide.")
def change_password(
    data: schemas.PasswordChangeSchema,
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    if not verify_password(data.old_password, current_user.password_hash):
        raise HTTPException(status_code=400, detail="La contraseña actual es incorrecta")

    current_user.password_hash = get_password_hash(data.new_password)
    db.commit()
    return {"status": "Contraseña actualizada correctamente"}

@app.delete("/users/me",
    summary="Eliminar cuenta",
    description="Borra permanentemente la cuenta del usuario. El CASCADE de PostgreSQL elimina también todos sus reportes asociados.")
def delete_user_account(
    current_user: models.User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    db.delete(current_user)
    db.commit()
    return {"status": "Cuenta eliminada permanentemente"}

@app.post("/users/forgot-password",
    summary="Recuperar contraseña",
    description="Inicia el flujo de recuperación de contraseña. Devuelve siempre el mismo mensaje exista o no el correo para evitar enumeración de usuarios.")
def forgot_password(
    data: schemas.ForgotPasswordSchema,
    db: Session = Depends(get_db)
):
    user = db.query(models.User).filter(models.User.email == data.email).first()

    if not user:
        return {"status": "Si el correo existe, se ha enviado un enlace de recuperación."}

    # TODO: integrar envío real de email con Mailtrap o similar
    return {"status": "Si el correo existe, se ha enviado un enlace de recuperación."}