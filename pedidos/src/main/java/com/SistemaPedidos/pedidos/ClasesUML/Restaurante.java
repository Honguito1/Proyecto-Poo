package com.SistemaPedidos.pedidos.ClasesUML;

import com.SistemaPedidos.pedidos.ClasesUML.Productos.Producto;
import java.util.ArrayList;

public class Restaurante {
    private String nombre;
    private String ubicacion;
    private ArrayList<Producto> productos;

    public Restaurante(String nombre, String ubicacion, ArrayList<Producto> productos) {
        this.productos = productos;
        this.ubicacion = ubicacion;
        this.nombre = nombre;
    }
}
