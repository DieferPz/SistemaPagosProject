-- Datos de prueba OPCIONALES (ventas de ejemplo). El usuario admin se crea solo al primer arranque.
USE sistema_pagos;

INSERT INTO ventas (codigo, descripcion, importe, fecha) VALUES
 ('V-001', 'Laptop de oficina',          3500.00, CURDATE()),
 ('V-002', 'Mouse y teclado',             250.50, CURDATE()),
 ('V-003', 'Monitor 27 pulgadas',        4800.00, CURDATE()),
 ('V-004', 'Servidor de pruebas',        7200.00, CURDATE()),
 ('V-005', 'Licencia de software anual', 1200.00, CURDATE());
