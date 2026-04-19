from datetime import datetime, timedelta
from jose import jwt
import bcrypt
from config import settings

def get_password_hash(password: str) -> str:
    # Encriptamos la contraseña con un salt seguro
    salt = bcrypt.gensalt()
    hashed = bcrypt.hashpw(password.encode('utf-8'), salt)
    return hashed.decode('utf-8')

def verify_password(plain_password: str, hashed_password: str) -> bool:
    # Validamos que la contraseña introducida coincida con el hash
    return bcrypt.checkpw(plain_password.encode('utf-8'), hashed_password.encode('utf-8'))

def create_access_token(data: dict):
    # Generamos el token inyectando las claves dinámicas
    to_encode = data.copy()
    expire = datetime.utcnow() + timedelta(minutes=settings.access_token_expire_minutes)
    to_encode.update({"exp": expire}) 
    return jwt.encode(to_encode, settings.secret_key, algorithm=settings.algorithm)