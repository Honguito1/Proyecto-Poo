package com.SistemaPedidos.pedidos.ClasesUML;

import com.SistemaPedidos.pedidos.ClasesUML.Productos.Producto;
import java.util.List;

public class Pedido {
    private String firebaseKey;
    private String fecha;
    private EstadoPedido estado;
    private List<Producto> productos;
    private Cliente cliente;
    private double costoTotal;

    public Pedido(){}
    public Pedido(String firebaseKey, String fecha, EstadoPedido estado, List<Producto> productos, Cliente cliente) {
        this.firebaseKey = firebaseKey;
        this.fecha = fecha;
        this.estado = estado;
        this.productos = productos;
        this.cliente = cliente;
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }
    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    public String getFecha() {
        return fecha;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public EstadoPedido getEstado() {
        return estado;
    }
    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public List<Producto> getProductos() {
        return productos;
    }
    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public Cliente getCliente() {
        return cliente;
    }
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public double getCostoTotal() {
        return costoTotal;
    }
    public void setCostoTotal(double costoTotal) {
        this.costoTotal = costoTotal;
    }
}
