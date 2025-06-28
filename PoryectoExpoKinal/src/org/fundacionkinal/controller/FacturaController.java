package org.fundacionkinal.controller;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.fundacionkinal.system.Main;

/**
 * FXML Controller class
 *
 * @author Wilson Florian
 */
public class FacturaController implements Initializable {

    @FXML
    private Button btnRegresar, btnAgregar, btnEditar, btnEliminar, btnReporte;

    @FXML
    private TableView<Factura> tablaFacturas;

    @FXML
    private TableColumn<Factura, Integer> colId;
    @FXML
    private TableColumn<Factura, LocalDate> colFecha;
    @FXML
    private TableColumn<Factura, Double> colTotal;
    @FXML
    private TableColumn<Factura, String> colMetodoPago;
    @FXML
    private TableColumn<Factura, Integer> colCliente;
    @FXML
    private TableColumn<Factura, Integer> colEmpleado;
    @FXML
    private TableColumn<Factura, Integer> colCompra;
    @FXML
    private TableColumn<Factura, Integer> colPago;

    @FXML
    private TextField txtId, txtTotal, txtCantidad, txtCliente, txtEmpleado, txtCompra, txtPago;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private ComboBox<String> cbCliente, cbEmpleado, cbCompra, cbPago;
    @FXML
    private ToggleGroup tgMetodoPago;

    private ObservableList<Factura> listaFacturas;
    private Main principal;
    private Factura modeloFactura;

    private enum Acciones {
        Agregar, Editar, Eliminar, Ninguna
    };
    private Acciones accion = Acciones.Ninguna;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtCantidad.setDisable(true);
        setFormatoColumnaModelo();
        cargarDatos();
        tablaFacturas.setOnMouseClicked(event -> {
            getFacturaTextFiel();
            deshabilitarControles();
        });
        deshabilitarControles();
    }

    public void setFormatoColumnaModelo() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idFactura"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colMetodoPago.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colEmpleado.setCellValueFactory(new PropertyValueFactory<>("idEmpleado"));
        colCompra.setCellValueFactory(new PropertyValueFactory<>("idCompra"));
        colPago.setCellValueFactory(new PropertyValueFactory<>("idPago"));
    }

    public void cargarDatos() {
        listaFacturas = FXCollections.observableArrayList(listarFacturas());
        tablaFacturas.setItems(listaFacturas);
        if (!listaFacturas.isEmpty()) {
            tablaFacturas.getSelectionModel().selectFirst();
            getFacturaTextFiel();
        }
    }

    public void getFacturaTextFiel() {
        Factura facturaSeleccionada = tablaFacturas.getSelectionModel().getSelectedItem();
        if (facturaSeleccionada != null) {
            txtId.setText(String.valueOf(facturaSeleccionada.getIdFactura()));
            dpFecha.setValue(facturaSeleccionada.getFecha().toLocalDate());
            txtTotal.setText(String.valueOf(facturaSeleccionada.getTotal()));
            seleccionarMetodoPago(facturaSeleccionada.getMetodoPago());
            txtCliente.setText(String.valueOf(facturaSeleccionada.getIdCliente()));
            txtEmpleado.setText(String.valueOf(facturaSeleccionada.getIdEmpleado()));
            txtCompra.setText(String.valueOf(facturaSeleccionada.getIdCompra()));
            txtPago.setText(String.valueOf(facturaSeleccionada.getIdPago()));
        }
    }

    private void seleccionarMetodoPago(String metodo) {
        tgMetodoPago.getToggles().forEach(toggle -> {
            RadioButton rb = (RadioButton) toggle;
            if (metodo.equalsIgnoreCase("Tarjeta") && rb.getText().equalsIgnoreCase("TARJETA")) {
                tgMetodoPago.selectToggle(toggle);
            } else if (metodo.equalsIgnoreCase("Efectivo") && rb.getText().equalsIgnoreCase("EFECTIVO")) {
                tgMetodoPago.selectToggle(toggle);
            }
        });
    }

    public ArrayList<Factura> listarFacturas() {
        ArrayList<Factura> facturas = new ArrayList<>();
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            String sql = "SELECT idFactura, fecha, total, metodoPago, idCliente, idEmpleado, idCompra, idPago FROM Facturas";
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                // Convertir Timestamp a LocalDateTime
                Timestamp timestamp = rs.getTimestamp("fecha");
                LocalDateTime fecha = timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now();

                facturas.add(new Factura(
                        rs.getInt("idFactura"),
                        fecha,
                        rs.getDouble("total"),
                        rs.getString("metodoPago"),
                        rs.getInt("idCliente"),
                        rs.getInt("idEmpleado"),
                        rs.getInt("idCompra"),
                        rs.getInt("idPago")
                ));
            }
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar facturas: " + e.getMessage());
        }
        return facturas;
    }

    private Factura getModeloFactura() {
        int idFactura = txtId.getText().isEmpty() ? 0 : Integer.parseInt(txtId.getText());

        LocalDateTime fecha = dpFecha.getValue() != null
                ? dpFecha.getValue().atStartOfDay()
                : LocalDateTime.now();

        double total = txtTotal.getText().isEmpty() ? 0 : Double.parseDouble(txtTotal.getText());
        String metodoPago = ((RadioButton) tgMetodoPago.getSelectedToggle()).getText();
        int idCliente = txtCliente.getText().isEmpty() ? 0 : Integer.parseInt(txtCliente.getText());
        int idEmpleado = txtEmpleado.getText().isEmpty() ? 0 : Integer.parseInt(txtEmpleado.getText());
        int idCompra = txtCompra.getText().isEmpty() ? 0 : Integer.parseInt(txtCompra.getText());
        int idPago = txtPago.getText().isEmpty() ? 0 : Integer.parseInt(txtPago.getText());

        return new Factura(idFactura, fecha, total, metodoPago, idCliente, idEmpleado, idCompra, idPago);
    }

    public void agregarFactura() {
        if (validarCampos()) {
            modeloFactura = getModeloFactura();
            try {
                CallableStatement stmt = Conexion.getInstancia().getConexion()
                        .prepareCall("{call sp_AgregarFactura(?, ?, ?, ?, ?, ?)}");
                stmt.setDouble(1, modeloFactura.getTotal());
                stmt.setString(2, modeloFactura.getMetodoPago());
                stmt.setInt(3, modeloFactura.getIdCliente());
                stmt.setInt(4, modeloFactura.getIdEmpleado());
                stmt.setInt(5, modeloFactura.getIdCompra());
                stmt.setInt(6, modeloFactura.getIdPago());
                stmt.execute();
                cargarDatos();
                cambiarOriginal();
            } catch (SQLException e) {
                mostrarAlerta("Error al agregar factura: " + e.getMessage());
            }
        }
    }

    public void editarFactura() {
        if (validarCampos()) {
            modeloFactura = getModeloFactura();
            try {
                CallableStatement stmt = Conexion.getInstancia().getConexion()
                        .prepareCall("{call sp_ActualizarFactura(?, ?, ?, ?, ?, ?, ?)}");
                stmt.setInt(1, modeloFactura.getIdFactura());
                stmt.setDouble(2, modeloFactura.getTotal());
                stmt.setString(3, modeloFactura.getMetodoPago());
                stmt.setInt(4, modeloFactura.getIdCliente());
                stmt.setInt(5, modeloFactura.getIdEmpleado());
                stmt.setInt(6, modeloFactura.getIdCompra());
                stmt.setInt(7, modeloFactura.getIdPago());
                stmt.execute();
                cargarDatos();
                cambiarOriginal();
            } catch (SQLException e) {
                mostrarAlerta("Error al editar factura: " + e.getMessage());
            }
        }
    }

    public void eliminarFactura() {
        modeloFactura = getModeloFactura();
        try {
            CallableStatement stmt = Conexion.getInstancia().getConexion()
                    .prepareCall("{call sp_EliminarFacturas(?)}");
            stmt.setInt(1, modeloFactura.getIdFactura());
            stmt.execute();
            cargarDatos();
        } catch (SQLException e) {
            mostrarAlerta("Error al eliminar factura: " + e.getMessage());
        }
    }

    private boolean validarCampos() {
        if (dpFecha.getValue() == null
                || txtTotal.getText().isEmpty()
                || tgMetodoPago.getSelectedToggle() == null
                || txtCliente.getText().isEmpty()
                || txtEmpleado.getText().isEmpty()
                || txtCompra.getText().isEmpty()
                || txtPago.getText().isEmpty()) {

            mostrarAlerta("Todos los campos son obligatorios");
            return false;
        }

        try {
            Double.parseDouble(txtTotal.getText());
            Integer.parseInt(txtCliente.getText());
            Integer.parseInt(txtEmpleado.getText());
            Integer.parseInt(txtCompra.getText());
            Integer.parseInt(txtPago.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Total, Cliente, Empleado, Compra y Pago deben ser valores numéricos");
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
        dpFecha.setValue(null);
        txtTotal.clear();
        tgMetodoPago.selectToggle(null);
        txtCliente.clear();
        txtEmpleado.clear();
        txtCompra.clear();
        txtPago.clear();
    }

    public void btnRegresarActionEvent(ActionEvent evento) {
        principal.getLoginView();
    }

    @FXML
    private void cambiarNuevoFactura() {
        switch (accion) {
            case Ninguna:
                cambiarGuardarEditar();
                accion = Acciones.Agregar;
                limpiarTexto();
                habilitarControles(true);
                break;
            case Agregar:
                agregarFactura();
                break;
            case Editar:
                editarFactura();
                break;
        }
    }

    @FXML
    private void cambiarEdicionFactura() {
        cambiarGuardarEditar();
        accion = Acciones.Editar;
        habilitarControles(true);
    }

    @FXML
    private void cambiarCancelarEliminar() {
        if (accion == Acciones.Ninguna) {
            eliminarFactura();
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
        dpFecha.setDisable(true);
        txtTotal.setDisable(true);
        tgMetodoPago.getToggles().forEach(t -> ((RadioButton) t).setDisable(true));
        txtCliente.setDisable(true);
        txtEmpleado.setDisable(true);
        txtCompra.setDisable(true);
        txtPago.setDisable(true);
        tablaFacturas.setDisable(false);
    }

    private void habilitarControles(boolean habilitar) {
        txtId.setDisable(true); // ID siempre deshabilitado
        dpFecha.setDisable(!habilitar);
        txtTotal.setDisable(!habilitar);
        tgMetodoPago.getToggles().forEach(t -> ((RadioButton) t).setDisable(!habilitar));
        txtCliente.setDisable(!habilitar);
        txtEmpleado.setDisable(!habilitar);
        txtCompra.setDisable(!habilitar);
        txtPago.setDisable(!habilitar);
        tablaFacturas.setDisable(habilitar);
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
}
