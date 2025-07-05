
package org.fundacionkinal.report;

import java.io.InputStream;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import java.sql.Connection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.util.JRLoader;

import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author informatica
 */
public class Report {
    // reporte, visualizador, impresor
    private static JasperReport jreport;
    private static JasperViewer jviewer;
    private static JasperPrint jprint;
    
    //generarRporte
    public static void generarReporte(Connection conexion, Map<String, Object> parametros, InputStream reporte){
        try {
            //cargar el reporte (jasper)
            jreport = (JasperReport) JRLoader.loadObject(reporte);
            //rellenar (no) es necesario
            jprint = JasperFillManager.fillReport(jreport, parametros,conexion);
            
        } catch (JRException ex) {
            System.out.println("Error al generar un reporte: "+ex.getMessage());
            ex.printStackTrace();
        }
    }
    //mostrarReporte
    public static void mostrarReporte(){
        //configurar el visualizador (visor)
        jviewer = new JasperViewer(jprint, false);
        //hacerlo visible
        jviewer.setVisible(true);
    } 
    
}