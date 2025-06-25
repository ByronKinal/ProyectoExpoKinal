
package org.fundacionkinal.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.fundacionkinal.system.Main;

/**
 * FXML Controller class
 *
 * @author Wilson Florian
 */
public class FacturaController implements Initializable {

    private Main principal;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }  
    
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
}
