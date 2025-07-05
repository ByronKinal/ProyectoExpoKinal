package org.fundacionkinal.system;

import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.fundacionkinal.controller.ComprasController;
import org.fundacionkinal.controller.EmpleadoController;
import org.fundacionkinal.controller.Factura2Controller;
import org.fundacionkinal.controller.FacturaController;
import org.fundacionkinal.controller.LoginController;
import org.fundacionkinal.controller.MenuAdminController;
import org.fundacionkinal.controller.ProductosController;

/**
 *
 * @author Wilson Florian
 */
public class Main extends Application {

    private Stage escenarioPrincipal;
    private Scene siguienteEscena;
    private static String URL = "/org/fundacionkinal/view/";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage escenarioPrincipal) throws Exception {
        this.escenarioPrincipal = escenarioPrincipal;
        escenarioPrincipal.setTitle("CAJA KINAL");
        Image icono = new javafx.scene.image.Image("/org/fundacionkinal/image/Logo.png");
        escenarioPrincipal.getIcons().add(icono);
        getLoginView();
        escenarioPrincipal.show();
    }

    public Initializable cambiarEscena(String fxml, double ancho, double alto) throws Exception {
        Initializable interfazDeCambio = null;
        FXMLLoader cargadorFXML = new FXMLLoader();
        InputStream archivoFXML = Main.class.getResourceAsStream(URL + fxml);

        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Main.class.getResource(URL + fxml));

        siguienteEscena = new Scene(cargadorFXML.load(archivoFXML), ancho, alto);
        escenarioPrincipal.setScene(siguienteEscena);
        escenarioPrincipal.sizeToScene();

        interfazDeCambio = cargadorFXML.getController();
        return interfazDeCambio;
    }

    public void getLoginView() {
        try {
            LoginController control
                    = (LoginController) cambiarEscena("LoginView.fxml", 1500, 800);
            control.setPrincipal(this);
        } catch (Exception ex) {
            System.out.println("Error al ir al login: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void getMenuAdminView() {
        try {
            MenuAdminController control
                    = (MenuAdminController) cambiarEscena("MenuAdminView.fxml", 1282.4, 585.6);
            control.setPrincipal(this);
        } catch (Exception ex) {
            System.out.println("Error al ir a menuAdmin: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void getFacturaView() {
        try {
            FacturaController control
                    = (FacturaController) cambiarEscena("FacturaView.fxml", 1920, 1080);
            control.setPrincipal(this);
        } catch (Exception ex) {
            System.out.println("Error al ir a facturas: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void getFactura2View(int idCompra, double subtotal) {
        try {
            FXMLLoader cargadorFXML = new FXMLLoader();
            InputStream archivoFXML = Main.class.getResourceAsStream(URL + "Factura2View.fxml");

            cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
            cargadorFXML.setLocation(Main.class.getResource(URL + "Factura2View.fxml"));

            siguienteEscena = new Scene(cargadorFXML.load(archivoFXML), 1920, 1080);
            escenarioPrincipal.setScene(siguienteEscena);
            escenarioPrincipal.sizeToScene();

            Factura2Controller control = cargadorFXML.getController();
            control.setPrincipal(this);
            control.initData(idCompra, subtotal); // Pasar los datos a Factura2Controller
        } catch (Exception ex) {
            System.out.println("Error al ir a factura2: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public void getFactura2View() {
    try {
        Factura2Controller control
                = (Factura2Controller) cambiarEscena("Factura2View.fxml", 1920, 1000);
        control.setPrincipal(this);
    } catch (Exception ex) {
        System.out.println("Error al ir a facturas: " + ex.getMessage());
        ex.printStackTrace();
    }
}

    public void getComprasView() {
        try {
            ComprasController control
                    = (ComprasController) cambiarEscena("ComprasView.fxml", 1000, 700);
            control.setPrincipal(this);
        } catch (Exception ex) {
            System.out.println("Error al ir a compras: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void getEmpleadosView() {
        try {
            EmpleadoController control
                    = (EmpleadoController) cambiarEscena("EmpleadoView.fxml", 1000, 700);
            control.setPrincipal(this);
        } catch (Exception ex) {
            System.out.println("Error al ir a empleados: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void getProductosView() {
        try {
            ProductosController control
                    = (ProductosController) cambiarEscena("ProductosView.fxml", 1000, 700);
            control.setPrincipal(this);
        } catch (Exception ex) {
            System.out.println("Error al ir a productos: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
