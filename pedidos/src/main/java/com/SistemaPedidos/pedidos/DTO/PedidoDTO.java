package com.SistemaPedidos.pedidos.DTO;

import com.SistemaPedidos.pedidos.ClasesUML.Cliente;

import java.util.List;

public class PedidoDTO {
    private String firebaseKey;
    private String fecha;
    private String estado;
    private List<ProductoDTO> productos;
    private Cliente cliente;
    private double costoTotal;

    public PedidoDTO() {}
    public PedidoDTO(String firebaseKey, String fecha, String estado, List<ProductoDTO> productos, Cliente cliente) {
        this.firebaseKey = firebaseKey;
        this.fecha = fecha;
        this.estado = estado;
        this.productos = productos;
        this.cliente = cliente;
        this.costoTotal = 0;
    }


    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<ProductoDTO> getProductos() { return productos; }
    public void setProductos(List<ProductoDTO> productos) { this.productos = productos; }

    public String getFirebaseKey() { return firebaseKey; }
    public void setFirebaseKey(String firebaseKey) { this.firebaseKey = firebaseKey; }

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
