INSERT IGNORE INTO usuario
(id, nombre, apellido, correo, telefono, direccion, rol, activo, fecha_registro)
VALUES
    (9, 'Ignacio', 'Vargas', 'ignacio@tiendajuegos.cl', '+56999990001', 'Santiago', 'CLIENTE', TRUE, NOW()),
    (10, 'Fernanda', 'Castro', 'fernanda@tiendajuegos.cl', '+56999990002', 'Penalolen', 'CLIENTE', TRUE, NOW()),
    (11, 'Tomas', 'Navarro', 'tomas@tiendajuegos.cl', '+56999990003', 'Quilicura', 'CLIENTE', TRUE, NOW()),
    (12, 'Antonia', 'Reyes', 'antonia@tiendajuegos.cl', '+56999990004', 'La Reina', 'CLIENTE', TRUE, NOW());
