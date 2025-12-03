-- ============================================================
-- SCRIPT DE DATOS INICIALES - HUERTOHOGAR
-- Productos orgánicos y de agricultura sustentable
-- ============================================================

-- Limpiar datos existentes (opcional - comentar si no quieres eliminar)
-- DELETE FROM carrito_items;
-- DELETE FROM pedido_items;
-- DELETE FROM transacciones;
-- DELETE FROM pedidos;
-- DELETE FROM carritos;
-- DELETE FROM productos;
-- DELETE FROM usuarios WHERE email != 'admin@huertohogar.cl';

-- ============================================================
-- USUARIOS DE PRUEBA
-- ============================================================

-- Admin (contraseña: admin123)
INSERT INTO usuarios (nombre, email, password, telefono, direccion, comuna, region, rol, activo, fecha_creacion, fecha_actualizacion)
VALUES 
('Administrador', 'admin@huertohogar.cl', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+56912345678', 'Av. Principal 123', 'Santiago', 'Región Metropolitana', 'ROLE_ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Usuario de prueba (contraseña: usuario123)
INSERT INTO usuarios (nombre, email, password, telefono, direccion, comuna, region, rol, activo, fecha_creacion, fecha_actualizacion)
VALUES 
('Usuario Test', 'usuario@test.cl', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+56987654321', 'Calle Falsa 456', 'Providencia', 'Región Metropolitana', 'ROLE_USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================================
-- PRODUCTOS - VERDURAS
-- ============================================================

INSERT INTO productos (nombre, descripcion, precio, categoria, stock, unidad, origen, imagen, destacado, activo, rating, descuento, fecha_creacion, fecha_actualizacion)
VALUES
('Tomates Cherry Orgánicos', 'Tomates cherry cultivados sin pesticidas, dulces y jugosos. Perfectos para ensaladas y snacks saludables.', 2990, 'VERDURAS', 50, 'Bandeja 250g', 'Melipilla, Región Metropolitana', 'https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=500', true, true, 4.8, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Lechugas Hidropónicas', 'Lechugas frescas cultivadas en sistema hidropónico. Crocantes y limpias, listas para consumir.', 1990, 'VERDURAS', 80, 'Unidad', 'Buin, Región Metropolitana', 'https://images.unsplash.com/photo-1622206151226-18ca2c9ab4a1?w=500', true, true, 4.7, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Zanahorias Orgánicas', 'Zanahorias cultivadas de forma orgánica, ricas en betacaroteno. Perfectas para jugos y ensaladas.', 1490, 'VERDURAS', 100, 'Kg', 'Paine, Región Metropolitana', 'https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?w=500', false, true, 4.6, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Brócoli Fresco', 'Brócoli verde oscuro, rico en vitaminas y minerales. Ideal para saltear o al vapor.', 2490, 'VERDURAS', 45, 'Unidad', 'Colina, Región Metropolitana', 'https://images.unsplash.com/photo-1459411621453-7b03977f4bfc?w=500', false, true, 4.5, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Espinacas Frescas', 'Espinacas tiernas y frescas, perfectas para ensaladas o smoothies verdes. Alto contenido de hierro.', 1890, 'VERDURAS', 60, 'Atado 250g', 'San Bernardo, Región Metropolitana', 'https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=500', true, true, 4.9, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Pimientos Tricolor', 'Mix de pimientos rojo, amarillo y verde. Dulces y crujientes, ideales para cualquier preparación.', 3490, 'VERDURAS', 35, 'Kg', 'Quilicura, Región Metropolitana', 'https://images.unsplash.com/photo-1563565375-f3fdfdbefa83?w=500', false, true, 4.7, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Cebollas Moradas', 'Cebollas moradas dulces, perfectas para ensaladas y preparaciones crudas.', 990, 'VERDURAS', 120, 'Kg', 'Talagante, Región Metropolitana', 'https://images.unsplash.com/photo-1618512496248-a07fe83aa8cb?w=500', false, true, 4.4, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Apio Orgánico', 'Apio fresco y crujiente, perfecto para jugos detox y sopas saludables.', 1790, 'VERDURAS', 40, 'Atado', 'Lampa, Región Metropolitana', 'https://images.unsplash.com/photo-1628773822990-202d3e6f1dfb?w=500', false, true, 4.3, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Calabacín Verde', 'Calabacines tiernos y frescos. Bajos en calorías, perfectos para dietas saludables.', 1690, 'VERDURAS', 55, 'Kg', 'Peñaflor, Región Metropolitana', 'https://images.unsplash.com/photo-1584868186470-18f3e1b6bb63?w=500', false, true, 4.6, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Rúcula Premium', 'Rúcula con sabor ligeramente picante. Ideal para ensaladas gourmet y pizzas.', 2290, 'VERDURAS', 30, 'Bolsa 150g', 'Pirque, Región Metropolitana', 'https://images.unsplash.com/photo-1622973536968-3ead9e780960?w=500', true, true, 4.8, 15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================================
-- PRODUCTOS - FRUTAS
-- ============================================================

INSERT INTO productos (nombre, descripcion, precio, categoria, stock, unidad, origen, imagen, destacado, activo, rating, descuento, fecha_creacion, fecha_actualizacion)
VALUES
('Frutillas Orgánicas', 'Frutillas rojas y jugosas cultivadas sin químicos. Dulces y aromáticas, perfectas para postres.', 3990, 'FRUTAS', 40, 'Bandeja 500g', 'San Fernando, Región O''Higgins', 'https://images.unsplash.com/photo-1464965911861-746a04b4bca6?w=500', true, true, 4.9, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Arándanos Premium', 'Arándanos azules, ricos en antioxidantes. Ideales para snacks saludables y smoothies.', 4990, 'FRUTAS', 25, 'Caja 250g', 'Los Ángeles, Región del Biobío', 'https://images.unsplash.com/photo-1498557850523-fd3d118b962e?w=500', true, true, 5.0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Manzanas Verdes', 'Manzanas granny smith crujientes y ácidas. Perfectas para comer solas o en preparaciones.', 1990, 'FRUTAS', 150, 'Kg', 'Curicó, Región del Maule', 'https://images.unsplash.com/photo-1568702846914-96b305d2aaeb?w=500', false, true, 4.6, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Peras Packham', 'Peras jugosas y dulces. Excelente fuente de fibra y vitaminas.', 2490, 'FRUTAS', 90, 'Kg', 'Rancagua, Región O''Higgins', 'https://images.unsplash.com/photo-1568572933382-74d440642117?w=500', false, true, 4.5, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Plátanos Orgánicos', 'Plátanos maduros y dulces. Fuente natural de energía, perfectos para deportistas.', 1290, 'FRUTAS', 200, 'Kg', 'Ecuador (Comercio Justo)', 'https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?w=500', true, true, 4.7, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Paltas Hass', 'Paltas hass cremosas y nutritivas. Ricas en grasas saludables y vitaminas.', 2990, 'FRUTAS', 80, 'Kg', 'La Cruz, Región de Valparaíso', 'https://images.unsplash.com/photo-1523049673857-eb18f1d7b578?w=500', true, true, 4.8, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Naranjas para Jugo', 'Naranjas jugosas ideales para jugos naturales. Alto contenido de vitamina C.', 1790, 'FRUTAS', 120, 'Kg', 'Ovalle, Región de Coquimbo', 'https://images.unsplash.com/photo-1580052614034-c55d20bfee3b?w=500', false, true, 4.6, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Limones de Pica', 'Limones aromáticos de Pica, jugosos y ácidos. Perfectos para aliños y bebidas.', 1990, 'FRUTAS', 100, 'Kg', 'Pica, Región de Tarapacá', 'https://images.unsplash.com/photo-1590502593747-42a996133562?w=500', false, true, 4.7, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Kiwis Orgánicos', 'Kiwis verdes ricos en vitamina C y fibra. Sabor dulce-ácido refrescante.', 2790, 'FRUTAS', 60, 'Kg', 'Quillota, Región de Valparaíso', 'https://images.unsplash.com/photo-1585059895524-72359e9e489b?w=500', false, true, 4.5, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Uvas Red Globe', 'Uvas rojas sin semillas, dulces y crujientes. Perfectas para snacks y postres.', 3490, 'FRUTAS', 45, 'Kg', 'Copiapó, Región de Atacama', 'https://images.unsplash.com/photo-1601275868399-45bec4f4cd9d?w=500', true, true, 4.9, 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================================
-- PRODUCTOS - HIERBAS Y ESPECIAS
-- ============================================================

INSERT INTO productos (nombre, descripcion, precio, categoria, stock, unidad, origen, imagen, destacado, activo, rating, descuento, fecha_creacion, fecha_actualizacion)
VALUES
('Albahaca Fresca', 'Albahaca aromática recién cosechada. Ideal para salsas, ensaladas y preparaciones italianas.', 990, 'HIERBAS', 50, 'Atado', 'Huerto Local, Santiago', 'https://images.unsplash.com/photo-1618375569909-3c8616cf7733?w=500', true, true, 4.8, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Cilantro Orgánico', 'Cilantro fresco y aromático. Esencial en la cocina chilena y latinoamericana.', 890, 'HIERBAS', 70, 'Atado', 'Cultivo Local, Maipú', 'https://images.unsplash.com/photo-1631544111278-82ab6689b47a?w=500', false, true, 4.7, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Perejil Liso', 'Perejil fresco de hojas lisas. Rico en vitaminas y minerales.', 790, 'HIERBAS', 80, 'Atado', 'Cultivo Local, Santiago', 'https://images.unsplash.com/photo-1607664208395-d3c8c7ced7e5?w=500', false, true, 4.6, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Menta Fresca', 'Menta aromática perfecta para infusiones, mojitos y postres.', 890, 'HIERBAS', 45, 'Atado', 'Huerto Orgánico, Puente Alto', 'https://images.unsplash.com/photo-1628556270448-4d4e4148e1b1?w=500', true, true, 4.9, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Romero Fresco', 'Romero aromático ideal para carnes, papas y panes. Propiedades medicinales.', 1190, 'HIERBAS', 35, 'Atado', 'Cultivo Orgánico, Colina', 'https://images.unsplash.com/photo-1612437728925-7a4b0ad5d605?w=500', false, true, 4.5, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Orégano Fresco', 'Orégano aromático perfecto para pizzas, pastas y carnes.', 990, 'HIERBAS', 40, 'Atado', 'Huerto Local, La Reina', 'https://images.unsplash.com/photo-1627514204194-cf68fce97beb?w=500', false, true, 4.6, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================================
-- PRODUCTOS - HUEVOS Y LÁCTEOS
-- ============================================================

INSERT INTO productos (nombre, descripcion, precio, categoria, stock, unidad, origen, imagen, destacado, activo, rating, descuento, fecha_creacion, fecha_actualizacion)
VALUES
('Huevos de Campo', 'Huevos de gallinas felices criadas en libertad. Yema naranja intensa y sabor superior.', 3990, 'HUEVOS_LACTEOS', 100, 'Maple 12 unidades', 'Granja Orgánica, Batuco', 'https://images.unsplash.com/photo-1582722872445-44dc5f7e3c8f?w=500', true, true, 4.9, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Queso de Cabra Artesanal', 'Queso de cabra cremoso hecho artesanalmente. Sabor suave y textura única.', 5990, 'HUEVOS_LACTEOS', 25, 'Pieza 200g', 'Quesería Artesanal, Pirque', 'https://images.unsplash.com/photo-1452195100486-9cc805987862?w=500', true, true, 5.0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Yogurt Natural Orgánico', 'Yogurt natural sin azúcar añadida. Probióticos naturales para tu salud digestiva.', 2490, 'HUEVOS_LACTEOS', 60, 'Pote 500g', 'Lechería Orgánica, Frutillar', 'https://images.unsplash.com/photo-1488477181946-6428a0291777?w=500', true, true, 4.8, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Mantequilla Artesanal', 'Mantequilla cremosa hecha con leche de vacas de pastoreo. Sin conservantes.', 3490, 'HUEVOS_LACTEOS', 40, 'Pote 250g', 'Granja Láctea, Osorno', 'https://images.unsplash.com/photo-1589985270826-4b7bb135bc9d?w=500', false, true, 4.7, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================================
-- PRODUCTOS - LEGUMBRES Y GRANOS
-- ============================================================

INSERT INTO productos (nombre, descripcion, precio, categoria, stock, unidad, origen, imagen, destacado, activo, rating, descuento, fecha_creacion, fecha_actualizacion)
VALUES
('Lentejas Orgánicas', 'Lentejas verdes orgánicas. Alto contenido proteico y fibra. Perfectas para guisos.', 2990, 'LEGUMBRES', 80, 'Bolsa 1kg', 'Cultivo Orgánico, Ñuble', 'https://images.unsplash.com/photo-1607672632458-9b96ab46ae43?w=500', true, true, 4.8, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Porotos Negros', 'Porotos negros orgánicos. Ricos en proteínas vegetales y antioxidantes.', 2490, 'LEGUMBRES', 70, 'Bolsa 1kg', 'Agricultura Sustentable, Los Lagos', 'https://images.unsplash.com/photo-1615485500828-174c36210745?w=500', false, true, 4.7, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Quinoa Real', 'Quinoa real de calidad premium. Superalimento completo en proteínas.', 4990, 'LEGUMBRES', 50, 'Bolsa 500g', 'Comercio Justo, Bolivia', 'https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500', true, true, 5.0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Garbanzos Orgánicos', 'Garbanzos grandes y cremosos. Perfectos para hummus y guisos mediterráneos.', 2790, 'LEGUMBRES', 65, 'Bolsa 1kg', 'Cultivo Orgánico, Aconcagua', 'https://images.unsplash.com/photo-1610646402935-c40de9ce7d3f?w=500', false, true, 4.6, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Arroz Integral Orgánico', 'Arroz integral de grano largo. Rico en fibra y nutrientes esenciales.', 3490, 'LEGUMBRES', 90, 'Bolsa 1kg', 'Agricultura Orgánica, Parral', 'https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500', true, true, 4.7, 15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================================
-- PRODUCTOS - MIEL Y ENDULZANTES
-- ============================================================

INSERT INTO productos (nombre, descripcion, precio, categoria, stock, unidad, origen, imagen, destacado, activo, rating, descuento, fecha_creacion, fecha_actualizacion)
VALUES
('Miel de Ulmo Pura', 'Miel de ulmo 100% pura de la zona sur. Sabor único y propiedades medicinales.', 6990, 'MIEL', 40, 'Frasco 500g', 'Apicultura Sustentable, Valdivia', 'https://images.unsplash.com/photo-1587049352846-4a222e784422?w=500', true, true, 5.0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Miel de Flores Silvestres', 'Miel multifloral de montaña. Sabor delicado y aromático.', 5490, 'MIEL', 50, 'Frasco 500g', 'Apiario Orgánico, Curacautín', 'https://images.unsplash.com/photo-1558642084-fd07fae5282e?w=500', true, true, 4.9, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Miel de Manuka Chilena', 'Miel con propiedades antibacterianas. Ideal para fortalecer el sistema inmune.', 8990, 'MIEL', 20, 'Frasco 250g', 'Producción Premium, Chiloé', 'https://images.unsplash.com/photo-1471943311424-646960669fbc?w=500', true, true, 5.0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
