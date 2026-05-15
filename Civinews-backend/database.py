from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, declarative_base
from config import settings

# Configuración del motor de base de datos, sesión y base declarativa de SQLAlchemy

engine = create_engine(settings.database_url)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

# Generador de sesión inyectable en los endpoints, garantiza el cierre de la conexión tras cada petición
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()