from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import List
from uuid import UUID
from database import get_db
import models
import schemas
from security import get_password_hash, verify_password, create_access_token
from dependencies import get_current_user, get_current_admin_user

app = FastAPI(title="Civinews API", version="0.1")

# ==========================================
# RUTAS PÚBLICAS (No requieren Token)
# ==========================================

@app.post("/register")
def register(user_data: schemas.UserCreate, db: Session = Depends(get_db)):
    user_exists = db.query(models.User).filter(models.User.email == user_data.email).first()
    if user_exists: raise HTTPException(status_code=400, detail="El usuario ya existe")
    
    new_user = models.User(email=user_data.email, alias=user_data.username, password_hash=get_password_hash(user_data.password))
    db.add(new_user)
    db.commit()
    return {"status": "ok"}

@app.post("/login")
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

@app.get("/canales", response_model=List[schemas.CanalResponse])
def get_canales(db: Session = Depends(get_db), current_user: models.User = Depends(get_current_user)):
    return db.query(models.Canal).order_by(models.Canal.nombre).all()

@app.get("/noticias", response_model=List[schemas.ReportResponse])
def get_noticias(db: Session = Depends(get_db), current_user: models.User = Depends(get_current_user)):
    return db.query(models.Noticia).filter(models.Noticia.estado == "aprobada").order_by(models.Noticia.fecha_creacion.desc()).all()

@app.post("/noticias")
def create_noticia(
    report: schemas.ReportCreate, 
    db: Session = Depends(get_db), 
    current_user: models.User = Depends(get_current_user)
):
    canal = db.query(models.Canal).filter(models.Canal.nombre == report.categoria).first()
    if not canal: 
        raise HTTPException(status_code=400, detail="Categoría no encontrada")

    nueva_noticia = models.Noticia(
        titulo=report.titulo, 
        contenido=report.contenido, 
        estado="pendiente", # Forzamos que entre como pendiente
        ubicacion=report.ubicacion_texto, 
        imagen_url=report.imagen_url, 
        canal_id=canal.id,
        autor_id=current_user.id # AQUÍ SE ASIGNA EL DNI DEL USUARIO
    )

    try:
        db.add(nueva_noticia)
        db.commit()
        db.refresh(nueva_noticia) 
        
        return nueva_noticia 
        
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"Error en la base de datos: {str(e)}")

@app.get("/noticias/mis-avisos", response_model=List[schemas.ReportResponse])
def get_mis_avisos(db: Session = Depends(get_db), current_user: models.User = Depends(get_current_user)):
    mis_noticias = db.query(models.Noticia)\
        .filter(models.Noticia.autor_id == current_user.id)\
        .order_by(models.Noticia.fecha_creacion.desc())\
        .all()
    return mis_noticias

# ==========================================
# RUTAS ADMINISTRADOR (Requieren Token Admin)
# ==========================================

@app.get("/noticias/pendientes", response_model=List[schemas.ReportResponse])
def get_noticias_pendientes(db: Session = Depends(get_db), admin_user: models.User = Depends(get_current_admin_user)):
    return db.query(models.Noticia).filter(models.Noticia.estado == "pendiente").order_by(models.Noticia.fecha_creacion.desc()).all()

@app.patch("/noticias/{noticia_id}/estado")
def update_noticia_estado(noticia_id: UUID, status_data: schemas.StatusUpdate, db: Session = Depends(get_db), admin_user: models.User = Depends(get_current_admin_user)):
    noticia = db.query(models.Noticia).filter(models.Noticia.id == noticia_id).first()
    if not noticia: raise HTTPException(status_code=404, detail="Noticia no encontrada")
    noticia.estado = status_data.estado
    db.commit()
    return {"status": "ok"}

@app.delete("/noticias/{noticia_id}")
def delete_noticia(noticia_id: UUID, db: Session = Depends(get_db), admin_user: models.User = Depends(get_current_admin_user)):
    noticia = db.query(models.Noticia).filter(models.Noticia.id == noticia_id).first()
    if not noticia: raise HTTPException(status_code=404, detail="Noticia no encontrada")
    db.delete(noticia)
    db.commit()
    return {"status": "deleted"}