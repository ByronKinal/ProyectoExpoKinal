
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
import org.fundacionkinal.model.Producto;
import org.fundacionkinal.system.Main;

/**
 * FXML Controller class
 *
 * @author Wilson Florian
 */
public class ProductosController implements Initializable {

    @FXML private Button btnRegresar, btnAgregar, btnEditar, btnEliminar, btnReporte;
    
    @FXML private TableView<Producto> tablaProductos;
    
    @FXML private TableColumn<Producto, Integer> colId;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private TableColumn<Producto, String> colCodigoBarras;
    
    @FXML private TextField txtId, txtNombre, txtPrecio, txtStock, txtCodigoBarras;
    
    private ObservableList<Producto> listaProductos;
    private Main principal;
    private Producto modeloProducto;

    private enum Acciones {
        Agregar, Editar, Eliminar, Ninguna
    };
    private Acciones accion = Acciones.Ninguna;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setFormatoColumnaModelo();
        cargarDatos();
        tablaProductos.setOnMouseClicked(event -> {
            getProductoTextFiel();
            deshabilitarControles();
        });
        deshabilitarControles();
    }
    
    public void setFormatoColumnaModelo() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioProducto"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockProducto"));
        colCodigoBarras.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));
    }
    
    public void cargarDatos() {
        listaProductos = FXCollections.observableArrayList(listarProductos());
        tablaProductos.setItems(listaProductos);
        if (!listaProductos.isEmpty()) {
            tablaProductos.getSelectionModel().selectFirst();
            getProductoTextFiel();
        }
    }

    public void getProductoTextFiel() {
        Producto productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado != null) {
            txtId.setText(String.valueOf(productoSeleccionado.getIdProducto()));
            txtNombre.setText(productoSeleccionado.getNombreProducto());
            txtPrecio.setText(String.valueOf(productoSeleccionado.getPrecioProducto()));
            txtStock.setText(String.valueOf(productoSeleccionado.getStockProducto()));
            txtCodigoBarras.setText(productoSeleccionado.getCodigoBarras());
        }
    }

    public ArrayList<Producto> listarProductos() {
        ArrayList<Producto> productos = new ArrayList<>();
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            String sql = "SELECT idProducto, nombreProducto, precioProducto, stockProducto, codigoBarras FROM Productos";
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                productos.add(new Producto(
                        rs.getInt("idProducto"),
                        rs.getString("nombreProducto"),
                        rs.getDouble("precioProducto"),
                        rs.getInt("stockProducto"),
                        rs.getString("codigoBarras")
                ));
            }
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar productos: " + e.getMessage());
        }
        return productos;
    }
    
    private Producto getModeloProducto() {
        int idProducto = txtId.getText().isEmpty() ? 0 : Integer.parseInt(txtId.getText());
        String nombreProducto = txtNombre.getText();
        double precioProducto = txtPrecio.getText().isEmpty() ? 0 : Double.parseDouble(txtPrecio.getText());
        int stockProducto = txtStock.getText().isEmpty() ? 0 : Integer.parseInt(txtStock.getText());
        String codigoBarras = txtCodigoBarras.getText();

        return new Producto(idProducto, nombreProducto, precioProducto, stockProducto, codigoBarras);
    }
    
    public void agregarProducto() {
        if (validarCampos()) {
            modeloProducto = getModeloProducto();
            try {
                CallableStatement stmt = Conexion.getInstancia().getConexion()
                        .prepareCall("{call sp_agregarProducto(?, ?, ?, ?)}");
                stmt.setString(1, modeloProducto.getNombreProducto());
                stmt.setDouble(2, modeloProducto.getPrecioProducto());
                stmt.setInt(3, modeloProducto.getStockProducto());
                stmt.setString(4, modeloProducto.getCodigoBarras());
                stmt.execute();
                cargarDatos();
                cambiarOriginal();
            } catch (SQLException e) {
                mostrarAlerta("Error al agregar producto: " + e.getMessage());
            }
        }
    }

    public void editarProducto() {
        if (validarCampos()) {
            modeloProducto = getModeloProducto();
            try {
                CallableStatement stmt = Conexion.getInstancia().getConexion()
                        .prepareCall("{call sp_ActualizarProducto(?, ?, ?, ?, ?)}");
                stmt.setInt(1, modeloProducto.getIdProducto());
                stmt.setString(2, modeloProducto.getNombreProducto());
                stmt.setDouble(3, modeloProducto.getPrecioProducto());
                stmt.setInt(4, modeloProducto.getStockProducto());
                stmt.setString(5, modeloProducto.getCodigoBarras());
                stmt.execute();
                cargarDatos();
                cambiarOriginal();
            } catch (SQLException e) {
                mostrarAlerta("Error al editar producto: " + e.getMessage());
            }
        }
    }

    public void eliminarProducto() {
        modeloProducto = getModeloProducto();
        try {
            CallableStatement stmt = Conexion.getInstancia().getConexion()
                    .prepareCall("{call sp_EliminarProducto(?)}");
            stmt.setInt(1, modeloProducto.getIdProducto());
            stmt.execute();
            cargarDatos();
        } catch (SQLException e) {
            mostrarAlerta("Error al eliminar producto: " + e.getMessage());
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().isEmpty() || 
            txtPrecio.getText().isEmpty() || 
            txtStock.getText().isEmpty() || 
            txtCodigoBarras.getText().isEmpty()) {
            
            mostrarAlerta("Todos los campos son obligatorios");
            return false;
        }
        
        try {
            Double.parseDouble(txtPrecio.getText());
            Integer.parseInt(txtStock.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Precio y Stock deben ser valores numéricos");
            return false;
        }
        
        return true;
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void limpiarTexto() {
        txtId.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtStock.clear();
        txtCodigoBarras.clear();
    }

    public void btnRegresarActionEvent(ActionEvent evento) {
        principal.getMenuAdminView();
    }

    @FXML
    private void cambiarNuevoProducto() {
        switch (accion) {
            case Ninguna:
                cambiarGuardarEditar();
                accion = Acciones.Agregar;
                limpiarTexto();
                habilitarControles(true);
                break;
            case Agregar:
                agregarProducto();
                break;
            case Editar:
                editarProducto();
                break;
        }
    }

    @FXML
    private void cambiarEdicionProducto() {
        cambiarGuardarEditar();
        accion = Acciones.Editar;
        habilitarControles(true);
    }

    @FXML
    private void cambiarCancelarEliminar() {
        if (accion == Acciones.Ninguna) {
            eliminarProducto();
        } else {
            cambiarOriginal();
        }
    }

    private void cambiarGuardarEditar() {
        btnAgregar.setText("Guardar");
        btnEliminar.setText("Cancelar");
        btnEditar.setDisable(true);
    }

    private void cambiarOriginal() {
        btnAgregar.setText("Agregar");
        btnEliminar.setText("Eliminar");
        btnEditar.setDisable(false);
        accion = Acciones.Ninguna;
        deshabilitarControles();
    }

    private void deshabilitarControles() {
        txtId.setDisable(true);
        txtNombre.setDisable(true);
        txtPrecio.setDisable(true);
        txtStock.setDisable(true);
        txtCodigoBarras.setDisable(true);
        tablaProductos.setDisable(false);
    }

    private void habilitarControles(boolean habilitar) {
        txtId.setDisable(true); // ID siempre deshabilitado
        txtNombre.setDisable(!habilitar);
        txtPrecio.setDisable(!habilitar);
        txtStock.setDisable(!habilitar);
        txtCodigoBarras.setDisable(!habilitar);
        tablaProductos.setDisable(habilitar);
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
}