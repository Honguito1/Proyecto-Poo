package com.SistemaPedidos.pedidos.ClasesUML.Productos;

public class Bebida extends Producto {
    private String tipo;

    public Bebida(){}
    public Bebida(String nombre, double precio, String tipo) {
        super(nombre, precio);
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

}
