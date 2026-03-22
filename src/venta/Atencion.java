
package venta;

import venta.TipoAtencion;
import java.text.DateFormat;
import java.util.Date;


public class Atencion {
    protected int id, cantidad;
    protected String cliente, articulo, fecha;
    protected Double precio;
    protected TipoAtencion tipo;

    public Atencion(int id, TipoAtencion tipo, String cliente, String articulo, int cantidad, Double precio,  String fecha) {
        this.id = id;
        this.cliente = cliente;
        this.articulo = articulo;
        this.precio = precio;
        this.tipo = tipo;
        this.fecha = fecha;
        this.cantidad = cantidad;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }       

    public String getArticulo() {
        return articulo;
    }

    public void setArticulo(String articulo) {
        this.articulo = articulo;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public TipoAtencion getTipo() {
        return tipo;
    }

    public void setTipo(TipoAtencion tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getFecha() {
        return fecha;
    }
    
    
}
