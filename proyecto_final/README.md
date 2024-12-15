# Sistema de Punto de Venta (SPV)

## Índice
1. [Descripción General](#descripción-general)
2. [Configuración del Entorno de Desarrollo](#configuración-del-entorno-de-desarrollo)
3. [Requerimientos e Instalación](#requerimientos-e-instalación)
4. [Características Principales](#características-principales)
    - [Gestión de Usuarios](#gestión-de-usuarios)
    - [Gestión de Productos y Categorías](#gestión-de-productos-y-categorías)
    - [Gestión de Ventas](#gestión-de-ventas)
    - [Corte de Caja](#corte-de-caja)
    - [Reportes](#reportes)
    - [Seguridad](#seguridad)
5. [Flujo de Utilización](#flujo-de-utilización)
    - [Inicio de Sesión](#1-inicio-de-sesión)
    - [Gestión de Productos y Categorías](#2-gestión-de-productos-y-categorías)
    - [Gestión de Ventas](#3-gestión-de-ventas)
    - [Realizar Corte de Caja (Cajero)](#4-realizar-corte-de-caja-cajero)
    - [Consultar Corte de Caja (Gerente)](#5-consultar-corte-de-caja-gerente)
    - [Consultar Reportes (Gerente)](#6-consultar-reportes-gerente)
    - [Gestión y Alta de Usuarios (Gerente)](#7-gestion-y-alta-de-usuarios-gerente)
    - [Cierre de Sesión](#8-cierre-de-sesión)

## Descripción General
El **Sistema de Punto de Venta (SPV)** está diseñado para digitalizar las operaciones comerciales de una tienda de abarrotes minorista, mejorando la eficiencia en la gestión de inventarios y transacciones. Está orientado para su uso en una única sucursal, con roles específicos para **Cajeros** y **Gerentes**.

## Configuración del Entorno de Desarrollo
### Versiones Recomendadas
| Recomendado               | Referencia                                                                                  | Notas                                                                                                 |
|---------------------------|---------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------|
| Oracle Java 17 JDK      | [Download]([https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html](https://www.oracle.com/java/technologies/downloads/#java17) | Java 17 or higher is required for Spring Boot 3                                                        |
| IntelliJ 2022 or Higher | [Download](https://www.jetbrains.com/idea/download/)                                                                                                          | Ultimate Edition recommended. Anyway, this runs in Community Edition                                   |
| Maven 3.9.0 or higher   | [Download](https://maven.apache.org/download.cgi)                                                                                                             | [Installation Instructions](https://maven.apache.org/install.html)                                     |
| Git 2.44 or higher      | [Download](https://git-scm.com/downloads)                                                                                                                     |                                                                                                        | 
| Git GUI Clients         | [Downloads](https://git-scm.com/downloads/guis)                                                                                                               | Not required. But can be helpful if new to Git. SourceTree is a good option for Mac and Windows users. |
| MariaDB 10.11.10          | [Descargar](https://mariadb.org/download/)                                                  | Distribución binaria compatible con las características del sistema.                                 |

## Requerimientos e Instalación

1. Clonar el repositorio.
   ```bash
   git clone https://github.com/usuario/sistema-punto-venta.git
   ```
2. Configurar la base de datos MariaDB:
    - Importar el archivo `schema_tienda_pf.sql` para crear el esquema.
    - Cargar datos iniciales con `data_tienda_pf.sql`.
3. Configurar el archivo `.yml`:
   ```
    url: jdbc:mariadb://localhost:3306/tiendabd
    username: tu usuario
    password: tu contraseña
    driver-class-name: org.mariadb.jdbc.Driver
   ```
4. Acceder al sistema mediante `http://localhost:8090/`.



## Características Principales

### Gestión de Usuarios
- Inicio de sesión seguro con autenticación mediante usuario y contraseña cifrada.
- Roles:
    - **Gerente:** Puede registrar, editar y eliminar usuarios.
    - **Cajero:** Gestiona ventas y productos.

### Gestión de Productos y Categorías
- Administración de categorías (crear, editar y eliminar).
- Administración de productos, incluyendo actualización de inventario después de cada venta.
- Reasignación de productos a la categoría predeterminada cuando una categoría es eliminada.

### Gestión de Ventas
- Registro de ventas mediante código de barras.
- Almacenamiento de información de ventas: productos vendidos, total, tipo de pago, cambio.
- Cancelación de productos en una venta, validada por un gerente.

### Corte de Caja
- Registro de cortes de caja asociados a un cajero y validados por un gerente.
- Consulta de cortes de caja filtrados por fecha y cajero (disponible para gerentes).

### Reportes
- Reportes de ganancias por día, semana y mes.
- Reportes de ventas por categorías y rango de fechas.
- Productos con existencias bajas o agotados.

### Seguridad
- Contraseñas cifradas con BCrypt.
- Uso de JSON Web Tokens (JWT) para autenticación.
- Validación de permisos en cada solicitud.

## Flujo de Utilización

### 1. Inicio de Sesión
- **URL:** `/login`
- Ingrese sus credenciales.
- Los usuarios serán redirigidos según su rol:
    - **Gerente:** Acceso completo.
    - **Cajero:** Acceso a ventas y productos.
- Ejemplos de usuarios que estan en la base de datos:
    - ROL GERENTE
    - username: juan.perez
    - password: password
    - ROL CAJERO
    - username: carlos.lopez
    - password: password
- **Nota:** Todos los usuarios de la base de datos tienen como contraseña inicial `password`.

### 2. Gestión de Productos y Categorías
#### Categorías
1. Navegar al módulo de categorías mediante el navbar
- a. Crear categoria nueva
- b. Gestionar categorias (editar/ eliminar/consultar)

#### Productos
1. Navegar al módulo de productos mediante el navbar.
- a. Agregar un producto.
- b. Gestionar productos (editar/ eliminar/consultar).

### 3. Gestión de Ventas
1. Navegar al módulo de ventas.
2. Escanear o ingresar manualmente los códigos de los productos a vender y modificar cantidad.
3. Confirmar el total y el método de pago.
4. Guardar la venta.
   En este modulo también se permite la cancelación de una venta, la cancelación de un producto y checar el precio de un producto.
- Cancelar Producto:
1. Ingresar el codigo del producto manualmente y buscarlo.
2. Ingresar las credenciales del gerente y confirmar cancelación.
- Cancelar Venta:
1. Verificar la información de la venta.
2. Ingresar el las credenciales del gerente y confirmar cancelación.
- Checar precio:
1. Ingresar el codigo del producto manualmente y buscarlo.

### 4. Realizar Corte de Caja (Cajero)
1. Desde la sesión del cajero, ingresar los detalles del corte:
    - Saldo inicial (cuando es el primer corte, si no, no se permite escribir)
    - Importe entregado (el dinero que el cajero obtuvo de las ventas)
    - Saldo remanente (el dinero que se deja en la caja para el siguiente turno)
2. Validar el corte con las credenciales del gerente.
3. El sistema almacena el corte de caja asociado al cajero y al gerente que aprobo.

### 5. Consultar Corte de Caja (Gerente)
1. Desde la sesión de gerente ingresar a la consulta de cortes de caja:
- Consultar los cortes de caja por cajero y por periodo

### 6. Consultar Reportes (Gerente)
1. Navegar al módulo de reportes.
2. Seleccionar el tipo de reporte:
    - Ventas: Consultar ganancias y total de ventas por tipo de pago y periodo.
    - Productos Inventario: Consultar productos con existencias bajas o agotadas por categoria.

### 7. Gestion y Alta de Usuarios (Gerente)
- Alta
1. Desde la sesión de gerente, ingresar a agregar usuario.
2. Llenar los datos del usuario. La contraseña se cifrará con ByCript
3. El nuevo usario se asociara con el gerente que lo creó.
- Gestión
1. Consultar los usuarios por rol
2. Gestionar los usuarios (editar y dar de baja)

### 8. Cierre de Sesión
- Haga clic en el botón de cierre de sesión para finalizar su actividad.

