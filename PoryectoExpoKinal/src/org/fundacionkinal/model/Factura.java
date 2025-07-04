
package org.fundacionkinal.model;

import java.time.LocalDateTime;

/**
 *
 * @author Wilson Florian
 */
public class Factura {
    
    private int idFactura;
    private LocalDateTime fecha;
    private double total;
    private String metodoPago;
    private int idCliente;
    private int idEmpleado;
    private int idCompra;
    private int idPago;

    public Factura() {
    }

    public Factura(int idFactura, LocalDateTime fecha, double total, String metodoPago, int idCliente, int idEmpleado, int idCompra, int idPago) {
        this.idFactura = idFactura;
        this.fecha = fecha;
        this.total = total;
        this.metodoPago = metodoPago;
        this.idCliente = idCliente;
        this.idEmpleado = idEmpleado;
        this.idCompra = idCompra;
        this.idPago = idPago;
    }

    public Factura(String metodoPago, int idCliente, int idEmpleado, int idCompra, int idPago) {
        this.metodoPago = metodoPago;
        this.idCliente = idCliente;
        this.idEmpleado = idEmpleado;
        this.idCompra = idCompra;
        this.idPago = idPago;
    }

    public Factura(int idFactura, LocalDateTime fecha, double total, String metodoPago, int idCompra) {
        this.idFactura = idFactura;
        this.fecha = fecha;
        this.total = total;
        this.metodoPago = metodoPago;
        this.idCompra = idCompra;
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public int getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }
}
