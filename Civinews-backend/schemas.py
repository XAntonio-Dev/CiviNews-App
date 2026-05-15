from pydantic import BaseModel, EmailStr
from typing import Optional
from uuid import UUID
from datetime import datetime

# Esquemas Pydantic para la validación y transferencia de datos entre el cliente y la API

class LoginRequest(BaseModel):
    email: str
    password: str

class UserCreate(BaseModel):
    email: EmailStr
    username: str
    password: str

class ReportResponse(BaseModel):
    id: UUID
    titulo: str
    contenido: str
    imagen_url: Optional[str] = None
    estado: str
    ubicacion: Optional[str] = None
    fecha_creacion: datetime
    canal_id: Optional[int] = None
    autor_id: Optional[UUID] = None

    class Config:
        from_attributes = True

class ReportCreate(BaseModel):
    titulo: str
    contenido: str
    categoria: str
    ubicacion_texto: str
    latitud: float
    longitud: float
    imagen_url: Optional[str] = None

class CanalResponse(BaseModel):
    id: int
    nombre: str

    class Config:
        from_attributes = True

class StatusUpdate(BaseModel):
    estado: str

class NameUpdateSchema(BaseModel):
    name: str
    
class ReportDetailResponse(BaseModel):
    id: UUID
    titulo: str
    contenido: str
    imagen_url: Optional[str] = None
    estado: str
    ubicacion: Optional[str] = None
    latitud: Optional[float] = None  
    longitud: Optional[float] = None
    fecha_creacion: datetime
    autor_nombre: Optional[str] = None
    canal_nombre: Optional[str] = None

class Config:
        from_attributes = True
        
class PasswordChangeSchema(BaseModel):
    old_password: str
    new_password: str

class ForgotPasswordSchema(BaseModel):
    email: EmailStr