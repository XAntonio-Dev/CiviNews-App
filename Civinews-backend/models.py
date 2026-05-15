from sqlalchemy import Column, Integer, String, Text, DateTime, ForeignKey, Float, Boolean
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import relationship
import uuid
from datetime import datetime
from database import Base

# Modelos ORM de SQLAlchemy que representan las tablas de la base de datos PostgreSQL

# Tabla usuarios: gestiona la identidad, autenticación y roles de acceso
class User(Base):
    __tablename__ = "usuarios"
    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    alias = Column(String(50), nullable=False)
    email = Column(String(100), unique=True, nullable=False)
    password_hash = Column(String(255), nullable=False)
    is_admin = Column(Boolean, default=False)
    fecha_registro = Column(DateTime, default=datetime.utcnow)
    fecha_ultimo_cambio_alias = Column(DateTime, nullable=True)

    noticias = relationship("Noticia", back_populates="autor")


# Tabla canales: categorías temáticas que clasifican los reportes ciudadanos
class Canal(Base):
    __tablename__ = "canales"
    id = Column(Integer, primary_key=True, index=True)
    nombre = Column(String(50), unique=True, nullable=False)
    descripcion = Column(Text)

    noticias = relationship("Noticia", back_populates="canal")


# Tabla noticias: almacena los reportes ciudadanos con su estado de moderación y coordenadas
class Noticia(Base):
    __tablename__ = "noticias"
    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    titulo = Column(String(100), nullable=False)
    contenido = Column(Text, nullable=False)
    imagen_url = Column(String(255))
    estado = Column(String, default="pendiente")
    ubicacion = Column(String(100))
    latitud = Column(Float, nullable=True)
    longitud = Column(Float, nullable=True)
    fecha_creacion = Column(DateTime, default=datetime.utcnow)

    autor_id = Column(UUID(as_uuid=True), ForeignKey("usuarios.id"))
    canal_id = Column(Integer, ForeignKey("canales.id"))

    autor = relationship("User", back_populates="noticias")
    canal = relationship("Canal", back_populates="noticias")