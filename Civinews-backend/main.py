from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session, joinedload
from sqlalchemy import or_
from typing import List
from uuid import UUID
from datetime import datetime, time
import string
import secrets
from database import get_db
import models
import schemas
from security import get_password_hash, verify_password, create_access_token
from dependencies import get_current_user, get_current_admin_user
from fastapi.middleware.cors import CORSMiddleware
from mailtrap import send_mailtrap_email

app = FastAPI(title="Civinews API", version="0.1")

# Bloqueo peticiones web externas por seguridad. La app nativa no lo necesita.
app.add_middleware(
    CORSMiddleware,
    allow_origins=[],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# --- RUTAS PÚBLICAS ---

@app.post("/register", summary="Registro de usuario")
def register(user_data: schemas.UserCreate, db: Session = Depends(get_db)):
    # Compruebo si el correo ya existe antes de insertar
    user_exists = db.query(models.User).filter(models.User.email == user_data.email).first()
    if user_exists: 
        raise HTTPException(status_code=400, detail="El usuario ya existe")

    new_user = models.User(
        email=user_data.email, 
        alias=user_data.username, 
        password_hash=get_password_hash(user_data.password)
    )
    db.add(new_user)
    db.commit()

    # Mando un correo de bienvenida al registrarse
    send_mailtrap_email(
        destinatario=user_data.email,
        asunto="Bienvenido a CiviNews - Cuenta verificada",
        cuerpo=f"Hola {user_data.username},\n\nTu cuenta ha sido creada con éxito. Ya puedes reportar incidencias."
    )
    
    return {"status": "ok"}

@app.post("/login", summary="Inicio de sesión")
def login(credentials: schemas.LoginRequest, db: Session = Depends(get_db)):
    # Verifico el usuario y si la contraseña hace match con el hash
    user = db.query(models.User).filter(models.User.email == credentials.email).first()
    if not user or not verify_password(credentials.password, user.password_hash):
        raise HTTPException(status_code=401, detail="Credenciales incorrectas")

    # Añado el rol al token para que Compose decida a qué pantalla navegar
    return {
        "access_token": create_access_token(data={"sub": user.email}),
        "token_type": "bearer",
        "user": {"username": user.alias, "email": user.email, "is_admin": user.is_admin}
    }

# --- RUTAS CIUDADANO ---

@app.get("/canales", response_model=List[schemas.CanalResponse], summary="Listado de canales")
def get_canales(db: Session = Depends(get_db), current_user: models.User = Depends(get_current_user)):
    return db.query(models.Canal).order_by(models.Canal.nombre).all()

@app.get("/noticias", response_model=List[schemas.ReportResponse], summary="Feed principal")
def get_noticias(db: Session = Depends(get_db), current_user: models.User = Depends(get_current_user)):
    # Solo devuelvo las noticias que los admins ya han aprobado
    return db.query(models.Noticia).filter(models.Noticia.estado == "aprobada").order_by(models.Noticia.fecha_creacion.desc()).all()

@app.post("/noticias", summary="Crear reporte")
def create_noticia(report: schemas.ReportCreate, db: Session = Depends(get_db), current_user: models.User = Depends(get_current_user)):
    # Limito a 3 reportes al día para evitar spam en la BD
    hoy_inicio = datetime.combine(datetime.utcnow().date(), time.min)
    reportes_hoy = db.query(models.Noticia).filter(
        models.Noticia.autor_id == current_user.id,
        models.Noticia.fecha_creacion >= hoy_inicio
    ).count()

    if reportes_hoy >= 3:
        raise HTTPException(status_code=429, detail="Límite de 3 reportes diarios alcanzado.")

    canal = db.query(models.Canal).filter(models.Canal.nombre == report.categoria).first()
    if not canal:
        raise HTTPException(status_code=400, detail="Categoría inválida")

    nueva_noticia = models.Noticia(
        titulo=report.titulo, contenido=report.contenido, estado="pendiente",
        ubicacion=report.ubicacion_texto, latitud=report.latitud, longitud=report.longitud,
        imagen_url=report.imagen_url, canal_id=canal.id, autor_id=current_user.id
    )

    try:
        db.add(nueva_noticia)
        db.commit()
        db.refresh(nueva_noticia)
        return nueva_noticia
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail="Error interno en la base de datos")

@app.get("/noticias/mis-avisos", response_model=List[schemas.ReportResponse], summary="Historial del usuario")
def get_mis_avisos(db: Session = Depends(get_db), current_user: models.User = Depends(get_current_user)):
    return db.query(models.Noticia).filter(models.Noticia.autor_id == current_user.id).order_by(models.Noticia.fecha_creacion.desc()).all()

@app.get("/noticias/pendientes", response_model=List[schemas.ReportResponse], summary="Cola de moderación")
def get_noticias_pendientes(db: Session = Depends(get_db), admin_user: models.User = Depends(get_current_admin_user)):
    return db.query(models.Noticia).filter(models.Noticia.estado == "pendiente").order_by(models.Noticia.fecha_creacion.desc()).all()

@app.get("/noticias/{noticia_id}", response_model=schemas.ReportDetailResponse, summary="Detalle del reporte")
def get_noticia_detail(noticia_id: UUID, db: Session = Depends(get_db), current_user: models.User = Depends(get_current_user)):
    # Uso joinedload para traerme el autor y el canal en la misma consulta
    noticia = db.query(models.Noticia).options(joinedload(models.Noticia.autor), joinedload(models.Noticia.canal)).filter(models.Noticia.id == noticia_id).first()
    if not noticia: raise HTTPException(status_code=404, detail="Aviso no encontrado")

    return {
        "id": noticia.id, "titulo": noticia.titulo, "contenido": noticia.contenido,
        "imagen_url": noticia.imagen_url, "estado": noticia.estado, "ubicacion": noticia.ubicacion,
        "latitud": noticia.latitud, "longitud": noticia.longitud, "fecha_creacion": noticia.fecha_creacion,
        "autor_nombre": noticia.autor.alias if noticia.autor else "Anónimo",
        "canal_nombre": noticia.canal.nombre if noticia.canal else "Sin categoría"
    }

# --- PERFIL DE USUARIO ---

@app.get("/users/me", response_model=schemas.UserResponse, summary="Datos del usuario")
def get_user_profile(current_user: models.User = Depends(get_current_user)):
    # Al devolver el current_user, FastAPI lee el response_model y lo formatea automáticamente con el 'alias'
    return current_user

@app.patch("/users/me/name", summary="Actualizar alias")
def update_user_name(name_update: schemas.NameUpdateSchema, current_user: models.User = Depends(get_current_user), db: Session = Depends(get_db)):
    # Impido que cambien de nombre constantemente (cooldown de 14 días)
    new_name = name_update.name.strip()
    if not new_name: raise HTTPException(status_code=400, detail="Alias no válido")

    if current_user.fecha_ultimo_cambio_alias:
        dias_pasados = (datetime.utcnow() - current_user.fecha_ultimo_cambio_alias).days
        if dias_pasados < 14:
            raise HTTPException(status_code=429, detail=f"Espera {14 - dias_pasados} días para volver a cambiarlo.")

    current_user.alias = new_name
    current_user.fecha_ultimo_cambio_alias = datetime.utcnow()
    db.commit()
    db.refresh(current_user)
    return {"id": current_user.id, "name": current_user.alias, "email": current_user.email, "is_admin": current_user.is_admin}

@app.patch("/users/me/password", summary="Cambio de contraseña")
def change_password(data: schemas.PasswordChangeSchema, current_user: models.User = Depends(get_current_user), db: Session = Depends(get_db)):
    if not verify_password(data.old_password, current_user.password_hash):
        raise HTTPException(status_code=400, detail="Contraseña actual incorrecta")

    current_user.password_hash = get_password_hash(data.new_password)
    db.commit()
    return {"status": "Contraseña actualizada"}

@app.delete("/users/me", summary="Borrar cuenta propia")
def delete_user_account(current_user: models.User = Depends(get_current_user), db: Session = Depends(get_db)):
    db.delete(current_user)
    db.commit()
    return {"status": "Cuenta eliminada"}

@app.post("/users/forgot-password", summary="Recuperar contraseña")
def forgot_password(data: schemas.ForgotPasswordSchema, db: Session = Depends(get_db)):
    user = db.query(models.User).filter(models.User.email == data.email).first()

    if user:
        # Genero una clave temporal segura de 8 caracteres porque no tenemos entorno web
        alfabeto = string.ascii_letters + string.digits
        temp_password = ''.join(secrets.choice(alfabeto) for _ in range(8))
        
        user.password_hash = get_password_hash(temp_password)
        db.commit()

        # Le mando la clave generada al correo
        send_mailtrap_email(
            destinatario=user.email,
            asunto="Tu nueva contraseña temporal - CiviNews",
            cuerpo=f"Hola {user.alias},\n\nHemos restablecido tu acceso. Tu contraseña temporal es: {temp_password}\n\nInicia sesión en la aplicación con esta clave y cámbiala en tu perfil lo antes posible."
        )

    # Mensaje estándar para no filtrar si el correo existe o no en la base de datos
    return {"status": "Si el correo existe, se han enviado las instrucciones de recuperación."}

# --- ADMINISTRADOR ---

@app.patch("/noticias/{noticia_id}/estado", summary="Moderar un reporte")
def update_noticia_estado(noticia_id: UUID, status_data: schemas.StatusUpdate, db: Session = Depends(get_db), admin_user: models.User = Depends(get_current_admin_user)):
    noticia = db.query(models.Noticia).filter(models.Noticia.id == noticia_id).first()
    if not noticia: raise HTTPException(status_code=404, detail="Noticia no encontrada")
    
    noticia.estado = status_data.estado
    db.commit()
    return {"status": "ok"}

@app.delete("/noticias/{noticia_id}", summary="Borrado físico de reporte")
def delete_noticia(noticia_id: UUID, db: Session = Depends(get_db), admin_user: models.User = Depends(get_current_admin_user)):
    noticia = db.query(models.Noticia).filter(models.Noticia.id == noticia_id).first()
    if not noticia: raise HTTPException(status_code=404, detail="Noticia no encontrada")
    
    db.delete(noticia)
    db.commit()
    return {"status": "deleted"}

@app.get("/admin/users", response_model=List[schemas.UserResponse], summary="Listado completo de usuarios")
def get_all_users(db: Session = Depends(get_db), admin_user: models.User = Depends(get_current_admin_user)):
    return db.query(models.User).all()

@app.patch("/admin/users/{user_id}/role", summary="Conceder/Revocar Admin")
def toggle_user_role(user_id: UUID, db: Session = Depends(get_db), admin_user: models.User = Depends(get_current_admin_user)):
    user = db.query(models.User).filter(models.User.id == user_id).first()
    if not user: raise HTTPException(status_code=404, detail="Usuario no encontrado")
    
    # Prevengo que me quite mis propios permisos por error
    if user.id == admin_user.id: raise HTTPException(status_code=400, detail="No puedes modificar tus propios permisos")

    user.is_admin = not user.is_admin
    db.commit()
    return {"status": "ok", "new_role": user.is_admin}

@app.delete("/admin/users/{user_id}", summary="Banear usuario")
def ban_user(user_id: UUID, db: Session = Depends(get_db), admin_user: models.User = Depends(get_current_admin_user)):
    user = db.query(models.User).filter(models.User.id == user_id).first()
    if not user: raise HTTPException(status_code=404, detail="Usuario no encontrado")
    
    if user.id == admin_user.id: raise HTTPException(status_code=400, detail="No puedes banearte a ti mismo")

    # Postgres borra en cascada los reportes de este usuario automáticamente
    db.delete(user)
    db.commit()
    return {"status": "banned"}

@app.get("/admin/users/search", response_model=List[schemas.UserResponse], summary="Buscar usuarios por alias o email")
def search_users(query: str, db: Session = Depends(get_db), admin_user: models.User = Depends(get_current_admin_user)):
    # Usamos 'ilike' de PostgreSQL para búsqueda parcial insensible a mayúsculas
    search_term = f"%{query}%"
    usuarios = db.query(models.User).filter(
        or_(
            models.User.alias.ilike(search_term),
            models.User.email.ilike(search_term)
        )
    ).order_by(models.User.alias).all()
    
    return usuarios