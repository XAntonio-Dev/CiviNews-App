from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session
from pydantic import BaseModel
from uuid import UUID
from datetime import datetime
from typing import List, Optional
from database import get_db
import models

app = FastAPI(title="Civinews API", version="0.1")

# Esquema para el login
class LoginRequest(BaseModel):
    email: str
    password: str

# Esquema para la respuesta de noticias (REPORTES)
class ReportResponse(BaseModel):
    id: UUID
    titulo: str
    contenido: str
    imagen_url: Optional[str] = None
    estado: str
    ubicacion: Optional[str] = None
    fecha_creacion: datetime
    canal_id: Optional[int] = None

    class Config:
        from_attributes = True

@app.post("/login")
def login(request: LoginRequest, db: Session = Depends(get_db)):
    usuario = db.query(models.Usuario).filter(models.Usuario.email == request.email).first()
    if not usuario or request.password != "1234":
        raise HTTPException(status_code=401, detail="Credenciales incorrectas")
    return {
        "access_token": "token_jwt_simulado_123",
        "token_type": "bearer",
        "is_admin": usuario.is_admin
    }

@app.get("/noticias", response_model=List[ReportResponse])
def get_noticias(db: Session = Depends(get_db)):
    # Traemos las noticias aprobadas para el feed
    return db.query(models.Noticia).order_by(models.Noticia.fecha_creacion.desc()).all()