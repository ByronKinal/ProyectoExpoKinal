use cajeroDB;

call sp_AgregarUsuario("Kevin Kinal","k@gmail.com","2024000","Admin");
call sp_AgregarUsuario("Kevin Chino","kc@gmail.com","2024100","Cliente");
call sp_ListarUsuario();

call sp_AgregarProducto('Tortrix','1.5',10,'A-0010-Z');
call sp_AgregarProducto('Pikaron','2',15,'A-0030-Z');
call sp_AgregarProducto('Cheto','3.5',30,'A-0040-Z');
call sp_ListarProductos();

call sp_AgregarCompra(1,'Pendiente','Pagado');
call sp_AgregarCompra(2,'Completada','Pagado');
call sp_AgregarCompra(2,'Pendiente','Pagado');
call sp_ListarCompras();


call sp_ListarDetalleCompras();

call sp_AgregarPagos('Efectivo',10.5,2);
call sp_AgregarPagos('Tarjeta',20.5,2);
call sp_ListarPagos();

call sp_ListarFactura();