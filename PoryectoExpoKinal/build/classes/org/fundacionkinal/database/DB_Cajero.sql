drop database if exists cajeroDB;
create database cajeroDB;
use cajeroDB;

create table Usuarios (
	idUsuario int auto_increment,
	nombreUsuario varchar(64) not null,
	correoUsuario varchar(128) not null,
	contraseñaUsuario varchar(128) not null,
    tipo enum('Empleado', 'Admin'),
    constraint pk_usuarios primary key (idUsuario)
);

create table Productos(
	idProducto int auto_increment,
	nombreProducto varchar(64) not null,
	precioProducto decimal(10,2) not null,
	stockProducto int not null,
    codigoBarras varchar(256),
    constraint pk_productos primary key (idProducto) 
);

create table Compras(
	idCompra int auto_increment,
    estadoCompra enum('Pendiente','Completada','Cancelada'),
    estadoPago enum('Pendiente', 'Pagado')default'Pendiente',
    fechaCompra datetime default now(),
    constraint pk_compras primary key (idCompra)
);

create table DetalleCompras(
	idCompra int not null,
    idProducto int not null,
    cantidad int not null,
    subtotal decimal(10,2) not null,
    constraint pk_detallecompras primary key (idCompra, idProducto),
    constraint fk_detalle_compras_compras foreign key (idCompra)
		references Compras(idCompra) on delete cascade,
    constraint fk_detalle_compras_productos foreign key (idProducto)
		references Productos(idProducto) on delete cascade
);

create table Pagos(
	idPago int auto_increment,
    fecha datetime default now(),
	metodoPago enum('Efectivo','Tarjeta','Transferencia'),
	cantidad  decimal(10,2) not null,
    idCompra int not null,
    constraint pk_pagos primary key (idPago),
    constraint fk_pagos_compras foreign key (idCompra) 
		references Compras (idCompra)
);

create table Facturas(
	idFactura int auto_increment,
    fecha datetime default now(),
    total decimal(10,2) not null,
    metodoPago enum('Efectivo','Tarjeta','Transferencia'),
    idEmpleado int not null,
    idCompra int not null,
    idPago int not null,
    constraint pk_facturas primary key (idFactura),

	constraint fk_facturas_empleados foreign key (idEmpleado)
		references Usuarios(idUsuario) on delete cascade,
	constraint fk_facturas_compras foreign key (idCompra)
		references Compras(idCompra) on delete cascade,
	constraint fk_facturas_pagos foreign key (idPago)
		references Pagos(idPago) on delete cascade
);

create table AuditoriaFacturas(
	idAuditoriaFactura int auto_increment,
    fecha datetime default now(),
    estado enum('Pendiente','Completada','Cancelada'),
    descripcion varchar(256) not null,
    idFactura int not null,
    idEmpleado int not null,
    idCompra int not null,
    idPago int not null,
    constraint pk_auditoria_facturas primary key (idAuditoriaFactura),
    constraint fk_auditoria_facturas_facturas foreign key (idFactura)
		references Facturas(idFactura) on delete cascade,
	constraint fk_auditoria_facturas_empleados foreign key (idEmpleado)
		references Usuarios(idUsuario) on delete cascade,
	constraint fk_auditoria_facturas_compras foreign key (idCompra)
		references Compras(idCompra) on delete cascade,
	constraint fk_auditoria_facturas_pagos foreign key (idPago)
		references Pagos(idPago) on delete cascade
);

create table AuditoriaCompras(
	idAuditoriaCompra int auto_increment,
    idCompra int not null,
    fecha datetime default now(),
    estado enum('Pendiente','Completada','Cancelada'),
    descripcion varchar(256) not null,
    constraint pk_auditoria_compras primary key (idAuditoriaCompra),

	constraint fk_auditoria_compras_compras foreign key (idCompra)
		references Compras(idCompra) on delete cascade
);

create table AuditoriaPagos(
	idAuditoriaPago int auto_increment,
    idPago int not null,
    idCompra int not null,
    fecha datetime default now(),
    estado enum('Pendiente','Completada','Cancelada'),
    descripcion varchar(256) not null,
    constraint pk_auditoria_pagos primary key (idAuditoriaPago),
    constraint fk_auditoria_pagos_pagos foreign key (idPago)
		references Pagos(idPago) on delete cascade,
    constraint fk_auditoria_pagos_compras foreign key (idCompra)
		references Compras(idCompra) on delete cascade
);

CREATE TABLE AuditoriaProductos (
    idAuditoriaProducto INT AUTO_INCREMENT PRIMARY KEY,
    idProducto INT NOT NULL,
    precio_anterior DECIMAL(10,2),
    precio_nuevo DECIMAL(10,2),
    stock_anterior INT,
    stock_nuevo INT,
    fecha_cambio DATETIME DEFAULT NOW(),
    descripcion VARCHAR(255),
    FOREIGN KEY (idProducto) REFERENCES Productos(idProducto) ON DELETE CASCADE
);
create table Cliente(
	idCliente int auto_increment,
	nombreCliente varchar (128),
    NIT varchar(128),
    Numero int(8),
    idCompra int,
    constraint pk_clientes primary key (idCliente), 
    constraint fk_facturas_cliente foreign key (idCompra)
		references Compras(idCompra) on delete cascade
);

-- -----------------------------------------------------------------------------------------------------------CRUD--------------------------------------------------------------------------------------------------------------------
-- CRUD CLIENTE

-- CREATE:
DELIMITER $$ 
create procedure sp_AgregarCliente(
		in p_nombreCliente varchar(200),
		in p_NIT varchar(200),
		in p_idCompra int,
        in p_numero int)
	begin
		insert into CLiente(nombreCliente, NIT,idCompra,Numero)
		values(p_nombreCliente, p_NIT, p_idCompra,p_numero);
	end;
$$
DELIMITER ;
--

-- READ:
DELIMITER $$
create procedure sp_ListarClientes()
    begin
		select 
        idCliente as ID,
        nombreCliente as CLIENTE,
        NIT as NIT,
        idCompra as COMPRA,
        Numero as NUMERO
        from Cliente;
    end;
$$
DELIMITER ;
call sp_ListarClientes();

-- UPDATE
DELIMITER $$
create procedure sp_ActualizarCliente(
		in p_idCliente int,
		in p_nombreCliente varchar(200),
		in p_NIT varchar(200),
		in p_idCompra int,
        in p_numero int)
	begin
		update Cliente
			set
				nombreCliente = p_nombreCliente,
				NIT = p_NIT,
				idCompra = p_idCompra,
                Numero = p_numero
            where 
				p_idCliente = idCliente;
		
	end;
$$
DELIMITER ;

-- DELETE

DELIMITER $$
create procedure sp_EliminarCliente(in p_idCliente int)
    begin
		delete 
        from Cliente
			where idCliente = p_idClientes;
    end 
$$
DELIMITER ;


-- CRUD USUARIOS

-- CREATE
DELIMITER $$ 
create procedure sp_AgregarUsuario(
		in p_nombreUsuario varchar(200),
		in p_correoUsuario varchar(200),
		in p_contraseñaUsuario varchar(200),
		in p_tipo enum ('Cliente','Empleado','Admin'))
	begin
		insert into Usuarios(nombreUsuario, correoUsuario, contraseñaUsuario, tipo)
		values(p_nombreUsuario, p_correoUsuario,p_contraseñaUsuario, p_tipo);
	end;
$$
DELIMITER ;

-- READ
DELIMITER $$
create procedure sp_ListarUsuario()
    begin
		select 
        idUsuario as ID,
        nombreUsuario as USUARIO,
        correoUsuario as CORREO,
        contraseñaUsuario as CONTRASEÑA,
        tipo as TIPO 
        from Usuarios;
    end;
$$
DELIMITER ;
call sp_ListarUsuario();

-- UPDATE
DELIMITER $$
create procedure sp_ActualizarUsuario(
		in p_idUsuario int,
		in p_nombreUsuario varchar(200),
		in p_correoUsuario varchar(200),
		in p_contraseñaUsuario varchar(200),
		in p_tipo enum ('Cliente','Empleado','Admin'))
	begin
		update Usuarios
			set
				nombreUsuario = p_nombreUsuario,
				correoUsuario = p_correoUsuario,
				contraseñaUsuario = p_contraseñaUsuario,
				tipo = p_tipo
            where 
				p_idUsuario = idUsuario;
		
	end;
$$
DELIMITER ;
-- call sp_ActualizarUsuario(1,'Kevin Kinalito','kk@gmail.com','2024001','Admin');


-- DELETE
DELIMITER $$
create procedure sp_EliminarUsuario(in p_idUsuario int)
    begin
		delete 
        from Usuarios
			where idUsuario = p_idUsuario;
    end 
$$
DELIMITER ;
-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- CRUD PRODUCTOS

-- CREATE
DELIMITER $$
create procedure sp_agregarProducto(
    in p_nombreProducto varchar (200),
    in p_precioProducto decimal(10,2),
    in p_stockProducto int,
    in p_codigoBarras varchar(200))
	begin
		insert into Productos(
        nombreProducto,precioProducto,stockProducto,codigoBarras)
        values(p_nombreProducto,p_precioProducto,p_stockProducto,p_codigoBarras);
    end
$$
DELIMITER ;

-- READ
DELIMITER $$
create procedure sp_ListarProductos()
    begin
		select 
        idProducto as ID,
        nombreProducto as PRODUCTO,
        precioProducto as PRECIO,
        stockProducto as STOCK,
        codigoBarras as 'CODIGO DE BARRAS'
        from Productos;
    end;
$$
DELIMITER ;
-- call sp_ListarProductos();

-- UPDATE
DELIMITER $$
create procedure sp_ActualizarProducto(
		in p_idProducto int,
		in p_nombreProducto varchar(200),
		in p_precioProducto decimal(10,2),
		in p_stockProducto int,
		in p_codigoBarras varchar (200))
	begin
		update Productos
			set
				nombreProducto = p_nombreProducto,
				precioProducto = p_precioProducto,
				stockProducto = p_stockProducto,
				codigoBarras = p_codigoBarras
            where 
				p_idProducto = idProducto;
		
	end;
$$
DELIMITER ;
-- call sp_ActualizarProducto(1,'Señorial',2.0,10,'A-0020-Z');

-- DELETE
DELIMITER $$
create procedure sp_EliminarProducto(in p_idProducto int)
    begin
		delete 
        from Productos
			where idProducto = p_idProducto;
    end 
$$
DELIMITER ;
-- call sp_EliminarProducto(1);
-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- CRUD COMPRAS

-- CREATE
DELIMITER $$
create procedure sp_agregarCompra(
    in p_estadoCompra enum ('Pendiente','Completada','Cancelada'),
    in p_estadoPago enum ('Pendiente','Pagado'))
	begin
		insert into Compras(
        estadoCompra,estadoPago)
        values(p_estadoCompra,p_estadoPago);
    end
$$
DELIMITER ;


-- READ
DELIMITER $$
CREATE PROCEDURE sp_ListarCompras()
BEGIN
    SELECT 
        c.idCompra AS COMPRA,
        (SELECT SUM(subtotal) FROM DetalleCompras WHERE idCompra = c.idCompra) * 1.12 AS TOTAL,
        c.estadoCompra AS ESTADO_COMPRA,
        c.estadoPago AS ESTADO_PAGO,
        c.fechaCompra AS FECHA
    FROM Compras c;
END$$
DELIMITER ;
call sp_ListarCompras();	

-- UPDATE
DELIMITER $$
create procedure sp_ActualizarCompras(
		in p_idCompra int,
		in p_estadoCompra enum('Pendiente','Completada','Cancelada'),
		in p_estadoPago enum('Pendiente', 'Pagado'))
	begin
		update Compras
			set
                estadoCompra = p_estadoCompra,
				estadoPago = p_estadoPago
            where 
				p_idCompra = idCompra;
		
	end;
$$
DELIMITER ;
-- call sp_ActualizarCompras(2,2,'Completada','Pendiente');

-- DELETE
DELIMITER $$
create procedure sp_EliminarCompras(in p_idCompras int)
    begin
		delete 
        from Compras
			where idCompra = p_idCompras;
    end 
$$
DELIMITER ;
-- call sp_EliminarCompras(1);
-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- CRUD DETALLECOMPRAS

-- CREATE
DELIMITER $$
create procedure sp_agregarDetalleCompra(
	in p_idCompra int,
    in p_idProducto int,
    in p_cantidad int)
	begin
		insert into DetalleCompras(
        idCompra,idProducto,cantidad)
        values(p_idCompra,p_idProducto,p_cantidad);
    end
$$
DELIMITER ;

-- READ
DELIMITER $$
create procedure sp_ListarDetalleCompras()
    begin
		select 
        idCompra as COMPRA,
        idProducto as PRODUCTO,
        cantidad as CANTIDAD,
        subtotal as SUBTOTAL
        from DetalleCompras;
    end;
$$
DELIMITER ;
call sp_ListarDetalleCompras();

-- UPDATE
DELIMITER $$
create procedure sp_ActualizarDetalleCompras(
		in p_idCompra int,
        in p_idProducto int,
		in p_cantidad int,
		in p_subtotal decimal(10,2))
	begin
		update DetalleCompras
			set
                cantidad = p_cantidad,
				subtotal = p_subtotal
            where 
				 idCompra = p_idCompra and idProducto = p_idProducto;
		
	end;
$$
DELIMITER ;
-- call sp_ActualizarDetalleCompras(2,2,2,3.0);

-- DELETE
DELIMITER $$
create procedure sp_EliminarDetalleCompras(in p_idCompras int)
    begin
		delete 
        from DetalleCompras
			where idCompra = p_idCompras;
    end 
$$
DELIMITER ;
-- call sp_EliminarDetalleCompras(2);

 DELIMITER $$
CREATE PROCEDURE sp_ReporteVentas(
    IN p_fechaInicio DATE,
    IN p_fechaFin DATE)
BEGIN
    SELECT 
        c.idCompra,
        u.nombreUsuario AS cliente,
        COUNT(dc.idProducto) AS productos,
        SUM(dc.subtotal) AS total,
        c.fechaCompra
    FROM Compras c

    JOIN DetalleCompras dc ON c.idCompra = dc.idCompra
    WHERE c.fechaCompra BETWEEN p_fechaInicio AND p_fechaFin
    AND c.estadoCompra = 'Completada'
    GROUP BY c.idCompra;
END$$
DELIMITER ;
 
 -- -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- CRUD PAGOS

-- CREATE
DELIMITER $$
create procedure sp_AgregarPagos(
    in p_metodoPago enum('Efectivo','Tarjeta','Transferencia'),
    in p_cantidad decimal(10,2),
    in p_idCompra int,
    out p_idPago int)
begin
    insert into Pagos(metodoPago, cantidad, idCompra)
    values(p_metodoPago, p_cantidad, p_idCompra);
    
    set p_idPago = LAST_INSERT_ID();
end$$
DELIMITER ;

-- READ
DELIMITER $$
create procedure sp_ListarPagos()
    begin
		select 
        idPago as PAGO,
        fecha as FECHA,
        cantidad as CANTIDAD,
        idCompra as COMPRA
        from Pagos;
    end;
$$
DELIMITER ;
call sp_ListarPagos();

-- UPDATE
DELIMITER $$
create procedure sp_ActualizarPagos(
		in p_idPago int,
		in p_cantidad decimal(10,2),
		in p_idCompra int)
	begin
		if not exists 
        (select 1 from Compras where idCompra = p_idCompra) 
        then 
        signal sqlstate '45000' 
        set message_text = 'La compra no existe';
        else
		update Pagos
			set
                cantidad = p_cantidad,
				idCompra = p_idCompra
            where 
				idPago = p_idPago;
		end if;
	end;
$$
DELIMITER ;
-- call sp_ActualizarPagos(2,1055,2);

-- DELETE
DELIMITER $$
create procedure sp_EliminarPagos(in p_idPagos int)
    begin
		delete 
        from Pagos
			where idPago = p_idPagos;
    end 
$$
DELIMITER ;
-- call sp_EliminarPagos(1);
 
 -- ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- CRUD FACTURAS

-- CREATE
DELIMITER $$
create procedure sp_AgregarFactura(
    in p_metodoPago enum('Efectivo','Tarjeta'),
    in p_idEmpleado int,
    in p_idCompra int,
    in p_idPago int)
	begin
		insert into Facturas(
        metodoPago,idEmpleado,idCompra,idPago)
        values(p_metodoPago,p_idEmpleado,p_idCompra,p_idPago);
    end
$$
DELIMITER ;

-- READ
DELIMITER $$
create procedure sp_ListarFactura()
    begin
		select 
        idFactura as FACTURA,
        fecha as FECHA,
        total as TOTAL,
        metodoPago as METODO_PAGO,
        idEmpleado as EMPLEADO,
        idCompra as COMPRA,
        idPago as PAGO
        from Facturas;
    end;
$$
DELIMITER ;
call sp_ListarFactura();

-- UPDATE
DELIMITER $$
create procedure sp_ActualizarFactura(
		in p_idFactura int,
		in p_total decimal(10,2),
		in p_metodoPago enum('Efectivo','Tarjeta','Transferencia'),
		in p_idEmpleado int,
		in p_idCompra int,
		in p_idPago int)
	begin
		update Facturas
			set
                total = p_total,
				metodoPago = p_metodoPago,
 
                idEmpleado = p_idEmpleado,
                idCompra = p_idCompra,
                idPago = p_idPago
            where 
				p_idFactura = idFactura;
	end;
$$
DELIMITER ;
-- call sp_ActualizarFactura(2,1000.00,'Tarjeta',2,2,2,2);

-- DELETE
DELIMITER $$
create procedure sp_EliminarFacturas(in p_idFactura int)
    begin
		delete 
        from Facturas
			where idFactura = p_idFactura;
    end 
$$
DELIMITER ;
-- call sp_EliminarFacturas(1); 
 -- ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- CRUD AuditoriaFactura

-- CREATE
DELIMITER $$
create procedure sp_AgregarAuditoriaFactura(
    in p_estado enum('Pendiente','Completada','Cancelada'),
    in p_descripcion varchar(256),
    in p_idFactura int,
    in p_idEmpleado int,
    in p_idCompra int,
    in p_idPago int)
	begin
		insert into AuditoriaFacturas(
        estado,descripcion,idFactura,idEmpleado,idCompra,idPago)
        values(p_estado,p_descripcion,p_idFactura,p_idEmpleado,p_idCompra,p_idPago);
    end
$$
DELIMITER ;
-- call sp_AgregarAuditoriaFactura('Completada','PagoZzz',2,2,2,2,2);

-- READ
DELIMITER $$
create procedure sp_ListarAuditoriaFactura()
    begin
		select
        fecha as FECHA,
        estado as ESTADO,
        descripcion as DESCRIPCION,
        idFactura as FACTURA,
        idEmpleado as EMPLEADO,
        idCompra as COMPRA,
        idPago as PAGO
        from AuditoriaFacturas;
    end;
$$
DELIMITER ;
call sp_ListarAuditoriaFactura();

-- UPDATE
DELIMITER $$
create procedure sp_ActualizarAuditoriaFactura(
		in p_idAuditoriaFactura int,
        in p_estado enum('Pendiente','Completada','Cancelada'),
		in p_descripcion varchar(256),
		in p_idFactura int,

		in p_idEmpleado int,
		in p_idCompra int,
		in p_idPago int)
	begin
		update AuditoriaFacturas
			set
				estado = p_estado,
                descripcion = p_descripcion,
				idFactura = p_idFactura,
                idEmpleado = p_idEmpleado,
                idCompra = p_idCompra,
                idPago = p_idPago
            where 
				p_idAuditoriaFactura = idAuditoriaFactura;
	end;
$$
DELIMITER ;
-- call sp_ActualizarAuditoriaFactura(2,'Pendiente','Zzz',2,2,2,2,2);

-- DELETE
DELIMITER $$
create procedure sp_EliminarAuditoriaFactura(in p_idAuditoriaFactura int)
    begin
		delete 
        from AuditoriaFacturas
			where idAuditoriaFactura = p_idAuditoriaFactura;
    end 
$$
DELIMITER ;
-- call sp_EliminarAuditoriaFactura(1);
-- --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- CRUD AuditoriaCompra

-- CREATE
DELIMITER $$
create procedure sp_AgregarAuditoriaCompra(
    in p_idCompra int,

    in p_estado enum('Pendiente','Completada','Cancelada'),
    in p_descripcion varchar(256))
	begin
		insert into AuditoriaCompras(
        idCompra,estado,descripcion)
        values(p_idCompra,p_estado,p_descripcion);
    end
$$
DELIMITER ;
-- call sp_AgregarAuditoriaCompra(2,2,'Completada','Compra procesada correctamente');

-- READ
DELIMITER $$
create procedure sp_ListarAuditoriaCompra()
    begin
		select
        fecha as FECHA,
        estado as ESTADO,
        descripcion as DESCRIPCION,
        idCompra as COMPRA

        from AuditoriaCompras;
    end;
$$
DELIMITER ;
call sp_ListarAuditoriaCompra();

-- UPDATE
DELIMITER $$
create procedure sp_ActualizarAuditoriaCompra(
		in p_idAuditoriaCompra int,
        in p_idCompra int,
        in p_estado enum('Pendiente','Completada','Cancelada'),
		in p_descripcion varchar(256))
	begin
		update AuditoriaCompras
			set
				idCompra = p_idCompra,
				estado = p_estado,
                descripcion = p_descripcion
            where 
				p_idAuditoriaCompra = idAuditoriaCompra;
		
	end;
$$
DELIMITER ;
-- call sp_ActualizarAuditoriaCompra(2,2,2,'Pendiente','Compra en espera');

-- DELETE
DELIMITER $$
create procedure sp_EliminarAuditoriaCompra(in p_idAuditoriaCompra int)
    begin
		delete 
        from AuditoriaCompras
			where idAuditoriaCompra = p_idAuditoriaCompra;
    end 
$$
DELIMITER ;
-- call sp_EliminarAuditoriaCompra(1);

-- --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- CRUD AuditoriaPago

-- CREATE
DELIMITER $$
create procedure sp_AgregarAuditoriaPago(
    in p_idPago int,
    in p_idCompra int,
    in p_estado enum('Pendiente','Completada','Cancelada'),
    in p_descripcion varchar(256))
	begin
		insert into AuditoriaPagos(
        idPago,idCompra,estado,descripcion)
        values(p_idPago,p_idCompra,p_estado,p_descripcion);
    end
$$
DELIMITER ;
-- call sp_AgregarAuditoriaPago(2,2,'Completada','Pago verificado exitosamente');

-- READ
DELIMITER $$
create procedure sp_ListarAuditoriaPago()
    begin
		select
        fecha as FECHA,
        estado as ESTADO,
        descripcion as DESCRIPCION,
        idPago as PAGO,
        idCompra as COMPRA
        from AuditoriaPagos;
    end;
$$
DELIMITER ;
-- call sp_ListarAuditoriaPago();

-- UPDATE
DELIMITER $$
create procedure sp_ActualizarAuditoriaPago(
		in p_idAuditoriaPago int,
        in p_idPago int,
        in p_idCompra int,
        in p_estado enum('Pendiente','Completada','Cancelada'),
		in p_descripcion varchar(256))
	begin
		update AuditoriaPagos
			set
				idPago = p_idPago,
                idCompra = p_idCompra,
				estado = p_estado,
                descripcion = p_descripcion
            where 
				p_idAuditoriaPago = idAuditoriaPago;
		
	end;
$$
DELIMITER ;
-- call sp_ActualizarAuditoriaPago(2,2,2,'Pendiente','Pago en revisión');

-- DELETE
DELIMITER $$
create procedure sp_EliminarAuditoriaPago(in p_idAuditoriaPago int)
    begin
		delete 
        from AuditoriaPagos
			where idAuditoriaPago = p_idAuditoriaPago;
    end 
$$
DELIMITER ;
-- call sp_EliminarAuditoriaPago(1);

-- --------------------------------------------------------------------------   
-- Auditorias

-- Compras
delimiter $$
	create trigger tr_GenerarAuditoriaCompras_after_update
    after update
    on Compras
    for each row
		begin
				call sp_AgregarAuditoriaCompra(
					old.idCompra, 
                    'Pendiente',
					concat('Se hizo un cambio en Compras el estado de la compra cambio a: ',new.estadoCompra,' y el estado del pago es:',new.estadoPago));
        end$$
delimiter ;


-- Pagos
delimiter $$
	create trigger tr_GenerarAuditoriaPagos_after_update
    after update
    on pagos
    for each row
		begin
				call sp_AgregarAuditoriaPago(
					old.idPago, 
                    old.idCompra,
                    'Pendiente',
					concat('Se hizo un cambio en pagos el metodo de pago cambio a: ',
                    new.metodoPago,
                    ' y la cantidad pago es:',
                    new.cantidad));
        end$$
delimiter ;

-- facturas
delimiter $$
	create trigger tr_GenerarAuditoriaFacturas_after_update
    after update
    on Facturas
    for each row
		begin
				call sp_AgregarAuditoriaFactura(
					'Pendiente',
                    concat('Se hizo un cambio en facturas el metodo de pago cambio a: ',
                    new.metodoPago,
                    ' y el total es: ',
                    new.total),
                    old.idFactura, 

                    old.idEmpleado, 
                    old.idCompra,
                    old.idPago);
        end$$
delimiter ;

/*
delimiter $$
create trigger tr_ActualizarEstadoPago_After_Insert
after insert on Pagos
for each row
begin
    declare total_pagado decimal(10,2);
    declare total_compra decimal(10,2);
    
    -- Calcular el total pagado para esta compra
    select sum(cantidad) into total_pagado 
    from Pagos 
    where idCompra = new.idCompra;
    
    -- Calcular el total de la compra
    select sum(subtotal) into total_compra
    from DetalleCompras
    where idCompra = new.idCompra;
    
    -- Actualizar estado de pago
    if total_pagado >= total_compra then
        update Compras 
        set estadoPago = 'Pagado'
        where idCompra = new.idCompra;
    end if;
end$$
delimiter ;

delimiter $$
create trigger tr_ActualizarEstadoCompra_After_Update
after update on Compras
for each row
begin
    if new.estadoPago = 'Pagado' and old.estadoPago != 'Pagado' then
        update Compras
        set estadoCompra = 'Completada'
        where idCompra = new.idCompra;
    end if;
end$$
delimiter ;
*/

delimiter $$
create trigger tr_AuditoriaProductos_After_Update
after update on Productos
for each row
begin
    if old.precioProducto != new.precioProducto or old.stockProducto != new.stockProducto then
        insert into AuditoriaProductos (
            idProducto, 
            precio_anterior, 
            precio_nuevo, 
            stock_anterior, 
            stock_nuevo, 
            fecha_cambio,
            descripcion
        )
        values (
            new.idProducto,
            old.precioProducto,
            new.precioProducto,
            old.stockProducto,
            new.stockProducto,
            now(),
            concat('Cambio en producto ', new.nombreProducto)
        );
    end if;
end$$
delimiter ;


delimiter $$
create trigger tr_CalcularSubTotal_Before_Insert
before insert on DetalleCompras
for each row
begin
    declare precio decimal(10,2);
    
    select precioProducto into precio from Productos where idProducto = new.idProducto;
    
    set new.subtotal = precio * new.cantidad;
end$$
delimiter ;

delimiter $$
create trigger tr_CalcularTotal_Before_Insert
before insert on Facturas
for each row
begin
    declare total_compra decimal(10,2);
    
    select sum(subtotal) into total_compra 
    from detallecompras 
    where idCompra = new.idCompra;
    
    set new.total = total_compra;
end$$
delimiter ;

call sp_AgregarUsuario("Wilson Florian","w","w","Admin");
call sp_AgregarUsuario("Byron Pineda","b","b","Admin");
call sp_AgregarUsuario("Roger Valladares","r","r","Admin");
call sp_AgregarUsuario("Dennys Morales","d","d","Admin");
call sp_AgregarUsuario("Anderson Sosa","a","a","Admin");
call sp_AgregarUsuario("Marcos García","m","m","Admin");
call sp_AgregarUsuario("Iosef Suarez","i","i","Admin");

call sp_AgregarUsuario("Wilson","wf","wf","Empleado");
call sp_AgregarUsuario("Byron","bp","bp","Empleado");
call sp_AgregarUsuario("Roger","rv","rv","Empleado");
call sp_AgregarUsuario("Dennys","dm","dm","Empleado");
call sp_AgregarUsuario("Anderson","as","as","Empleado");
call sp_AgregarUsuario("Marcos","mg","mg","Empleado");
call sp_AgregarUsuario("Iosef","is","is","Empleado");

call sp_ListarUsuario();

call sp_AgregarProducto('Tortrix','1.5',10,'A-0010-Z');
call sp_AgregarProducto('Pikaron','2',15,'A-0030-Z');
call sp_AgregarProducto('Cheto','3.5',30,'A-0040-Z');
call sp_ListarProductos();

-- call sp_AgregarCompra('Pendiente','Pagado');
-- call sp_AgregarCompra('Completada','Pagado');
-- call sp_AgregarCompra('Pendiente','Pagado');
-- call sp_ActualizarCompras(1,"Completada","Pagado");
call sp_ListarCompras();


call sp_ListarDetalleCompras();

-- call sp_AgregarPagos('Efectivo',10.5,1);
-- call sp_AgregarPagos('Tarjeta',20.5,2);
-- call sp_ListarPagos();
-- call sp_agregarDetalleCompra(1,1,3);
-- call sp_ListarDetalleCompras;
-- call sp_AgregarFactura("Efectivo",1,1,1);
call sp_ListarFactura();
-- call sp_AgregarCliente('Luis','224647202',1, 545);
