package com.SistemaPedidos.pedidos.DTO;

import com.SistemaPedidos.pedidos.ClasesUML.Calculable;

public class ProductoDTO implements Calculable {
    private String firebaseKey;
    private String nombre;
    private double precio;
    private String tipo;
    private Integer cantidad;

    public ProductoDTO() {}
    public ProductoDTO(String nombre, double precio, String tipo, Integer cantidad) {
        this.nombre = nombre;
        this.precio = precio;
        this.tipo = tipo;
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

    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getCantidad() {
        return cantidad;
    }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }
    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    @Override
    public double calcularCosto() {
        if(cantidad != null){
            return precio * cantidad;
        }
        else {return precio;}
    }

}
