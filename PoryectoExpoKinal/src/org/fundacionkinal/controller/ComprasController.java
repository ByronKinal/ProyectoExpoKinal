package org.fundacionkinal.controller;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import org.fundacionkinal.database.Conexion;
import org.fundacionkinal.model.Compra;
import org.fundacionkinal.model.Usuario;
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
    private TableColumn<Compra, String> colCliente;
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

    @FXML
    private ComboBox<Usuario> cbCliente;

    private ObservableList<Compra> listaCompras;
    private ObservableList<Usuario> listaClientes;

    private Main principal;
    private Compra modeloCompra;

    private enum Acciones {
        Agregar, Editar, Eliminar, Ninguna
    };
    private Acciones accion = Acciones.Ninguna;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setFormatoColumnaModelo();
        cargarClientes();
        cargarDatos();
        tablaCompras.setOnMouseClicked(event -> {
            getCompraTextFiel();
            deshabilitarControles();
        });
        deshabilitarControles();
    }

    private void cargarClientes() {
        listaClientes = FXCollections.observableArrayList();
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            String sql = "SELECT idUsuario, nombreUsuario FROM Usuarios WHERE tipo = 'Cliente'";
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                listaClientes.add(new Usuario(
                        rs.getInt("idUsuario"),
                        rs.getString("nombreUsuario"),
                        null, null, null
                ));
            }

            configurarComboBoxCliente();
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar clientes: " + e.getMessage());
        }
    }

    private void configurarComboBoxCliente() {
        cbCliente.setItems(listaClientes);

        cbCliente.setCellFactory(param -> new ListCell<Usuario>() {
            @Override
            protected void updateItem(Usuario item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getIdUsuario() + " | " + item.getNombreUsuario());
                }
            }
        });

        cbCliente.setButtonCell(new ListCell<Usuario>() {
            @Override
            protected void updateItem(Usuario item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombreUsuario());
                }
            }
        });
    }

    public void setFormatoColumnaModelo() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idCompra"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colEstadoCompra.setCellValueFactory(new PropertyValueFactory<>("estadoCompra"));
        colEstadoPago.setCellValueFactory(new PropertyValueFactory<>("estadoPago"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        colCliente.setCellValueFactory(cellData -> {
            Compra compra = cellData.getValue();

            if (compra == null) {
                return new SimpleStringProperty("");
            }

            Usuario cliente = listaClientes.stream()
                    .filter(p -> p.getIdUsuario() == compra.getIdCliente())
                    .findFirst()
                    .orElse(null);

            return new SimpleStringProperty(cliente != null ? cliente.getNombreUsuario() : "Sin cliente");
        });
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

            listaClientes.stream()
                    .filter(cliente -> cliente.getIdUsuario() == compraSeleccionada.getIdCliente())
                    .findFirst()
                    .ifPresent(cliente -> cbCliente.getSelectionModel().select(cliente));

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
            String sql = "{call sp_ListarCompras()}";
            CallableStatement stmt = conexion.prepareCall(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                compras.add(new Compra(
                        rs.getInt("COMPRA"),
                        rs.getInt("CLIENTE"),
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
        int idCliente = cbCliente.getSelectionModel().getSelectedItem().getIdUsuario();
        String estadoCompra = ((RadioButton) tgEstadoCompra.getSelectedToggle()).getText();
        String estadoPago = ((RadioButton) tgEstadoPago.getSelectedToggle()).getText();
        double total = txtTotal.getText().isEmpty() ? 0 : Double.parseDouble(txtTotal.getText());

        return new Compra(idCompra, idCliente, total, estadoCompra, estadoPago, null);
    }

    public void agregarCompra() {
        if (validarCampos()) {
            modeloCompra = getModeloCompra();
            try {
                CallableStatement stmt = Conexion.getInstancia().getConexion()
                        .prepareCall("{call sp_AgregarCompra(?, ?, ?)}");
                stmt.setInt(1, modeloCompra.getIdCliente());
                stmt.setString(2, modeloCompra.getEstadoCompra());
                stmt.setString(3, modeloCompra.getEstadoPago());
                stmt.execute();
                cargarDatos();
                cambiarOriginal();
            } catch (SQLException e) {
                mostrarAlerta("Error al agregar compra: " + e.getMessage());
            }
        }
    }

    public void editarCompra() {
        if (validarCampos()) {
            modeloCompra = getModeloCompra();
            try {
                CallableStatement stmt = Conexion.getInstancia().getConexion()
                        .prepareCall("{call sp_ActualizarCompras(?, ?, ?, ?)}");
                stmt.setInt(1, modeloCompra.getIdCompra());
                stmt.setInt(2, modeloCompra.getIdCliente());
                stmt.setString(3, modeloCompra.getEstadoCompra());
                stmt.setString(4, modeloCompra.getEstadoPago());
                stmt.execute();
                cargarDatos();
                cambiarOriginal();
            } catch (SQLException e) {
                mostrarAlerta("Error al editar compra: " + e.getMessage());
            }
        }
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

    private boolean validarCampos() {
        if (cbCliente.getSelectionModel().isEmpty()
                || tgEstadoCompra.getSelectedToggle() == null
                || tgEstadoPago.getSelectedToggle() == null) {

            mostrarAlerta("Todos los campos son obligatorios");
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
        txtTotal.clear();
        dpFecha.setValue(null);
        tgEstadoCompra.selectToggle(null);
        tgEstadoPago.selectToggle(null);
        cbCliente.getSelectionModel().clearSelection();
    }

    public void btnRegresarActionEvent(ActionEvent evento) {
        principal.getMenuAdminView();
    }

    @FXML
    private void cambiarNuevaCompra() {
        switch (accion) {
            case Ninguna:
                cambiarGuardarEditar();
                accion = Acciones.Agregar;
                limpiarTexto();
                habilitarControles(true);
                break;
            case Agregar:
                agregarCompra();
                break;
            case Editar:
                editarCompra();
                break;
        }
    }

    @FXML
    private void cambiarEdicionCompra() {
        cambiarGuardarEditar();
        accion = Acciones.Editar;
        habilitarControles(true);
    }

    @FXML
    private void cambiarCancelarEliminar() {
        if (accion == Acciones.Ninguna) {
            eliminarCompra();
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
        txtTotal.setDisable(true);
        dpFecha.setDisable(true);
        cbCliente.setDisable(true);
        tgEstadoCompra.getToggles().forEach(t -> ((RadioButton) t).setDisable(true));
        tgEstadoPago.getToggles().forEach(t -> ((RadioButton) t).setDisable(true));
        tablaCompras.setDisable(false);
    }

    private void habilitarControles(boolean habilitar) {
        txtId.setDisable(true);
        txtTotal.setDisable(true);
        dpFecha.setDisable(true);
        cbCliente.setDisable(!habilitar);
        tgEstadoCompra.getToggles().forEach(t -> ((RadioButton) t).setDisable(!habilitar));
        tgEstadoPago.getToggles().forEach(t -> ((RadioButton) t).setDisable(!habilitar));
        tablaCompras.setDisable(habilitar);
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
}
