
package org.fundacionkinal.model;

import java.time.LocalDateTime;

/**
 *
 * @author Wilson Florian
 */
public class Compra {
    
    private int idCompra;
    private int idCliente;
    private double total;
    private String estadoCompra;
    private String estadoPago;
    private LocalDateTime fecha;

    public Compra() {
    }

    public Compra(int idCompra, int idCliente, double total, String estadoCompra, String estadoPago, LocalDateTime fecha) {
        this.idCompra = idCompra;
        this.idCliente = idCliente;
        this.total = total;
        this.estadoCompra = estadoCompra;
        this.estadoPago = estadoPago;
        this.fecha = fecha;
    }

    public Compra(int idCompra, int idCliente, String estadoCompra, String estadoPago, LocalDateTime fecha) {
        this.idCompra = idCompra;
        this.idCliente = idCliente;
        this.estadoCompra = estadoCompra;
        this.estadoPago = estadoPago;
        this.fecha = fecha;
    }

    public Compra(int idCliente, String estadoCompra, String estadoPago) {
        this.idCliente = idCliente;
        this.estadoCompra = estadoCompra;
        this.estadoPago = estadoPago;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getEstadoCompra() {
        return estadoCompra;
    }

    public void setEstadoCompra(String estadoCompra) {
        this.estadoCompra = estadoCompra;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
