import os
import pytest
import requests
import uuid

@pytest.fixture(scope="session")
def api_url():
    # Enganchamos con la URL del docker-compose
    return os.getenv("API_BASE_URL", "http://localhost:8000")

@pytest.fixture(scope="session")
def test_user_data():
    # Genero datos aleatorios limpios para esta sesión de tests
    uid = str(uuid.uuid4())[:8]
    return {
        "email": f"test_{uid}@civinews.com",
        "username": f"alias_{uid}",
        "password": "Password123!"
    }

@pytest.fixture(scope="session")
def cliente_ciudadano(api_url, test_user_data):
    # 1. Registramos al usuario en la BD
    requests.post(f"{api_url}/register", json=test_user_data)
    
    # 2. Hacemos login
    payload_login = {
        "email": test_user_data["email"],
        "password": test_user_data["password"]
    }
    r = requests.post(f"{api_url}/login", json=payload_login)
    r.raise_for_status()
    
    # 3. Inyectamos el Bearer token en la sesión
    token = r.json().get("access_token")
    session = requests.Session()
    session.headers.update({"Authorization": f"Bearer {token}"})
    
    return session

@pytest.fixture(scope="session")
def cliente_admin(api_url):
    # Uso las credenciales del admin inicial de la BD
    payload_login = {
        "email": "admin@civinews.com",
        "password": "123456"           
    }
    r = requests.post(f"{api_url}/login", json=payload_login)
    
    r.raise_for_status()
    
    token = r.json().get("access_token")
    session = requests.Session()
    session.headers.update({"Authorization": f"Bearer {token}"})
    
    return session