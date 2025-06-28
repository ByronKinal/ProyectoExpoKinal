

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
import org.fundacionkinal.model.Empleado;
import org.fundacionkinal.system.Main;

/**
 * FXML Controller class
 *
 * @author Wilson Florian
 */
public class EmpleadoController implements Initializable {

    @FXML private Button btnRegresar, btnAgregar, btnEditar, btnEliminar, btnReporte;
    
    @FXML private TableView<Empleado> tablaEmpleados;
    
    @FXML private TableColumn<Empleado, Integer> colId;
    @FXML private TableColumn<Empleado, String> colNombre;
    @FXML private TableColumn<Empleado, String> colCorreo;
    @FXML private TableColumn<Empleado, String> colContraseña;
    @FXML private TableColumn<Empleado, String> colTipo;
    
    @FXML private TextField txtId, txtNombre, txtCorreo, txtContraseña;
    
    @FXML private ToggleGroup tgTipo;
    
    private ObservableList<Empleado> listaEmpleados;
    private Main principal;
    private Empleado modeloEmpleado;

    private enum Acciones {
        Agregar, Editar, Eliminar, Ninguna
    };
    private Acciones accion = Acciones.Ninguna;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setFormatoColumnaModelo();
        cargarDatos();
        tablaEmpleados.setOnMouseClicked(event -> {
            getEmpleadoTextFiel();
            deshabilitarControles();
        });
        deshabilitarControles();
    }
    
    public void setFormatoColumnaModelo() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correoUsuario"));
        colContraseña.setCellValueFactory(new PropertyValueFactory<>("contraseñaUsuario"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
    }
    
    public void cargarDatos() {
        listaEmpleados = FXCollections.observableArrayList(listarEmpleados());
        tablaEmpleados.setItems(listaEmpleados);
        if (!listaEmpleados.isEmpty()) {
            tablaEmpleados.getSelectionModel().selectFirst();
            getEmpleadoTextFiel();
        }
    }

    public void getEmpleadoTextFiel() {
        Empleado empleadoSeleccionado = tablaEmpleados.getSelectionModel().getSelectedItem();
        if (empleadoSeleccionado != null) {
            txtId.setText(String.valueOf(empleadoSeleccionado.getIdUsuario()));
            txtNombre.setText(empleadoSeleccionado.getNombreUsuario());
            txtCorreo.setText(empleadoSeleccionado.getCorreoUsuario());
            txtContraseña.setText(empleadoSeleccionado.getContraseñaUsuario());
            seleccionarRadioButtonTipo(empleadoSeleccionado.getTipo());
        }
    }
    
    private void seleccionarRadioButtonTipo(String tipo) {
        tgTipo.getToggles().forEach(toggle -> {
            RadioButton rb = (RadioButton) toggle;
            if (tipo.equalsIgnoreCase("Admin") && rb.getText().equalsIgnoreCase("ADMIN")) {
                tgTipo.selectToggle(toggle);
            } else if (tipo.equalsIgnoreCase("Empleado") && rb.getText().equalsIgnoreCase("EMPLEADO")) {
                tgTipo.selectToggle(toggle);
            }
        });
    }

    public ArrayList<Empleado> listarEmpleados() {
    ArrayList<Empleado> empleados = new ArrayList<>();
    try {
        Connection conexion = Conexion.getInstancia().getConexion();
        String sql = "SELECT idUsuario, nombreUsuario, correoUsuario, contraseñaUsuario, tipo " +
                     "FROM Usuarios WHERE tipo = 'Empleado'";
        Statement stmt = conexion.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            empleados.add(new Empleado(
                    rs.getInt("idUsuario"),
                    rs.getString("nombreUsuario"),
                    rs.getString("correoUsuario"),
                    rs.getString("contraseñaUsuario"),
                    rs.getString("tipo")
            ));
        }
    } catch (SQLException e) {
        mostrarAlerta("Error al cargar empleados: " + e.getMessage());
    }
    return empleados;
}
    
    private Empleado getModeloEmpleado() {
        int idUsuario = txtId.getText().isEmpty() ? 0 : Integer.parseInt(txtId.getText());
        String nombreUsuario = txtNombre.getText();
        String correoUsuario = txtCorreo.getText();
        String contraseñaUsuario = txtContraseña.getText();
        String tipo = ((RadioButton) tgTipo.getSelectedToggle()).getText();

        return new Empleado(idUsuario, nombreUsuario, correoUsuario, contraseñaUsuario, tipo);
    }
    
    public void agregarEmpleado() {
        if (validarCampos()) {
            modeloEmpleado = getModeloEmpleado();
            try {
                CallableStatement stmt = Conexion.getInstancia().getConexion()
                        .prepareCall("{call sp_AgregarUsuario(?, ?, ?, ?)}");
                stmt.setString(1, modeloEmpleado.getNombreUsuario());
                stmt.setString(2, modeloEmpleado.getCorreoUsuario());
                stmt.setString(3, modeloEmpleado.getContraseñaUsuario());
                stmt.setString(4, modeloEmpleado.getTipo());
                stmt.execute();
                cargarDatos();
                cambiarOriginal();
            } catch (SQLException e) {
                mostrarAlerta("Error al agregar empleado: " + e.getMessage());
            }
        }
    }

    public void editarEmpleado() {
        if (validarCampos()) {
            modeloEmpleado = getModeloEmpleado();
            try {
                CallableStatement stmt = Conexion.getInstancia().getConexion()
                        .prepareCall("{call sp_ActualizarUsuario(?, ?, ?, ?, ?)}");
                stmt.setInt(1, modeloEmpleado.getIdUsuario());
                stmt.setString(2, modeloEmpleado.getNombreUsuario());
                stmt.setString(3, modeloEmpleado.getCorreoUsuario());
                stmt.setString(4, modeloEmpleado.getContraseñaUsuario());
                stmt.setString(5, modeloEmpleado.getTipo());
                stmt.execute();
                cargarDatos();
                cambiarOriginal();
            } catch (SQLException e) {
                mostrarAlerta("Error al editar empleado: " + e.getMessage());
            }
        }
    }

    public void eliminarEmpleado() {
        modeloEmpleado = getModeloEmpleado();
        try {
            CallableStatement stmt = Conexion.getInstancia().getConexion()
                    .prepareCall("{call sp_EliminarUsuario(?)}");
            stmt.setInt(1, modeloEmpleado.getIdUsuario());
            stmt.execute();
            cargarDatos();
        } catch (SQLException e) {
            mostrarAlerta("Error al eliminar empleado: " + e.getMessage());
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().isEmpty() || 
            txtCorreo.getText().isEmpty() || 
            txtContraseña.getText().isEmpty() || 
            tgTipo.getSelectedToggle() == null) {
            
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
        txtNombre.clear();
        txtCorreo.clear();
        txtContraseña.clear();
        tgTipo.selectToggle(null);
    }

    public void btnRegresarActionEvent(ActionEvent evento) {
        principal.getMenuAdminView();
    }

    @FXML
    private void cambiarNuevoEmpleado() {
        switch (accion) {
            case Ninguna:
                cambiarGuardarEditar();
                accion = Acciones.Agregar;
                limpiarTexto();
                habilitarControles(true);
                break;
            case Agregar:
                agregarEmpleado();
                break;
            case Editar:
                editarEmpleado();
                break;
        }
    }

    @FXML
    private void cambiarEdicionEmpleado() {
        cambiarGuardarEditar();
        accion = Acciones.Editar;
        habilitarControles(true);
    }

    @FXML
    private void cambiarCancelarEliminar() {
        if (accion == Acciones.Ninguna) {
            eliminarEmpleado();
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
        txtCorreo.setDisable(true);
        txtContraseña.setDisable(true);
        tgTipo.getToggles().forEach(t -> ((RadioButton) t).setDisable(true));
        tablaEmpleados.setDisable(false);
    }

    private void habilitarControles(boolean habilitar) {
        txtId.setDisable(true); // ID siempre deshabilitado
        txtNombre.setDisable(!habilitar);
        txtCorreo.setDisable(!habilitar);
        txtContraseña.setDisable(!habilitar);
        tgTipo.getToggles().forEach(t -> ((RadioButton) t).setDisable(!habilitar));
        tablaEmpleados.setDisable(habilitar);
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
}