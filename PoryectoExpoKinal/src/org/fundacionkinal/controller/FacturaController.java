
package org.fundacionkinal.controller;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
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

    private Main principal;
    
    @FXML private Button btnAgregar, btnEditar, btnEliminar, btnReporte;
    
    @FXML private TableView<Factura> tablaFacturas;
    
    @FXML private TableColumn colId, colFecha, colMetodoPago, colProducto,
            colCantidad, colTotal;
    
    @FXML private TextField txtId, txtCantidad, txtTotal;
    
    @FXML private DatePicker dpFecha;
    
    @FXML private Spinner<Double> spPeso;
    
    @FXML private ComboBox<Producto> cbProducto;
    
    @FXML private ToggleGroup tgMetodoPago;

    private ObservableList<Factura> listaFacturas;
    private ObservableList<Producto> listaProductos;
    
    private enum Acciones {
        Agregar, Editar, Eliminar, Ninguna
    };
    private Acciones accion = Acciones.Ninguna;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }  
    
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    private void cargarProductos() {
        listaProductos = FXCollections.observableArrayList();
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            String sql = 
                    "select idProducto, nombreProducto, precioProducto from Productos";
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                listaProductos.add(new Producto(
                        rs.getInt("idProducto"),
                        rs.getString("nombreProducto"),
                        rs.getDouble("precioProducto"),
                        0,
                        null
                ));
            }

            configurarComboBoxProductos();
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar clientes: " + e.getMessage());
        }
    }
    
    private void configurarComboBoxProductos() {
        cbProducto.setItems(listaProductos);
        
        cbProducto.setCellFactory(param -> new ListCell<Producto>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getIdProducto()+ " | " + item.getNombreProducto()+ " " + item.getPrecioProducto());
                }
            }
        });

        cbProducto.setButtonCell(new ListCell<Producto>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombreProducto()+ " " + item.getPrecioProducto());
                }
            }
        });
    }
    
    public void setFormatoColumnaModelo() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idFactura"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colMetodoPago.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("productos"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        
        colCantidad.setCellValueFactory(cellData -> {
            TableColumn.CellDataFeatures<Factura, Factura> data = (TableColumn.CellDataFeatures<Factura, Factura>) cellData;
            Factura factura = data.getValue();
            
            if (factura == null) return new SimpleStringProperty("");
            
            Producto producto = listaProductos.stream()
                .filter(c -> c.getIdProducto()== factura.getIdCompra())
                .findFirst()
                .orElse(null);
                
            return new SimpleStringProperty(producto != null ? 
                producto.getIdProducto()+ " | " + producto.getNombreProducto()+ " " + producto.getPrecioProducto(): "Sin producto");
        });
        
        colProducto.setCellValueFactory(cellData -> {
            TableColumn.CellDataFeatures<Factura, Factura> data = (TableColumn.CellDataFeatures<Factura, Factura>) cellData;
            Factura factura = data.getValue();
            
            if (factura == null) return new SimpleStringProperty("");
            
            Producto producto = listaProductos.stream()
                .filter(c -> c.getIdProducto()== factura.getIdCompra())
                .findFirst()
                .orElse(null);
                
            return new SimpleStringProperty(producto != null ? 
                producto.getIdProducto()+ " | " + producto.getNombreProducto()+ " " + producto.getPrecioProducto(): "Sin producto");
        });
    }
    
//    public void cargarDatos() {
//        listaFacturas = FXCollections.observableArrayList(listarMascotas());
//        tablaFacturas.setItems(listaFacturas);
//        if (!listaFacturas.isEmpty()) {
//            tablaFacturas.getSelectionModel().selectFirst();
//            getFacturaTextFiel();
//        }
//    }
    
//    public void getFacturaTextFiel() {
//        Factura facturaSeleccionada = tablaFacturas.getSelectionModel().getSelectedItem();
//        if (facturaSeleccionada != null) {
//            txtId.setText(String.valueOf(facturaSeleccionada.getIdFactura()));
//            txtCantidad.setText(String.valueOf(facturaSeleccionada.getIdCompra()));
//            dpFecha.setValue(LofacturaSeleccionada.getFecha());
//            txtTotal.setText(String.valueOf(facturaSeleccionada.getTotal()));
//
//            seleccionarRadioButtonMetodoPago(facturaSeleccionada.getMetodoPago());
//            seleccionarProducto(facturaSeleccionada.getIdCompra());
//        }
//    }
    
    private void seleccionarRadioButtonMetodoPago(String metodoPago) {
        tgMetodoPago.getToggles().forEach(toggle -> {
            RadioButton rb = (RadioButton) toggle;
            if ((metodoPago.equals("Efectivo") && rb.getText().equals("EFECTIVO"))) {
                tgMetodoPago.selectToggle(toggle);
            } else if ((metodoPago.equals("Tarjeta") && rb.getText().equals("TARJETA"))) {
                tgMetodoPago.selectToggle(toggle);
            }
        });
    }

    private void seleccionarProducto(int idCompra) {
        listaProductos.stream()
            .filter(producto -> producto.getIdProducto()== idCompra)
            .findFirst()
            .ifPresent(producto -> cbProducto.getSelectionModel().select(producto));
    }
    
    public ArrayList<Factura> listarFacturas() {
        ArrayList<Factura> facturas = new ArrayList<>();
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            String sql = "{call sp_ListarFacturas()}";
            CallableStatement stmt = conexion.prepareCall(sql);
            ResultSet rs = stmt.executeQuery();

//            while (rs.next()) {
//                facturas.add(new Factura(
//                        rs.getInt("ID"),
//                        rs.getTimestamp("FECHA DE EMISION"),
//                        rs.getString("METODO DE PAGO"),
//                        rs.getInt("CANTIDAD"),
//                        rs.getDouble("TOTAL")
//                ));
//            }
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar mascotas: " + e.getMessage());
        }
        return facturas;
    }
    
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
