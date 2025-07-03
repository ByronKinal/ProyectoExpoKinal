drop database if exists cajeroDB;
create database cajeroDB;
use cajeroDB;

create table Usuarios (
	idUsuario int auto_increment,
	nombreUsuario varchar(64) not null,
	correoUsuario varchar(128) not null,
	contraseñaUsuario varchar(128) not null,
    tipo enum('Cliente', 'Empleado', 'Admin'),
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
    idCliente int not null,
    estadoCompra enum('Pendiente','Completada','Cancelada'),
    estadoPago enum('Pendiente', 'Pagado'),
    fechaCompra datetime default now(),
    constraint pk_compras primary key (idCompra),
    constraint fk_compras_clientes foreign key (idCliente) 
		references Usuarios (idUsuario) on delete cascade
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
    idCliente int not null,
    idEmpleado int not null,
    idCompra int not null,
    idPago int not null,
    constraint pk_facturas primary key (idFactura),
    constraint fk_facturas_clientes foreign key (idCliente)
		references Usuarios(idUsuario) on delete cascade,
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
    idCliente int not null,
    idEmpleado int not null,
    idCompra int not null,
    idPago int not null,
    constraint pk_auditoria_facturas primary key (idAuditoriaFactura),
    constraint fk_auditoria_facturas_facturas foreign key (idFactura)
		references Facturas(idFactura) on delete cascade,
    constraint fk_auditoria_facturas_clientes foreign key (idCliente)
		references Usuarios(idUsuario) on delete cascade,
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
    idCliente int not null,
    fecha datetime default now(),
    estado enum('Pendiente','Completada','Cancelada'),
    descripcion varchar(256) not null,
    constraint pk_auditoria_compras primary key (idAuditoriaCompra),
    constraint fk_auditoria_compras_clientes foreign key (idCliente)
		references Usuarios(idUsuario) on delete cascade,
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


-- -----------------------------------------------------------------------------------------------------------CRUD--------------------------------------------------------------------------------------------------------------------

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
    in p_idCliente int,
    in p_estadoCompra enum ('Pendiente','Completada','Cancelada'),
    in p_estadoPago enum ('Pendiente','Pagado'))
	begin
		insert into Compras(
        idCliente,estadoCompra,estadoPago)
        values(p_idCliente,p_estadoCompra,p_estadoPago);
    end
$$
DELIMITER ;


-- READ
DELIMITER $$
create procedure sp_ListarCompras()
    begin
		select 
        idCompra as COMPRA,
        idCliente as CLIENTE,
        estadoCompra as ESTADO_COMPRA,
        estadoPago as ESTADO_PAGO,
        fechaCompra as FECHA
        from Compras;
    end;
$$
DELIMITER ;
call sp_ListarCompras();

-- UPDATE
DELIMITER $$
create procedure sp_ActualizarCompras(
		in p_idCompra int,
        in p_idCliente int,
		in p_estadoCompra enum('Pendiente','Completada','Cancelada'),
		in p_estadoPago enum('Pendiente', 'Pagado'))
	begin
		update Compras
			set
				idCliente = p_idCliente,
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
    in p_cantidad int,
    in p_subtotal decimal(10,2))
	begin
		insert into DetalleCompras(
        idCompra,idProducto,cantidad,subtotal)
        values(p_idCompra,p_idProducto,p_cantidad,p_subtotal);
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
    JOIN Usuarios u ON c.idCliente = u.idUsuario
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
    in p_idCompra int)
	begin
		insert into Pagos(
        metodoPago,cantidad,idCompra)
        values(p_metodoPago,p_cantidad,p_idCompra);
    end
$$
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
	in p_total decimal(10,2),
    in p_metodoPago enum('Efectivo','Tarjeta','Transferencia'),
    in p_idCliente int,
    in p_idEmpleado int,
    in p_idCompra int,
    in p_idPago int)
	begin
		insert into Facturas(
        total,metodoPago,idCliente,idEmpleado,idCompra,idPago)
        values(p_total,p_metodoPago,p_idCliente,p_idEmpleado,p_idCompra,p_idPago);
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
        idCliente as CLIENTE,
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
		in p_idCliente int,
		in p_idEmpleado int,
		in p_idCompra int,
		in p_idPago int)
	begin
		update Facturas
			set
                total = p_total,
				metodoPago = p_metodoPago,
                idCliente = p_idCliente,
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
    in p_idCliente int,
    in p_idEmpleado int,
    in p_idCompra int,
    in p_idPago int)
	begin
		insert into AuditoriaFacturas(
        estado,descripcion,idFactura,idCliente,idEmpleado,idCompra,idPago)
        values(p_estado,p_descripcion,p_idFactura,p_idCliente,p_idEmpleado,p_idCompra,p_idPago);
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
        idCliente as CLIENTE,
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
		in p_idCliente int,
		in p_idEmpleado int,
		in p_idCompra int,
		in p_idPago int)
	begin
		update AuditoriaFacturas
			set
				estado = p_estado,
                descripcion = p_descripcion,
				idFactura = p_idFactura,
                idCliente = p_idCliente,
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
    in p_idCliente int,
    in p_estado enum('Pendiente','Completada','Cancelada'),
    in p_descripcion varchar(256))
	begin
		insert into AuditoriaCompras(
        idCompra,idCliente,estado,descripcion)
        values(p_idCompra,p_idCliente,p_estado,p_descripcion);
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
        idCompra as COMPRA,
        idCliente as CLIENTE
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
        in p_idCliente int,
        in p_estado enum('Pendiente','Completada','Cancelada'),
		in p_descripcion varchar(256))
	begin
		update AuditoriaCompras
			set
				idCompra = p_idCompra,
                idCliente = p_idCliente,
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
                    old.idCliente,
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
                    old.idCliente,
                    old.idEmpleado, 
                    old.idCompra,
                    old.idPago);
        end$$
delimiter ;

-- ---------------------------- Echo por Byron -----------------------------------
delimiter $$
	create trigger tr_GenerarFactura_after_insert
	after insert on Pagos
	for each row
		begin
			declare totalFactura decimal(12,2);
			declare clienteID int;
			declare empleadoID int;
			declare estadoPagoCompra varchar(20);    
			select estadoPago into estadoPagoCompra 
			from Compras 
			where idCompra = new.idCompra;
			if estadoPagoCompra = 'Pagado' then
				set totalFactura = new.cantidad;
				select idCliente into clienteID 
				from Compras 
				where idCompra = new.idCompra;        
				select idUsuario into empleadoID 
				from Usuarios 
				where tipo in ('Empleado', 'Admin') 
				limit 1;
				
				-- Insertar factura solo si no existe ya
				if not exists (
					select 1 from Facturas 
					where idCompra = new.idCompra 
					and idPago = new.idPago
				) then
					insert into Facturas(
						total, 
						metodoPago, 
						idCliente, 
						idEmpleado, 
						idCompra, 
						idPago
					)
					values(
						totalFactura,
						new.metodoPago,
						clienteID,
						empleadoID,
						new.idCompra,
						new.idPago
					);
				end if;
			end if;
	end$$
delimiter ;
-- call sp_ListarFactura;

-- Triggers faltantes ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

DELIMITER $$
CREATE TRIGGER tr_ActualizarEstadoPago_after_insert
AFTER INSERT ON Pagos
FOR EACH ROW
BEGIN
    DECLARE total_pagado DECIMAL(10,2);
    DECLARE total_compra DECIMAL(10,2);
    
    -- Calcular el total pagado para esta compra
    SELECT SUM(cantidad) INTO total_pagado 
    FROM Pagos 
    WHERE idCompra = NEW.idCompra;
    
    -- Calcular el total de la compra
    SELECT SUM(subtotal) INTO total_compra
    FROM DetalleCompras
    WHERE idCompra = NEW.idCompra;
    
    -- Actualizar estado de pago
    IF total_pagado >= total_compra THEN
        UPDATE Compras 
        SET estadoPago = 'Pagado'
        WHERE idCompra = NEW.idCompra;
    END IF;
END$$
DELIMITER ;
-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

DELIMITER $$
CREATE TRIGGER tr_ActualizarEstadoCompra_after_update
AFTER UPDATE ON Compras
FOR EACH ROW
BEGIN
    IF NEW.estadoPago = 'Pagado' AND OLD.estadoPago != 'Pagado' THEN
        UPDATE Compras
        SET estadoCompra = 'Completada'
        WHERE idCompra = NEW.idCompra;
    END IF;
END$$
DELIMITER ;
4. Trigger para Registrar Cambios en Productos (Auditoría)
sql
DELIMITER $$
CREATE TRIGGER tr_AuditoriaProductos_after_update
AFTER UPDATE ON Productos
FOR EACH ROW
BEGIN
    IF OLD.precioProducto != NEW.precioProducto OR OLD.stockProducto != NEW.stockProducto THEN
        INSERT INTO AuditoriaProductos (
            idProducto, 
            precio_anterior, 
            precio_nuevo, 
            stock_anterior, 
            stock_nuevo, 
            fecha_cambio,
            descripcion
        )
        VALUES (
            NEW.idProducto,
            OLD.precioProducto,
            NEW.precioProducto,
            OLD.stockProducto,
            NEW.stockProducto,
            NOW(),
            CONCAT('Cambio en producto ', NEW.nombreProducto)
        );
    END IF;
END$$
DELIMITER ;

-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

