CREATE DATABASE db_veterinaria;
use db_veterinaria;

-- ===========================================
-- TABLA USUARIOS
-- ===========================================
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_completo VARCHAR(150) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(15) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    rol ENUM('usuario', 'admin') DEFAULT 'usuario',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    
);

-- ===========================================
-- TABLA MASCOTAS
-- ===========================================
CREATE TABLE mascotas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    especie ENUM('perro', 'gato', 'conejo', 'ave', 'otro') NOT NULL,
    raza VARCHAR(50) NOT NULL,
    genero ENUM('macho', 'hembra') NOT NULL,
    edad INT NOT NULL,
    tamano ENUM('pequeño', 'mediano', 'grande') NOT NULL,
    descripcion TEXT,
    foto_url VARCHAR(255),
     tipo ENUM('propia','adopcion') DEFAULT 'propia',
    estado_adopcion ENUM('disponible','adoptado','en_proceso','no_disponible') DEFAULT 'disponible',
    vacunado BOOLEAN DEFAULT FALSE,
    esterilizado BOOLEAN DEFAULT FALSE,
    bueno_con_ninos BOOLEAN DEFAULT FALSE,
    bueno_con_otras_mascotas BOOLEAN DEFAULT FALSE,
    fecha_ingreso DATE NOT NULL,
    fecha_adopcion DATE NULL,
    usuario_id INT NULL, -- Usuario que adoptó la mascota
    activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- ===========================================
-- TABLA ADOPCIONES
-- ===========================================
CREATE TABLE adopciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    mascota_id INT NOT NULL,
    fecha_solicitud TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_aprobacion TIMESTAMP NULL,
    estado ENUM('pendiente','aprobada','rechazada','completada') DEFAULT 'pendiente',
    -- Información del formulario
    experiencia_mascotas ENUM('nula','básica','intermedia','avanzada') NOT NULL,
    tipo_vivienda ENUM('casa','departamento','finca','otro') NOT NULL,
    otras_mascotas TEXT,
    horario_trabajo VARCHAR(100),
    motivo_adopcion TEXT,
    contacto_emergencia VARCHAR(200) NOT NULL,
    veterinario_referencia VARCHAR(200),
    acepta_condiciones BOOLEAN DEFAULT FALSE,
    acepta_visita BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (mascota_id) REFERENCES mascotas(id)
);

-- ===========================================
-- TABLA SEDES
-- ===========================================
CREATE TABLE sedes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    telefono VARCHAR(20),
    ciudad VARCHAR(50)

);

-- ===========================================
-- TABLA VETERINARIOS
-- ===========================================
CREATE TABLE veterinarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_completo VARCHAR(150) NOT NULL,
    especialidad VARCHAR(100),
    telefono VARCHAR(20),
    email VARCHAR(100)

);

-- ===========================================
-- TABLA SERVICIOS
-- ===========================================
CREATE TABLE servicios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2),
    veterinario_id INT NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (veterinario_id) REFERENCES veterinarios(id)
);

-- ===========================================
-- TABLA CITAS
-- ===========================================
CREATE TABLE citas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    mascota_id INT NOT NULL,
    servicio_id INT NOT NULL,
    sede_id INT NOT NULL, -- se elige en el formulario
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    estado ENUM('pendiente','confirmada','completada','cancelada') DEFAULT 'pendiente',
    notas TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (mascota_id) REFERENCES mascotas(id),
    FOREIGN KEY (servicio_id) REFERENCES servicios(id),
    FOREIGN KEY (sede_id) REFERENCES sedes(id)
);
-- ===========================================
-- TABLA CATEGORIA
-- ===========================================
CREATE TABLE categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT
);

-- ===========================================
-- TABLA PRODUCTOS
-- ===========================================
CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2) NOT NULL,
    stock INT DEFAULT 0,
    imagen_url VARCHAR(255),
    categoria_id INT NOT NULL,
    estado ENUM('activo','inactivo') DEFAULT 'activo',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

-- ===========================================
-- TABLA CARRITO
-- ===========================================
CREATE TABLE carritos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL UNIQUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);


-- ===========================================
-- CARRITO ITEMS
-- ===========================================
CREATE TABLE carrito_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    carrito_id INT NOT NULL,
    producto_id INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (carrito_id) REFERENCES carritos(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);

-- ===========================================
-- TABLA PEDIDOS
-- ===========================================
CREATE TABLE pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE, -- ej: PED-001
    usuario_id INT NOT NULL,
    fecha_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('pendiente','procesando','enviado','entregado','cancelado') DEFAULT 'pendiente',
    subtotal DECIMAL(10,2) NOT NULL,
    envio DECIMAL(10,2) DEFAULT 0,
    total DECIMAL(10,2) NOT NULL,

    -- Información de envío
    direccion VARCHAR(200) NOT NULL,
    ciudad VARCHAR(50),
    codigo_postal VARCHAR(10),
    telefono_contacto VARCHAR(20),

    -- Método de pago
    metodo_pago ENUM('tarjeta','yape'),

    fecha_entrega DATE NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- ===========================================
-- TABLA DETALLE PEDIDOS
-- ===========================================

CREATE TABLE pedido_detalles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id INT NOT NULL,
    producto_id INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);

-- ===========================================
-- INDEX
-- ===========================================

CREATE INDEX idx_citas_usuario ON citas(usuario_id);
CREATE INDEX idx_pedidos_usuario ON pedidos(usuario_id);
CREATE INDEX idx_mascotas_estado ON mascotas(estado_adopcion);
CREATE INDEX idx_productos_categoria ON productos(categoria_id);

-- ==============================================
-- Insertar datos
-- =============================================
-- Insertar usuarios
INSERT INTO usuarios (nombre_completo, email, telefono, password_hash, rol)
VALUES (
  'Administrador',
  'admin@admin.com',
  '999999999',
  '$2a$10$l7Byba0TC9AWSRoltthmxeIz5ZvMQJQff3aTyTOHEvDcx.5P.l0yK',
  'admin'
);

-- Insertar veterinarios
INSERT INTO veterinarios (nombre_completo, especialidad, telefono, email) VALUES
('Dr. Juan Pérez', 'Medicina General', '123456789', 'juan.perez@veterinaria.com'),
('Dra. María García', 'Cirugía', '987654321', 'maria.garcia@veterinaria.com'),
('Dr. Carlos López', 'Cardiología', '456789123', 'carlos.lopez@veterinaria.com');

-- Insertar sedes
INSERT INTO sedes (nombre, direccion, telefono, ciudad) VALUES
('Sede Mariscal', 'Av. Mariscal Castilla 123, SJL', '01-234-5678', 'Lima'),
('Sede Paradita', 'Jr. La Paradita 456, SJL', '01-345-6789', 'Lima'),
('Sede Jicamarca', 'Av. Jicamarca 789, SJL', '01-456-7890', 'Lima');

-- Insertar servicios
INSERT INTO servicios (nombre, descripcion, precio, veterinario_id, activo) VALUES
('Consulta Médica General', 'Consulta veterinaria general para diagnóstico y chequeo', 80.00, 1, true),
('Vacunación', 'Aplicación de vacunas según calendario veterinario', 50.00, 1, true),
('Desparasitación', 'Tratamiento antiparasitario interno y externo', 40.00, 1, true),
('Cirugía Menor', 'Procedimientos quirúrgicos menores ambulatorios', 300.00, 2, true),
('Cirugía Mayor', 'Procedimientos quirúrgicos complejos con hospitalización', 800.00, 2, true),
('Esterilización', 'Cirugía de esterilización para machos y hembras', 250.00, 2, true),
('Consulta Cardiológica', 'Evaluación especializada del sistema cardiovascular', 150.00, 3, true),
('Electrocardiograma', 'Estudio del ritmo cardíaco mediante electrocardiograma', 120.00, 3, true),
('Ecografía', 'Diagnóstico por imágenes ecográficas', 100.00, 1, true),
('Análisis de Sangre', 'Exámenes de laboratorio básicos', 60.00, 1, true),
('Radiografía', 'Estudios radiológicos para diagnóstico', 80.00, 1, true),
('Limpieza Dental', 'Profilaxis dental veterinaria', 180.00, 1, true),
('Emergencias 24h', 'Atención de emergencias las 24 horas', 200.00, 1, true),
('Internamiento', 'Hospitalización y cuidados intensivos por día', 150.00, 1, true),
('Baño y Corte', 'Servicio de peluquería y aseo para mascotas', 35.00, 1, true);




