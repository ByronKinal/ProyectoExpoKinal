
package org.fundacionkinal.system;

import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.fundacionkinal.controller.FacturaController;
import org.fundacionkinal.controller.LoginController;
import org.fundacionkinal.controller.MenuAdminController;

/**
 *
 * @author Wilson Florian
 */
public class Main extends Application{
    
    private Stage escenarioPrincipal;
    private Scene siguienteEscena;
    private static String URL = "/org/fundacionkinal/view/";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage escenarioPrincipal) throws Exception {
        this.escenarioPrincipal = escenarioPrincipal;
        escenarioPrincipal.setTitle("SISTEMA DE CAJERO");
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
                    = (LoginController) cambiarEscena("LoginView.fxml", 757, 500);
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
                    = (FacturaController) cambiarEscena("MenuAdminView.fxml", 1069, 700);
            control.setPrincipal(this);
        } catch (Exception ex) {
            System.out.println("Error al ir a menuAdmin: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
