package com.SistemaPedidos.pedidos.ClasesUML.Productos;

import com.SistemaPedidos.pedidos.ClasesUML.Calculable;

public abstract class Producto implements Calculable {
    private String nombre;
    private double precio;
    private String firebaseKey;

    public Producto(){}
    public Producto(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
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

    public String getFirebaseKey() {
        return firebaseKey;
    }
    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    @Override
    public double calcularCosto() {
        return this.getPrecio();
    }

}
