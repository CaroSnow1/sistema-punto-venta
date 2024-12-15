-- Carolina Álvarez Rodea
-- Proyecto Final: SPV - Tienda Minorista Abarrotera

DROP DATABASE IF EXISTS tiendabd;
CREATE DATABASE tiendabd;

USE tiendabd;

--
-- TABLA CAT_CATEGORIA
--
DROP TABLE IF EXISTS cat_categoria;
CREATE TABLE cat_categoria(
	id_categoria INT UNSIGNED AUTO_INCREMENT NOT NULL,
	nombre_categoria VARCHAR(30) NOT NULL,
   cat_status VARCHAR(25) NOT NULL,
	PRIMARY KEY (id_categoria),
	UNIQUE KEY uq_nombre_categoria (nombre_categoria)
);

--
-- tabla producto
--

DROP TABLE IF EXISTS producto;
CREATE TABLE producto(
	codigo VARCHAR(15) NOT NULL,
	nombre VARCHAR(35) NOT NULL,
	stock INT UNSIGNED NOT NULL,
	prod_status VARCHAR(10) NOT NULL, 
	costo_compra DECIMAL(10,2) NOT NULL CHECK(costo_compra > 0),
	precio_venta DECIMAL(10,2) NOT NULL CHECK(precio_venta > 0),
	id_categoria INT UNSIGNED NOT NULL,
	PRIMARY KEY (codigo),
	INDEX idx_nombre (nombre), 
	INDEX idx_categoria (id_categoria),
	INDEX idx_prod_status (prod_status),
	CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria)
   REFERENCES cat_categoria(id_categoria) ON UPDATE CASCADE
);

--
-- TABLA USUARIO
--

DROP TABLE IF EXISTS usuario;
CREATE TABLE IF NOT EXISTS usuario(
	id_usuario INT UNSIGNED NOT NULL AUTO_INCREMENT,
	username VARCHAR(20) NOT NULL,
	usu_password VARCHAR(255) NOT NULL,
   usu_status TINYINT(1) NOT NULL DEFAULT 1,
   usu_rol VARCHAR(10) NOT NULL,
   nombre VARCHAR(35) NOT NULL,
	ap_paterno VARCHAR(35) NOT NULL,
	ap_materno VARCHAR(35) NOT NULL,
	horario_entrada TIME NOT NULL,
	horario_salida TIME NOT NULL,
	genero CHAR(1) NOT NULL DEFAULT 'N',
	fecha_alta DATE NOT NULL,
	fecha_baja DATE NULL,
	id_created_by INT UNSIGNED NULL,
	PRIMARY KEY (id_usuario),
	UNIQUE KEY uq_username (username),
	INDEX idx_usuario_usu_status (usu_status),
	INDEX idx_usuario_usu_rol (usu_rol),
	CONSTRAINT chk_usu_status CHECK (usu_status IN (0, 1, 2)),
	CONSTRAINT fk_usuario_usuario FOREIGN KEY (id_created_by)
	REFERENCES usuario(id_usuario) ON UPDATE CASCADE 
);

--
-- TABLA CORTE_CAJA
--

DROP TABLE IF EXISTS corte_caja;
CREATE TABLE corte_caja(
	id_corte_caja INT UNSIGNED NOT NULL AUTO_INCREMENT,
	saldo_inicial DECIMAL(10,2) NOT NULL,
	total_ventas DECIMAL(10,2) NOT NULL,
	importe_entregado DECIMAL(10,2) NOT NULL,
   efectivo_remanente DECIMAL(10,2) NOT NULL, 
	fecha_hora_corte TIMESTAMP NOT NULL,
	id_cajero INT UNSIGNED NOT NULL,
	id_gerente INT UNSIGNED NOT NULL,
	PRIMARY KEY (id_corte_caja),
   UNIQUE KEY uq_corte_caja (fecha_hora_corte, id_cajero),
	INDEX idx_corte_caja_id_cajero (id_cajero),
	INDEX idx_corte_caja_id_gerente (id_gerente),
	INDEX idx_corte_caja_fecha_hora (fecha_hora_corte),
	CONSTRAINT fk_corte_cj_usuario_caj FOREIGN KEY (id_cajero)
	REFERENCES usuario(id_usuario) ON UPDATE CASCADE,
	CONSTRAINT fk_corte_cj_usuario_ger FOREIGN KEY (id_gerente)
	REFERENCES usuario(id_usuario) ON UPDATE CASCADE
);

--
-- TABLA VENTA
--

DROP TABLE IF EXISTS venta;
CREATE TABLE venta(
	id_venta INT UNSIGNED NOT NULL AUTO_INCREMENT,
   total_venta DECIMAL(10,2) NOT NULL,
	importe_pagado DECIMAL(10,2) NOT NULL,
	cambio_entregado DECIMAL(10,2) NOT NULL,
	venta_status VARCHAR(10) NOT NULL,
	fecha_hora_venta TIMESTAMP NOT NULL,
	tipo_pago CHAR(1) NOT NULL DEFAULT 'E',
	id_cajero INT UNSIGNED NOT NULL,
	PRIMARY KEY (id_venta),
   UNIQUE KEY uq_venta (fecha_hora_venta, id_cajero),
	INDEX idx_venta_id_cajero (id_cajero),
	INDEX idx_venta_fecha_hora (fecha_hora_venta),
	INDEX idx_venta_venta_status (venta_status),
	CONSTRAINT chk_tipo_pago CHECK(tipo_pago IN ('E', 'T', 'V')),
	CONSTRAINT fk_venta_usuario_caj FOREIGN KEY (id_cajero)
	REFERENCES usuario(id_usuario) ON UPDATE CASCADE
);

--
-- TABLA PRODUCTO_VENDIDO
--
venta
DROP TABLE IF EXISTS producto_vendido;
CREATE TABLE producto_vendido(
	id_producto_vendido INT UNSIGNED NOT NULL AUTO_INCREMENT,
	cantidad INT UNSIGNED NOT NULL,
	precio_unitario DECIMAL(10,2) NOT NULL, 
	costo_unitario DECIMAL (10,2) NOT NULL,
	total_producto DECIMAL(10,2) GENERATED ALWAYS AS (cantidad * precio_unitario) STORED,
	total_ganancia DECIMAL(10,2) GENERATED ALWAYS AS (cantidad * (precio_unitario - costo_unitario)) STORED,
   pv_status VARCHAR(20) DEFAULT 'VENDIDO', 
	id_venta INT UNSIGNED NOT NULL,
   codigo VARCHAR(15) NOT NULL,
	PRIMARY KEY (id_producto_vendido),
	INDEX idx_producto_vendido_codigo (codigo),
	INDEX idx_producto_vendido_id_venta (id_venta),
	INDEX idx_producto_vendido_pv_status (pv_status),
	CONSTRAINT chk_pv_status CHECK (pv_status IN ('VENDIDO', 'CANCELADO')),
	CONSTRAINT fk_producto_vend_producto FOREIGN KEY (codigo)
	REFERENCES producto(codigo) ON UPDATE CASCADE,
	CONSTRAINT fk_producto_vend_venta FOREIGN KEY (id_venta)
	REFERENCES venta(id_venta) ON UPDATE CASCADE 
);

