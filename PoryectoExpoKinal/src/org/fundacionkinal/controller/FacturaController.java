
package org.fundacionkinal.controller;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.fundacionkinal.database.Conexion;
import org.fundacionkinal.model.Factura;
import org.fundacionkinal.model.Producto;
import org.fundacionkinal.system.Main;

/**
 * FXML Controller class
 *
 * @author Wilson Florian
 */
public class FacturaController implements Initializable {

    @FXML
    private Button btnRegresar, btnAgregar, btnEliminar, btnFinalizar;

    @FXML
    private TableView<Producto> tablaProductos;

    @FXML
    private TableColumn<Producto, Integer> colId;
    @FXML
    private TableColumn<Producto, String> colCodigo;
    @FXML
    private TableColumn<Producto, String> colProducto;
    @FXML
    private TableColumn<Producto, Double> colPrecioUnitario;
    @FXML
    private TableColumn<Factura, Integer> colCantidad;
    @FXML
    private TableColumn<Factura, Double> colSubtotal;

    @FXML
    private TextField txtCodigo, txtProducto, txtPrecio, txtCantidad;

    private ObservableList<Producto> listaProductos;
    private Main principal;
    private int idCompraActual;
    private int cantidadActual = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        crearNuevaCompra();
        deshabilitarControles();
        txtCodigo.setDisable(false);
        setFormatoColumnaModelo();
        cargarDatos();

        txtCodigo.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                buscarProductoPorCodigo(newValue);
            }
        });
    }

    private void crearNuevaCompra() {
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            Statement stmt = conexion.createStatement();

            stmt.executeUpdate("INSERT INTO Compras(estadoCompra, estadoPago) VALUES ('Pendiente', 'Pendiente')",
                    Statement.RETURN_GENERATED_KEYS);

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                idCompraActual = generatedKeys.getInt(1);
                System.out.println("Nueva compra creada con ID: " + idCompraActual);
            } else {
                mostrarAlerta("No se pudo obtener el ID de la nueva compra");
            }

            generatedKeys.close();
            stmt.close();
        } catch (SQLException e) {
            mostrarAlerta("Error al crear nueva compra: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setFormatoColumnaModelo() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioProducto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }

    public void cargarDatos() {
        listaProductos = FXCollections.observableArrayList(listarProductosEnFactura());
        tablaProductos.setItems(listaProductos);
        if (!listaProductos.isEmpty()) {
            tablaProductos.getSelectionModel().selectFirst();
        }
    }

    private void buscarProductoPorCodigo(String codigo) {
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            String sql = "SELECT idProducto, nombreProducto, precioProducto FROM Productos WHERE codigoBarras = ?";
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                cantidadActual++;
                txtCantidad.setText(String.valueOf(cantidadActual));

                txtProducto.setText(rs.getString("nombreProducto"));
                txtPrecio.setText(String.valueOf(rs.getDouble("precioProducto")));

                btnAgregar.setDisable(false);
            } else {
                mostrarAlerta("Producto no encontrado con el código: " + codigo);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error al buscar producto: " + e.getMessage());
        }
    }

    public ArrayList<Producto> listarProductosEnFactura() {
        ArrayList<Producto> productos = new ArrayList<>();
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            String sql = "SELECT p.idProducto, p.codigoBarras, p.nombreProducto, p.precioProducto, "
                    + "dc.cantidad, dc.subtotal "
                    + "FROM DetalleCompras dc "
                    + "JOIN Productos p ON dc.idProducto = p.idProducto "
                    + "WHERE dc.idCompra = ?";
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, idCompraActual);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Producto producto = new Producto(
                        rs.getInt("idProducto"),
                        rs.getString("nombreProducto"),
                        rs.getString("codigoBarras"),
                        rs.getDouble("precioProducto")
                );
                producto.setCantidad(rs.getInt("cantidad"));
                producto.setSubtotal(rs.getDouble("subtotal"));
                productos.add(producto);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar productos de la factura: " + e.getMessage());
        }
        return productos;
    }

    @FXML
    public void agregarProducto() {
        try {
            if (idCompraActual <= 0) {
                mostrarAlerta("No hay una compra válida asociada");
                return;
            }

            String codigo = txtCodigo.getText();
            Connection conexion = Conexion.getInstancia().getConexion();

            String sqlProducto = "SELECT idProducto FROM Productos WHERE codigoBarras = ?";
            PreparedStatement stmtProducto = conexion.prepareStatement(sqlProducto);
            stmtProducto.setString(1, codigo);
            ResultSet rsProducto = stmtProducto.executeQuery();

            if (rsProducto.next()) {
                int idProducto = rsProducto.getInt("idProducto");

                String sqlVerificar = "SELECT cantidad FROM DetalleCompras WHERE idCompra = ? AND idProducto = ?";
                PreparedStatement stmtVerificar = conexion.prepareStatement(sqlVerificar);
                stmtVerificar.setInt(1, idCompraActual);
                stmtVerificar.setInt(2, idProducto);
                ResultSet rsVerificar = stmtVerificar.executeQuery();

                if (rsVerificar.next()) {
                    int cantidadActual = rsVerificar.getInt("cantidad");
                    int nuevaCantidad = cantidadActual + this.cantidadActual;

                    String sqlActualizar = "UPDATE DetalleCompras SET cantidad = ?, subtotal = (SELECT precioProducto FROM Productos WHERE idProducto = ?) * ? WHERE idCompra = ? AND idProducto = ?";
                    PreparedStatement stmtActualizar = conexion.prepareStatement(sqlActualizar);
                    stmtActualizar.setInt(1, nuevaCantidad);
                    stmtActualizar.setInt(2, idProducto);
                    stmtActualizar.setInt(3, nuevaCantidad);
                    stmtActualizar.setInt(4, idCompraActual);
                    stmtActualizar.setInt(5, idProducto);
                    stmtActualizar.executeUpdate();
                } else {
                    CallableStatement procedimiento = conexion.prepareCall("{call sp_agregarDetalleCompra(?, ?, ?)}");
                    procedimiento.setInt(1, idCompraActual);
                    procedimiento.setInt(2, idProducto);
                    procedimiento.setInt(3, cantidadActual);
                    procedimiento.execute();
                }

                limpiarTexto();
                cantidadActual = 0;
                cargarDatos();
                btnAgregar.setDisable(true);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error al agregar detalle de compra: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void eliminarProducto() {
        Producto productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado != null) {
            try {
                Connection conexion = Conexion.getInstancia().getConexion();
                String sql = "DELETE FROM DetalleCompras WHERE idCompra = ? AND idProducto = ?";
                PreparedStatement stmt = conexion.prepareStatement(sql);
                stmt.setInt(1, idCompraActual);
                stmt.setInt(2, productoSeleccionado.getIdProducto());
                stmt.executeUpdate();

                cargarDatos();
            } catch (SQLException e) {
                mostrarAlerta("Error al eliminar producto: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Seleccione un producto de la tabla para eliminar");
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void limpiarTexto() {
        txtCodigo.clear();
        txtProducto.clear();
        txtPrecio.clear();
        txtCantidad.clear();
        txtCodigo.requestFocus();
    }

    public void btnRegresarActionEvent(ActionEvent evento) {
        principal.getLoginView();
    }

    @FXML
    private void cambiarNuevoFactura() {
        agregarProducto();
    }

    @FXML
    private void cambiarCancelarEliminar() {
        eliminarProducto();
    }

    @FXML
    private void FinalizarPedido() {
        double subtotal = 0;
        for (Producto producto : listaProductos) {
            subtotal += producto.getSubtotal();
        }

        principal.getFactura2View(idCompraActual, subtotal);
    }

    private void deshabilitarControles() {
        txtProducto.setDisable(true);
        txtPrecio.setDisable(true);
        txtCantidad.setDisable(true);
        btnAgregar.setDisable(true);
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
}
