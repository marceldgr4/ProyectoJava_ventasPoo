
package Invetario;


public class Articulo {
    private int id, cantidad;
    private String nombre;
    private double precio;
    private boolean vendible;

    public Articulo(int id,  String nombre, int cantidad, double precio, boolean vendible) {
        this.id = id;
        this.cantidad = cantidad;
        this.nombre = nombre;
        this.precio = precio;
        this.vendible = vendible;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public boolean isVendible() {
        return vendible;
    }

    public void setVendible(boolean vendible) {
        this.vendible = vendible;
    }
    
    
}
