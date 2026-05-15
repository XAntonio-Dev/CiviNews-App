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
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_ultimo_cambio_alias TIMESTAMP
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
    ubicacion VARCHAR(100),
    latitud FLOAT,  -- <--- COLUMNA NUEVA
    longitud FLOAT  -- <--- COLUMNA NUEVA
);

-- 4. ÍNDICES
CREATE INDEX IF NOT EXISTS idx_noticias_canal_estado ON noticias(canal_id, estado);
CREATE INDEX IF NOT EXISTS idx_noticias_fecha ON noticias(fecha_creacion DESC);

-- 5. DATOS DE PRUEBA
DO $$
DECLARE
    user_id UUID;
    c_trafico INT;
    c_infra INT;
    c_limpieza INT;
    c_seguridad INT;
    c_bulos INT;
    c_gasto INT;
    c_turismo INT;
    c_politica INT;
BEGIN
    -- Limpiamos por si ejecutamos el script varias veces (opcional, pero sano para desarrollo)
    TRUNCATE TABLE noticias CASCADE;
    
    -- Insertamos los 8 Canales
    INSERT INTO canales (nombre, descripcion) VALUES 
    ('Tráfico y Movilidad', 'Atascos, cortes de calle y transporte público'),
    ('Infraestructuras', 'Desperfectos en la vía, obras y asfalto'),
    ('Limpieza y Medio Ambiente', 'Gestión de residuos, parques y contaminación'),
    ('Seguridad Ciudadana', 'Emergencias, policía y problemas de convivencia'),
    ('Alerta Bulos', 'Fact-checking y desmentido de noticias falsas'),
    ('Gasto Público', 'Denuncias sobre obras paralizadas y despilfarro'),
    ('Turismo y Convivencia', 'Masificación, pisos turísticos y ruido'),
    ('Política Local', 'Plenos, protestas y decisiones del Ayuntamiento')
    ON CONFLICT (nombre) DO NOTHING;

    -- Capturamos los IDs de los canales para las noticias
    SELECT id INTO c_trafico FROM canales WHERE nombre = 'Tráfico y Movilidad';
    SELECT id INTO c_infra FROM canales WHERE nombre = 'Infraestructuras';
    SELECT id INTO c_limpieza FROM canales WHERE nombre = 'Limpieza y Medio Ambiente';
    SELECT id INTO c_seguridad FROM canales WHERE nombre = 'Seguridad Ciudadana';
    SELECT id INTO c_bulos FROM canales WHERE nombre = 'Alerta Bulos';
    SELECT id INTO c_gasto FROM canales WHERE nombre = 'Gasto Público';
    SELECT id INTO c_turismo FROM canales WHERE nombre = 'Turismo y Convivencia';
    SELECT id INTO c_politica FROM canales WHERE nombre = 'Política Local';

    -- Usuarios Admin (contraseña: 123456)
    INSERT INTO usuarios (alias, email, password_hash, is_admin) 
    VALUES ('Admin Principal', 'admin@civinews.com', '$2b$12$xThmKKH9G1QW0C/7PuzIH.ojnILPcmPxEB13maTBpSKOiT62t7KMq', TRUE)
    RETURNING id INTO user_id;

    INSERT INTO usuarios (alias, email, password_hash, is_admin) VALUES 
    ('Moderador Noche', 'mod@civinews.com', '$2b$12$xThmKKH9G1QW0C/7PuzIH.ojnILPcmPxEB13maTBpSKOiT62t7KMq', TRUE),
    ('Alcaldía', 'alcaldia@civinews.com', '$2b$12$xThmKKH9G1QW0C/7PuzIH.ojnILPcmPxEB13maTBpSKOiT62t7KMq', TRUE),
    ('test', 'test@test.com', '$2b$12$xThmKKH9G1QW0C/7PuzIH.ojnILPcmPxEB13maTBpSKOiT62t7KMq', FALSE);

    -- Insertamos las 8 noticias fundadoras (Con LATITUD y LONGITUD en 3 de ellas)
    INSERT INTO noticias (titulo, contenido, imagen_url, estado, autor_id, canal_id, ubicacion, latitud, longitud) VALUES
    (
        'Colapso total en hora punta', 
        'La A-357 está totalmente atascada dirección al PTA. Llevamos más de 40 minutos parados y no hay información sobre si es un accidente.', 
        'https://res.cloudinary.com/dtecznnri/image/upload/v1776110255/atasco_Coches_z5erk4.jpg', 
        'aprobada', user_id, c_trafico, 'A-357, Málaga', 36.7335, -4.5126 -- <-- A-357 hacia el PTA
    ),
    (
        'Socavón tremendo sin señalizar', 
        'Ha aparecido este agujero gigante en mitad de la vía. Es un peligro brutal para las motos y los patinetes. El Ayuntamiento ni ha puesto vallas.', 
        'https://res.cloudinary.com/dtecznnri/image/upload/v1776110256/socavon_ViaPublica_h6mrsq.jpg', 
        'aprobada', user_id, c_infra, 'Avenida de Andalucía', NULL, NULL
    ),
    (
        'Vergüenza en la zona universitaria', 
        'Llevan tres días sin recoger la basura en esta calle. Los contenedores están a rebosar y con el calor el olor es insoportable.', 
        'https://res.cloudinary.com/dtecznnri/image/upload/v1776110256/ContenedorARebosar_DeBasura_n2sgdk.jpg', 
        'aprobada', user_id, c_limpieza, 'Teatinos', 36.7171, -4.4735 -- <-- Bulevar Louis Pasteur (Teatinos)
    ),
    (
        'Fuerte dispositivo policial nocturno', 
        'Anoche cortaron la calle principal con varios furgones y luces de emergencia. Los vecinos estamos preocupados, necesitamos más información.', 
        'https://res.cloudinary.com/dtecznnri/image/upload/v1776110256/cochePolicia_ViaPublica_LucesEncendidas_ahozbb.jpg', 
        'aprobada', user_id, c_seguridad, 'El Palo', NULL, NULL
    ),
    (
        'FALSO: No van a cobrar por pasear por el centro', 
        'Está circulando por WhatsApp que el Ayuntamiento pondrá un peaje peatonal para residentes. Es un BULO. Confirmado por la alcaldía.', 
        'https://res.cloudinary.com/dtecznnri/image/upload/v1776110256/fakesNews_selloSobrePapel_hearc7.jpg', 
        'aprobada', user_id, c_bulos, 'Todo el municipio', NULL, NULL
    ),
    (
        'Obras del centro cívico, abandonadas', 
        'Otro proyecto millonario que se queda a medias. Las obras llevan paralizadas meses, la estructura está cogiendo humedad y el dinero público tirado.', 
        'https://res.cloudinary.com/dtecznnri/image/upload/v1776110256/obraSinTerminar_EnMedioDeLaCalle_rcvfns.jpg', 
        'aprobada', user_id, c_gasto, 'Calle Victoria', NULL, NULL
    ),
    (
        'Saturación en pleno centro', 
        'Ya es imposible pasear tranquilamente un martes por la tarde. Filas enormes, terrazas que invaden la calle y ruido constante.', 
        'https://res.cloudinary.com/dtecznnri/image/upload/v1776110256/calleLarios_ConUnosPocosGuirisAndando_rw3k7v.jpg', 
        'aprobada', user_id, c_turismo, 'Calle Marqués de Larios', 36.7189, -4.4211 -- <-- Calle Larios centro
    ),
    (
        'Concentración por la vivienda', 
        'Varios colectivos se han manifestado de espaldas a los furgones para pedir una regulación urgente de los alquileres.', 
        'https://res.cloudinary.com/dtecznnri/image/upload/v1776110257/policiaMirandoDeEspaldas_ManifestacionSinContexto_xzpwdc.jpg', 
        'aprobada', user_id, c_politica, 'Plaza de la Constitución', NULL, NULL
    );

    -- Registros de prueba pendientes
    INSERT INTO noticias (titulo, contenido, estado, ubicacion, imagen_url, canal_id) VALUES 
        (
            'Falso aviso de corte de agua', 
            'Circula un mensaje por WhatsApp sobre un corte de agua por supuesta contaminación. La empresa municipal ya lo ha desmentido, ruego que se publique para frenar la alarma.', 
            'pendiente', 
            'Redes Sociales', 
            'https://res.cloudinary.com/dtecznnri/image/upload/v1776536467/corteAgua_qdpffz.jpg', 
            5
        ),
        (
            'Candados turísticos en patrimonio', 
            'Han llenado la verja histórica de la plaza con cajetines para llaves de apartamentos turísticos. Dañan el hierro y hay ruido de maletas de madrugada.', 
            'pendiente', 
            'Plaza de la Merced', 
            'https://res.cloudinary.com/dtecznnri/image/upload/v1776536466/candadosTuristicos_ewfgzc.jpg', 
            7
        ),
        (
            'Exceso de farolas "de dos en dos"', 
            'Es absurdo el despliegue en la zona nueva de la Universidad. Han puesto farolas dobles en cada punto, hay muchísimas y están fatal colocadas. Un gasto público totalmente innecesario para esa zona.', 
            'pendiente', 
            'Ampliación de Teatinos (UMA)', 
            'https://res.cloudinary.com/dtecznnri/image/upload/v1776536467/farolasEncendidas_ygcduc.png', 
            6
        );
END $$;