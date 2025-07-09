package com.SistemaPedidos.pedidos.ClasesUML.Productos;


public class Comida extends Producto {
    private int cantidad;

    public Comida(){};
    public Comida(String nombre, double precio, int cantidad) {
        super(nombre, precio);
        this.cantidad = cantidad;
    }

    public int getCantidad() {
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public double calcularCosto() {
        return this.getPrecio() * cantidad;
    }
}
