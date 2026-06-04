import smtplib
from email.message import EmailMessage
from config import settings

def send_mailtrap_email(destinatario: str, asunto: str, cuerpo: str):
    """
    Cliente SMTP para notificaciones. 
    Totalmente desacoplado de los endpoints de la API.
    """
    msg = EmailMessage()
    msg.set_content(cuerpo)
    msg['Subject'] = asunto
    msg['From'] = "no-reply@civinews.com"
    msg['To'] = destinatario

    # Consumimos directamente de los settings (Pydantic se asegura de que existan)
    user = settings.mailtrap_user
    password = settings.mailtrap_pass

    if not user or not password:
        print("Error: Credenciales de Mailtrap vacías en el .env", flush=True)
        return

    try:
        with smtplib.SMTP("sandbox.smtp.mailtrap.io", 2525) as server:
            server.login(user, password)
            server.send_message(msg)
            print(f"Éxito: Correo enviado a {destinatario} vía Mailtrap", flush=True)
    except Exception as e:
        print(f"Error crítico en SMTP al enviar a {destinatario}: {e}", flush=True)