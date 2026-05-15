# Utilidades de seguridad: hashing de contraseñas y generación de tokens JWT

from datetime import datetime, timedelta
from jose import jwt
import bcrypt
from config import settings

# Genera un hash seguro de la contraseña usando bcrypt con salt aleatorio
def get_password_hash(password: str) -> str:
    salt = bcrypt.gensalt()
    hashed = bcrypt.hashpw(password.encode('utf-8'), salt)
    return hashed.decode('utf-8')

# Compara una contraseña en texto plano con su hash almacenado en base de datos
def verify_password(plain_password: str, hashed_password: str) -> bool:
    return bcrypt.checkpw(plain_password.encode('utf-8'), hashed_password.encode('utf-8'))

# Crea y firma un token JWT con los datos del usuario y un tiempo de expiración configurado
def create_access_token(data: dict):
    to_encode = data.copy()
    expire = datetime.utcnow() + timedelta(minutes=settings.access_token_expire_minutes)
    to_encode.update({"exp": expire}) 
    return jwt.encode(to_encode, settings.secret_key, algorithm=settings.algorithm)