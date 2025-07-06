
package org.fundacionkinal.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Wilson Florian
 */
public class Conexion {
    private static Conexion instancia;
    private Connection conexion;
    private String url = "jdbc:mysql://127.0.0.1:3306/cajeroDB2?useSSL=false";
    private String user = "quintov";
    private String password = "admin";
    private String driver = "com.mysql.jdbc.Driver";

    private Conexion() {
        conectar();
    }

    public void conectar() {
        try {
            Class.forName(driver);
            conexion = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }
    
    public Connection getConexion() {
        try {
            if (conexion != null || conexion.isClosed()) {
                conectar();
            }
        } catch (SQLException ex) {
            System.out.println("Error al reconectar: " + ex.getSQLState());
            ex.printStackTrace();
        }
        return conexion;
    }    
        
}
