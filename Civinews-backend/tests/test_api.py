import pytest
import requests
import uuid

# ==========================================
# 1. AUTH & REGISTRO (/register, /login, /users/forgot-password)
# ==========================================

def test_registro_usuario_duplicado(api_url, test_user_data, cliente_ciudadano):
    # Falla al registrar un correo que ya existe
    r = requests.post(f"{api_url}/register", json=test_user_data)
    assert r.status_code == 400

def test_login_ok(api_url, test_user_data):
    # Login correcto devuelve el token
    payload = {"email": test_user_data["email"], "password": test_user_data["password"]}
    r = requests.post(f"{api_url}/login", json=payload)
    assert r.status_code == 200
    assert "access_token" in r.json()

def test_login_credenciales_erroneas(api_url):
    # Bloquea acceso con mala contraseña
    payload = {"email": "inventado@civinews.com", "password": "fake"}
    r = requests.post(f"{api_url}/login", json=payload)
    assert r.status_code == 401

def test_recuperar_contrasena(api_url, test_user_data):
    # Envío de correo temporal sin exponer si el usuario existe
    payload = {"email": test_user_data["email"]}
    r = requests.post(f"{api_url}/users/forgot-password", json=payload)
    assert r.status_code == 200

# ==========================================
# 2. PERFIL DE USUARIO (/users/me)
# ==========================================

def test_obtener_perfil_propio(api_url, cliente_ciudadano, test_user_data):
    # Devuelve los datos correctos del usuario logueado
    r = cliente_ciudadano.get(f"{api_url}/users/me")
    assert r.status_code == 200
    assert r.json()["email"] == test_user_data["email"]

def test_actualizar_alias(api_url, cliente_ciudadano):
    # Permite cambiar el alias si no hay cooldown
    payload = {"name": "SuperCiudadano99"}
    r = cliente_ciudadano.patch(f"{api_url}/users/me/name", json=payload)
    assert r.status_code == 200
    assert r.json()["name"] == "SuperCiudadano99"

def test_actualizar_alias_cooldown(api_url, cliente_ciudadano):
    # Bloquea el cambio de alias antes de 14 días
    payload = {"name": "Spammer"}
    r = cliente_ciudadano.patch(f"{api_url}/users/me/name", json=payload)
    assert r.status_code == 429

def test_cambiar_contrasena_incorrecta(api_url, cliente_ciudadano):
    # Valida que pide la clave antigua correctamente
    payload = {"old_password": "Fail!", "new_password": "NuevaPassword456!"}
    r = cliente_ciudadano.patch(f"{api_url}/users/me/password", json=payload)
    assert r.status_code == 400

# ==========================================
# 3. NOTICIAS Y CANALES PÚBLICOS (/canales, /noticias, /noticias/mis-avisos)
# ==========================================

def test_obtener_canales(api_url, cliente_ciudadano):
    # Lista las categorías disponibles
    r = cliente_ciudadano.get(f"{api_url}/canales")
    assert r.status_code == 200

def test_obtener_feed_noticias(api_url, cliente_ciudadano):
    # Trae el feed principal solo con noticias aprobadas
    r = cliente_ciudadano.get(f"{api_url}/noticias")
    assert r.status_code == 200

def test_crear_noticia_y_limite(api_url, cliente_ciudadano):
    # Crea hasta 3 avisos y bloquea el 4º por spam
    payload = {
        "titulo": "Test Report", "contenido": "Contenido test",
        "categoria": "Infraestructuras", "ubicacion_texto": "Málaga",
        "latitud": 36.72, "longitud": -4.42, "imagen_url": ""
    }
    
    ids = []
    for _ in range(3):
        r = cliente_ciudadano.post(f"{api_url}/noticias", json=payload)
        if r.status_code == 200:
            ids.append(r.json()["id"])
            
    r_spam = cliente_ciudadano.post(f"{api_url}/noticias", json=payload)
    if len(ids) == 3:
        assert r_spam.status_code == 429
        
    pytest.noticia_id = ids[0] if ids else None

def test_obtener_mis_avisos(api_url, cliente_ciudadano):
    # Verifica que salen mis avisos pendientes en el historial
    r = cliente_ciudadano.get(f"{api_url}/noticias/mis-avisos")
    assert r.status_code == 200

def test_detalle_noticia(api_url, cliente_ciudadano):
    # Trae un aviso específico con autor y canal
    if not getattr(pytest, "noticia_id", None): pytest.skip()
    r = cliente_ciudadano.get(f"{api_url}/noticias/{pytest.noticia_id}")
    assert r.status_code == 200

# ==========================================
# 4. MODERACIÓN ADMIN (/noticias/pendientes, /noticias/{id}/estado, /noticias/{id})
# ==========================================

def test_admin_ve_pendientes(api_url, cliente_admin):
    # El admin puede ver la cola de pendientes
    r = cliente_admin.get(f"{api_url}/noticias/pendientes")
    assert r.status_code == 200

def test_admin_aprueba_noticia(api_url, cliente_admin):
    # El admin cambia el estado de un aviso a aprobado
    if not getattr(pytest, "noticia_id", None): pytest.skip()
    r = cliente_admin.patch(f"{api_url}/noticias/{pytest.noticia_id}/estado", json={"estado": "aprobada"})
    assert r.status_code == 200

def test_admin_borra_noticia(api_url, cliente_admin):
    # El admin elimina físicamente un reporte
    if not getattr(pytest, "noticia_id", None): pytest.skip()
    r = cliente_admin.delete(f"{api_url}/noticias/{pytest.noticia_id}")
    assert r.status_code == 200

# ==========================================
# 5. GESTIÓN DE USUARIOS ADMIN (/admin/users)
# ==========================================

def test_admin_lista_usuarios(api_url, cliente_admin):
    # El admin puede obtener todos los usuarios
    r = cliente_admin.get(f"{api_url}/admin/users")
    assert r.status_code == 200
    pytest.usuario_test_id = r.json()[0]["id"] # Guardamos uno para los siguientes tests

def test_admin_busca_usuarios(api_url, cliente_admin):
    # Filtro parcial por alias o correo
    r = cliente_admin.get(f"{api_url}/admin/users/search?query=test")
    assert r.status_code == 200

# ==========================================
# 6. SEGURIDAD CRUZADA Y LIMPIEZA
# ==========================================

def test_admin_cambia_rol(api_url, cliente_admin):
    # Concede permisos de admin a un usuario temporal para no romper el principal
    temp_user = {"email": f"rol_{uuid.uuid4().hex[:6]}@test.com", "username": "temp_admin", "password": "123"}
    requests.post(f"{api_url}/register", json=temp_user)
    
    # Buscamos su ID
    r_search = cliente_admin.get(f"{api_url}/admin/users/search?query={temp_user['email']}")
    uid = r_search.json()[0]["id"]
    
    r_role = cliente_admin.patch(f"{api_url}/admin/users/{uid}/role")
    assert r_role.status_code == 200
    assert r_role.json()["new_role"] is True

def test_admin_banea_usuario(api_url, cliente_admin):
    # Baneo físico borra en cascada la cuenta
    temp_user = {"email": f"ban_{uuid.uuid4().hex[:6]}@test.com", "username": "temp_ban", "password": "123"}
    requests.post(f"{api_url}/register", json=temp_user)
    
    r_search = cliente_admin.get(f"{api_url}/admin/users/search?query={temp_user['email']}")
    uid = r_search.json()[0]["id"]
    
    r_ban = cliente_admin.delete(f"{api_url}/admin/users/{uid}")
    assert r_ban.status_code == 200
    
# ==========================================
# 7. CASOS LÍMITE (EDGE CASES PARA 100% DE COBERTURA)
# ==========================================

def test_cambiar_contrasena_ok(api_url, cliente_ciudadano, test_user_data):
    # Verificamos que el cambio de clave funciona si ponemos bien la actual
    payload = {"old_password": test_user_data["password"], "new_password": "NuevaPassword123!"}
    r = cliente_ciudadano.patch(f"{api_url}/users/me/password", json=payload)
    assert r.status_code == 200

def test_detalle_noticia_no_encontrada(api_url, cliente_ciudadano):
    # Forzamos el error 404 buscando un UUID inventado
    fake_uuid = "123e4567-e89b-12d3-a456-426614174000"
    r = cliente_ciudadano.get(f"{api_url}/noticias/{fake_uuid}")
    assert r.status_code == 404

def test_admin_no_puede_quitarse_rol(api_url, cliente_admin):
    # El admin no puede revocarse permisos a sí mismo por error
    r_me = cliente_admin.get(f"{api_url}/users/me")
    mi_id = r_me.json()["id"]
    r = cliente_admin.patch(f"{api_url}/admin/users/{mi_id}/role")
    assert r.status_code == 400
    assert "propios permisos" in r.json()["detail"]

def test_admin_no_puede_banearse(api_url, cliente_admin):
    # El admin no puede autobanearse
    r_me = cliente_admin.get(f"{api_url}/users/me")
    mi_id = r_me.json()["id"]
    r = cliente_admin.delete(f"{api_url}/admin/users/{mi_id}")
    assert r.status_code == 400
    assert "banearte a ti mismo" in r.json()["detail"]