-- Carolina Álvarez Rodea
-- Proyecto Final: SPV - Tienda Minorista Abarrotera
-- Inserción de Datos Iniciales con Códigos de Barras

USE tiendabd;

--
-- INSERTAR DATOS EN cat_categoria
--

INSERT INTO cat_categoria (nombre_categoria, cat_status) VALUES
('SIN CATEGORIA', 'ACTIVO'),
('BEBIDAS', 'ACTIVO'),
('ALIMENTOS', 'ACTIVO'),
('LÁCTEOS', 'ACTIVO'),
('LIEMPIEZA HOGAR', 'ACTIVO'),
('CUIDADO PERSONAL', 'ACTIVO'),
('SNACKS', 'ACTIVO'),
('HOGAR', 'ACTIVO');


--
-- INSERTAR DATOS EN usuario
--
-- El password es = 'password'
INSERT INTO usuario (username, usu_password, usu_status, usu_rol, nombre, ap_paterno, ap_materno, horario_entrada, horario_salida, genero, fecha_alta, fecha_baja, id_created_by) VALUES
('juan.perez', '$2y$11$gqbijIsecqn0nU2YT.Pdiucf.Pe7eYDMcs19.Y6eg5qpPLq2ZCpSy', 0, 'GERENTE', 'Juan', 'Pérez', 'González', '08:00:00', '17:00:00', 'M', '2023-01-15', NULL, NULL),
('maria.garcia', '$2y$11$gqbijIsecqn0nU2YT.Pdiucf.Pe7eYDMcs19.Y6eg5qpPLq2ZCpSy', 0, 'GERENTE', 'María', 'García', 'López', '15:00:00', '22:30:00', 'F', '2023-02-20', NULL, 1),
('carlos.lopez', '$2y$11$gqbijIsecqn0nU2YT.Pdiucf.Pe7eYDMcs19.Y6eg5qpPLq2ZCpSy', 0, 'CAJERO', 'Carlos', 'Lopez', 'Mendez', '08:30:00', '15:30:00', 'M', '2023-03-10', NULL, 1),
('ana.martinez', '$2y$11$gqbijIsecqn0nU2YT.Pdiucf.Pe7eYDMcs19.Y6eg5qpPLq2ZCpSy', 0, 'CAJERO', 'Ana', 'Martinez', 'Diaz', '09:00:00', '16:00:00', 'F', '2023-04-05', NULL, 1),
('luis.sanchez', '$2y$11$gqbijIsecqn0nU2YT.Pdiucf.Pe7eYDMcs19.Y6eg5qpPLq2ZCpSy', 0, 'CAJERO', 'Luis', 'Sanchez', 'Torres', '16:00:00', '22:00:00', 'M', '2023-05-12', NULL, 2),
('laura.ramirez', '$2y$11$gqbijIsecqn0nU2YT.Pdiucf.Pe7eYDMcs19.Y6eg5qpPLq2ZCpSy', 0, 'CAJERO', 'Laura', 'Ramirez', 'Vega', '16:30:00', '22:30:00', 'F', '2023-06-18', NULL, 2);

--
-- INSERTAR DATOS EN producto
--

INSERT INTO producto (codigo, nombre, stock, prod_status, costo_compra, precio_venta, id_categoria) VALUES
('7501031311309', 'Coca Cola 1L', 15, 'ACTIVO', 15.00, 20.00, 2),
('7501010178840', 'Leche Lala 1L', 20, 'ACTIVO', 18.00, 25.00, 4),
('7501028304553', 'Arroz Grano Largo 1kg', 20, 'ACTIVO', 12.00, 18.00, 3),
('7501038276653', 'Detergente Ariel 500ml', 8, 'ACTIVO', 25.00, 35.00, 5),
('7501038276654', 'Shampoo Sedal 400ml', 6, 'ACTIVO', 30.00, 45.00, 6),
('7501038276655', 'Papas Sabritas 52g', 15, 'ACTIVO', 8.00, 12.00, 7),
('7501038276656', 'Vela Aromática 500g', 4, 'ACTIVO', 20.00, 30.00, 8);

--
-- INSERTAR DATOS EN venta
--

INSERT INTO venta (total_venta, importe_pagado, cambio_entregado, venta_status, fecha_hora_venta, tipo_pago, id_cajero) VALUES
(100.00, 100.00, 0.00, 'FINALIZADA', '2024-04-01 10:15:00', 'T', 3),
(90, 100.00, 10.00, 'FINALIZADA', '2024-04-01 11:30:00', 'E', 3),
(57.00, 70.00, 13.00, 'FINALIZADA', '2024-04-01 12:45:00', 'E', 4),
(115.00, 150.00, 35.00, 'FINALIZADA', '2024-04-01 14:00:00', 'E', 4),
(48.00, 48.00, 0.00, 'FINALIZADA', '2024-04-01 15:15:00', 'T', 5),
(30.00, 30.00, 0.00, 'FINALIZADA', '2024-04-01 16:30:00', 'E', 6),
(90.00, 100.00, 0.00, 'FINALIZADA', '2024-04-01 17:45:00', 'V', 6);

--
-- INSERTAR DATOS EN producto_vendido
--
INSERT INTO producto_vendido (cantidad, precio_unitario, costo_unitario, id_venta, codigo) VALUES
(2, 20.00, 15.00, 1, '7501031311309'), -- 2 Coca Cola 1L en venta 1
(1, 25.00, 18.00, 1, '7501010178840'), -- 1 Leche Lala 1L en venta 1
(1, 35.00, 25.00, 1, '7501038276653'), -- 1 Detergente Ariel 500ml
(5, 18.00, 12.00, 2, '7501028304553'), -- 5 Arroz Grano Largo 1kg en venta 2
(1, 20.00, 15.00, 3, '7501031311309'), -- 1 Coca Cola 1L en venta 3
(1, 25.00, 18.00, 3, '7501010178840'), -- 1 Leche Lala 1l en venta 3
(1, 12.00, 8.00, 3, '7501038276655'), -- 1 Paras Sabritas 52g en venta 3
(2, 35.00, 25.00, 4, '7501038276653'), -- 2 Detergente Ariel 500ml en venta 4
(1, 45.00, 30.00, 4, '7501038276654'), -- 1 Shampoo Sedal 400ml en venta 4
(4, 12.00, 8.00, 5, '7501038276655'), -- 4 Papas Sabritas 52g en venta 5
(1, 30.00, 20.00, 6, '7501038276656'), -- 1 Vela Aromática 500g en venta 6
(3, 30.00, 20.00, 7, '7501038276656'); -- 3 Vela Aromática 500g en venta 7
SELECT * FROM producto_vendido;

--
-- INSERTAR DATOS EN corte_caja
--

INSERT INTO corte_caja (saldo_inicial, total_ventas, importe_entregado, efectivo_remanente, fecha_hora_corte, id_cajero, id_gerente) VALUES
(150.00, 292.00, 442.00, 100.00, '2024-04-01 16:00:00', 3, 1),
(100.00, 120.00, 220.00, 150.00, '2024-04-01 22:00:00', 6, 2);
