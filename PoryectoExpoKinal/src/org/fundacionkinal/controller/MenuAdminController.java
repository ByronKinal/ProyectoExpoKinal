
package org.fundacionkinal.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import org.fundacionkinal.system.Main;

/**
 * FXML Controller class
 *
 * @author Wilson Florian
 */
public class MenuAdminController implements Initializable {

    private Main principal;
    @FXML private MenuItem itmCerrar;
    @FXML private MenuItem itmAcercaDe;
    @FXML private Button btnEmpleados;
    @FXML private Button btnCompras;
    @FXML private Button btnProductos;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    public void clickManejadorEventos(ActionEvent evento) {
        if (evento.getSource() == itmCerrar) {
            principal.getLoginView();
            
        }else if (evento.getSource() == itmAcercaDe) {
            //principal.getCaratulaView();
            
        }else if (evento.getSource() == btnEmpleados) {
            //principal.getEmpleadosView();
            
        }else if (evento.getSource() == btnCompras) {
            //principal.getComprasView();
            
        }else if (evento.getSource() == btnProductos) {
            //principal.getProductosView();
        }   
    }   
}
