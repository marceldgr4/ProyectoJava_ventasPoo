/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Invetario;

import venta.Atencion;
import venta.TipoAtencion;


public class Empenho extends Atencion{
    private String fechaDevolucion;
    private boolean vencido, devuelto;

    public Empenho(int id, TipoAtencion tipo, String cliente, String articulo, int cantidad,
            Double precio, String fecha, String fechaDevolucion, boolean vencido, boolean devuelto) {
        super(id, tipo, cliente, articulo, cantidad, precio, fecha);
        this.fechaDevolucion = fechaDevolucion;
        this.vencido = vencido;
        this.devuelto = devuelto;
    }

    public String getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(String fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public boolean isVencido() {
        return vencido;
    }

    public void setVencido(boolean vencido) {
        this.vencido = vencido;
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public void setDevuelto(boolean devuelto) {
        this.devuelto = devuelto;
    }
    
    
}
