-- 1. Extensiones necesarias para UUID
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 2. ENUMS
DO $$ BEGIN
    CREATE TYPE estado_noticia AS ENUM ('pendiente', 'aprobada', 'rechazada');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- 3. TABLAS
CREATE TABLE IF NOT EXISTS usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    alias VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL CHECK (email LIKE '%@%'),
    password_hash VARCHAR(255) NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS canales (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion TEXT
);

CREATE TABLE IF NOT EXISTS noticias (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    titulo VARCHAR(100) NOT NULL,
    contenido TEXT NOT NULL,
    imagen_url VARCHAR(255),
    estado estado_noticia DEFAULT 'pendiente',
    autor_id UUID REFERENCES usuarios(id) ON DELETE CASCADE,
    canal_id INT REFERENCES canales(id) ON DELETE SET NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ubicacion VARCHAR(100) -- Añadido para que coincida con tu diseño de Stitch
);

-- 4. ÍNDICES
CREATE INDEX IF NOT EXISTS idx_noticias_canal_estado ON noticias(canal_id, estado);
CREATE INDEX IF NOT EXISTS idx_noticias_fecha ON noticias(fecha_creacion DESC);

-- 5. DATOS DE PRUEBA
-- Insertamos el usuario de prueba y guardamos su ID para las noticias
DO $$
DECLARE
    user_id UUID;
    canal_seguridad_id INT;
    canal_trafico_id INT;
    canal_eventos_id INT;
BEGIN
    -- Canales
    INSERT INTO canales (nombre, descripcion) VALUES 
    ('Seguridad', 'Avisos sobre robos y emergencias'),
    ('Eventos', 'Fiestas locales y cultura'),
    ('Tráfico', 'Cortes de calle y transporte')
    ON CONFLICT (nombre) DO NOTHING;

    -- Pillamos IDs de los canales
    SELECT id INTO canal_trafico_id FROM canales WHERE nombre = 'Tráfico';
    SELECT id INTO canal_eventos_id FROM canales WHERE nombre = 'Eventos';

    -- Usuario (si no existe, lo crea y pilla el ID)
    INSERT INTO usuarios (alias, email, password_hash, is_admin) 
    VALUES ('Admin Prueba', 'test@test.com', '1234', TRUE)
    ON CONFLICT (email) DO UPDATE SET email = EXCLUDED.email
    RETURNING id INTO user_id;

INSERT INTO noticias (titulo, contenido, imagen_url, estado, autor_id, canal_id, ubicacion) VALUES
(
    'Retrasos en Avenida Central', 
    'Obras en el carril derecho causando atascos significativos.', 
    'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957', -- Imagen de tráfico
    'aprobada', -- La ponemos aprobada para que salga en el feed
    user_id, 
    canal_trafico_id, 
    'Centro Histórico'
),
(
    'Festival Cultural de Verano', 
    'Cierres de calles programados para el evento principal en la plaza.', 
    'https://images.unsplash.com/photo-1533174072545-7a4b6ad7a6c3', -- Imagen de evento
    'aprobada', 
    user_id, 
    canal_eventos_id, 
    'Plaza Mayor'
);

END $$;