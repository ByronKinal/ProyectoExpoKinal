package org.fundacionkinal.controller;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import org.fundacionkinal.database.Conexion;
import org.fundacionkinal.model.Compra;
import org.fundacionkinal.system.Main;

/**
 * FXML Controller class
 *
 * @author Wilson Florian
 */
public class ComprasController implements Initializable {

    @FXML
    private Button btnRegresar, btnAgregar, btnEditar, btnEliminar, btnReporte;

    @FXML
    private TableView<Compra> tablaCompras;

    @FXML
    private TableColumn<Compra, Integer> colId;
    @FXML
    private TableColumn<Compra, Double> colTotal;
    @FXML
    private TableColumn<Compra, String> colEstadoCompra;
    @FXML
    private TableColumn<Compra, String> colEstadoPago;
    @FXML
    private TableColumn<Compra, LocalDateTime> colFecha;

    @FXML
    private TextField txtId, txtTotal;

    @FXML
    private DatePicker dpFecha;

    @FXML
    private ToggleGroup tgEstadoCompra, tgEstadoPago;

    private ObservableList<Compra> listaCompras;

    private Main principal;
    private Compra modeloCompra;

    private enum Acciones {
        Agregar, Editar, Eliminar, Ninguna
    };
    private Acciones accion = Acciones.Ninguna;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setFormatoColumnaModelo();
        cargarDatos();
        tablaCompras.setOnMouseClicked(event -> {
            getCompraTextFiel();
            deshabilitarControles();
        });
        deshabilitarControles();
    }

    public void setFormatoColumnaModelo() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idCompra"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colEstadoCompra.setCellValueFactory(new PropertyValueFactory<>("estadoCompra"));
        colEstadoPago.setCellValueFactory(new PropertyValueFactory<>("estadoPago"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
    }

    public void cargarDatos() {
        listaCompras = FXCollections.observableArrayList(listarCompras());
        tablaCompras.setItems(listaCompras);
        if (!listaCompras.isEmpty()) {
            tablaCompras.getSelectionModel().selectFirst();
            getCompraTextFiel();
        }
    }

    public void getCompraTextFiel() {
        Compra compraSeleccionada = tablaCompras.getSelectionModel().getSelectedItem();
        if (compraSeleccionada != null) {
            txtId.setText(String.valueOf(compraSeleccionada.getIdCompra()));
            dpFecha.setValue(compraSeleccionada.getFecha().toLocalDate());
            txtTotal.setText(String.valueOf(compraSeleccionada.getTotal()));

            seleccionarRadioButtonEstadoCompra(compraSeleccionada.getEstadoCompra());
            seleccionarRadioButtonEstadoPago(compraSeleccionada.getEstadoPago());
        }
    }

    private void seleccionarRadioButtonEstadoCompra(String estado) {
        tgEstadoCompra.getToggles().forEach(toggle -> {
            RadioButton rb = (RadioButton) toggle;
            if (estado.equalsIgnoreCase("Pendiente") && rb.getText().equalsIgnoreCase("Pendiente")) {
                tgEstadoCompra.selectToggle(toggle);
            } else if (estado.equalsIgnoreCase("Completada") && rb.getText().equalsIgnoreCase("Completada")) {
                tgEstadoCompra.selectToggle(toggle);
            } else if (estado.equalsIgnoreCase("Cancelada") && rb.getText().equalsIgnoreCase("Cancelada")) {
                tgEstadoCompra.selectToggle(toggle);
            }
        });
    }

    private void seleccionarRadioButtonEstadoPago(String estado) {
        tgEstadoPago.getToggles().forEach(toggle -> {
            RadioButton rb = (RadioButton) toggle;
            if (estado.equalsIgnoreCase("Pendiente") && rb.getText().equalsIgnoreCase("Pendiente")) {
                tgEstadoPago.selectToggle(toggle);
            } else if (estado.equalsIgnoreCase("Pagado") && rb.getText().equalsIgnoreCase("Pagado")) {
                tgEstadoPago.selectToggle(toggle);
            }
        });
    }

    public ArrayList<Compra> listarCompras() {
        ArrayList<Compra> compras = new ArrayList<>();
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            String sql = "{call sp_listarComprasView()}";
            CallableStatement stmt = conexion.prepareCall(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                compras.add(new Compra(
                        rs.getInt("COMPRA"),
                        rs.getDouble("TOTAL"),
                        rs.getString("ESTADO_COMPRA"),
                        rs.getString("ESTADO_PAGO"),
                        rs.getTimestamp("FECHA").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar compras: " + e.getMessage());
        }
        return compras;
    }

    private Compra getModeloCompra() {
        int idCompra = txtId.getText().isEmpty() ? 0 : Integer.parseInt(txtId.getText());
        String estadoCompra = ((RadioButton) tgEstadoCompra.getSelectedToggle()).getText();
        String estadoPago = ((RadioButton) tgEstadoPago.getSelectedToggle()).getText();
        double total = txtTotal.getText().isEmpty() ? 0 : Double.parseDouble(txtTotal.getText());

        return new Compra(idCompra, total, estadoCompra, estadoPago, null);
    }

    public void eliminarCompra() {
        modeloCompra = getModeloCompra();
        try {
            CallableStatement stmt = Conexion.getInstancia().getConexion()
                    .prepareCall("{call sp_EliminarCompras(?)}");
            stmt.setInt(1, modeloCompra.getIdCompra());
            stmt.execute();
            cargarDatos();
        } catch (SQLException e) {
            mostrarAlerta("Error al eliminar compra: " + e.getMessage());
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
        txtId.clear();
        txtTotal.clear();
        dpFecha.setValue(null);
        tgEstadoCompra.selectToggle(null);
        tgEstadoPago.selectToggle(null);
    }

    public void btnRegresarActionEvent(ActionEvent evento) {
        principal.getMenuAdminView();
    }

    private void deshabilitarControles() {
        txtId.setDisable(true);
        txtTotal.setDisable(true);
        dpFecha.setDisable(true);
        tgEstadoCompra.getToggles().forEach(t -> ((RadioButton) t).setDisable(true));
        tgEstadoPago.getToggles().forEach(t -> ((RadioButton) t).setDisable(true));
        tablaCompras.setDisable(false);
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
}
