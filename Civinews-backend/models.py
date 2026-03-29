from sqlalchemy import Column, String, Boolean, DateTime, Text, ForeignKey, Integer
from sqlalchemy.dialects.postgresql import UUID
import uuid
from datetime import datetime
from database import Base

class Usuario(Base):
    __tablename__ = "usuarios"
    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    alias = Column(String(50), nullable=False)
    email = Column(String(100), unique=True, nullable=False)
    password_hash = Column(String(255), nullable=False)
    is_admin = Column(Boolean, default=False)
    fecha_registro = Column(DateTime, default=datetime.utcnow)

class Canal(Base):
    __tablename__ = "canales"
    id = Column(Integer, primary_key=True, index=True)
    nombre = Column(String(50), unique=True, nullable=False)
    descripcion = Column(Text)

class Noticia(Base):
    __tablename__ = "noticias"
    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    titulo = Column(String(100), nullable=False)
    contenido = Column(Text, nullable=False)
    imagen_url = Column(String(255))
    estado = Column(String, default="pendiente")
    ubicacion = Column(String(100))
    fecha_creacion = Column(DateTime, default=datetime.utcnow)
    
    autor_id = Column(UUID(as_uuid=True), ForeignKey("usuarios.id"))
    canal_id = Column(Integer, ForeignKey("canales.id"))