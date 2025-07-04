
package org.fundacionkinal.controller;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.fundacionkinal.database.Conexion;
import org.fundacionkinal.model.Usuario;
import org.fundacionkinal.system.Main;

/**
 * FXML Controller class
 *
 * @author Wilson Florian
 */
public class Factura2Controller implements Initializable {

    @FXML
    private Button btnRegresar, btnPagar, btnEnviar, btnCancelar;
    @FXML
    private TextField txtCliente, txtSubtotal, txtTotal;
    @FXML
    private ComboBox<Usuario> cbEmpleado;
    @FXML
    private RadioButton rbEfectivo, rbTarjeta;
    @FXML
    private ToggleGroup tgMetodoPago;

    private Main principal;
    private int idCompraActual;
    private double subtotal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        deshabilitarCampos();
        cargarEmpleados();
    }

    public void initData(int idCompra, double subtotal) {
        this.idCompraActual = idCompra;
        this.subtotal = subtotal;
        mostrarDatos();
    }

    private void deshabilitarCampos() {
        txtSubtotal.setDisable(true);
        txtTotal.setDisable(true);
    }

    private void cargarEmpleados() {
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            String sql = "SELECT idUsuario, nombreUsuario FROM Usuarios WHERE tipo = 'Empleado'";
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            ObservableList<Usuario> empleados = FXCollections.observableArrayList();
            while (rs.next()) {
                Usuario empleado = new Usuario();
                empleado.setIdUsuario(rs.getInt("idUsuario"));
                empleado.setNombreUsuario(rs.getString("nombreUsuario"));
                empleados.add(empleado);
            }

            cbEmpleado.setItems(empleados);
            cbEmpleado.setCellFactory(param -> new ListCell<Usuario>() {
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

            cbEmpleado.setButtonCell(new ListCell<Usuario>() {
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

            if (!empleados.isEmpty()) {
                cbEmpleado.getSelectionModel().selectFirst();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error al cargar empleados: " + e.getMessage());
        }
    }

    private void mostrarDatos() {
        txtSubtotal.setText(String.format("%.2f", subtotal));
        double total = subtotal * 1.12;
        txtTotal.setText(String.format("%.2f", total));
        
    }

    @FXML
    private void pagar() {
        if (validarCampos()) {
            try {
                Connection conexion = Conexion.getInstancia().getConexion();

                String metodoPago = rbEfectivo.isSelected() ? "Efectivo" : "Tarjeta";
                double total = Double.parseDouble(txtTotal.getText());

                CallableStatement procedimientoPago = conexion.prepareCall("{call sp_AgregarPagos(?, ?, ?, ?)}");
                procedimientoPago.setString(1, metodoPago);
                procedimientoPago.setDouble(2, total);
                procedimientoPago.setInt(3, idCompraActual);
                procedimientoPago.registerOutParameter(4, Types.INTEGER);
                procedimientoPago.execute();

                int idPago = procedimientoPago.getInt(4);

                Usuario empleado = cbEmpleado.getSelectionModel().getSelectedItem();
                String nombreCliente = txtCliente.getText();

                if (!nombreCliente.isEmpty()) {
                    CallableStatement procedimientoCliente = conexion.prepareCall("{call sp_AgregarCliente(?, ?, ?, ?)}");
                    procedimientoCliente.setString(1, nombreCliente);
                    procedimientoCliente.setString(2, "");
                    procedimientoCliente.setInt(3, idCompraActual);
                    procedimientoCliente.setInt(4, 0);
                    procedimientoCliente.execute();
                }

                CallableStatement procedimientoFactura = conexion.prepareCall("{call sp_AgregarFactura(?, ?, ?, ?)}");
                procedimientoFactura.setString(1, metodoPago);
                procedimientoFactura.setInt(2, empleado.getIdUsuario());
                procedimientoFactura.setInt(3, idCompraActual);
                procedimientoFactura.setInt(4, idPago);
                procedimientoFactura.execute();

                Statement stmtUpdate = conexion.createStatement();
                stmtUpdate.executeUpdate("UPDATE Compras SET estadoCompra = 'Completada', estadoPago = 'Pagado' WHERE idCompra = " + idCompraActual);

                mostrarMensaje("","Pago registrado y factura generada exitosamente");
                principal.getFacturaView();

            } catch (SQLException e) {
                mostrarAlerta("Error al registrar pago: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void cancelarPedido() {
        try {
            Connection conexion = Conexion.getInstancia().getConexion();
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate("UPDATE Compras SET estadoCompra = 'Cancelada' WHERE idCompra = " + idCompraActual);

            principal.getFacturaView();
        } catch (SQLException e) {
            mostrarAlerta("Error al cancelar pedido: " + e.getMessage());
        }
    }

    @FXML
    private void btnRegresarActionEvent() {
        principal.getFacturaView();
    }

    private boolean validarCampos() {
        if (cbEmpleado.getSelectionModel().isEmpty()) {
            mostrarAlerta("Seleccione un empleado");
            return false;
        }

        if (!rbEfectivo.isSelected() && !rbTarjeta.isSelected()) {
            mostrarAlerta("Seleccione un método de pago");
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
    
    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
}
