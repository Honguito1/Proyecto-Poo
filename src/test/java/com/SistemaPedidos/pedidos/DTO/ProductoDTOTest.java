package com.SistemaPedidos.pedidos.DTO;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductoDTOTest {

    @Test
    void testCalcularCosto_ComidaConCantidad() {
        ProductoDTO producto = new ProductoDTO("Hamburguesa", 10.0, "Comida", 3);
        assertEquals(30.0, producto.calcularCosto());
    }
    @Test
    void testCalcularCosto_BebidaSinCantidad() {
        ProductoDTO producto = new ProductoDTO("Coca-Cola", 5.0, "Bebida", null);
        assertEquals(5.0, producto.calcularCosto());
    }
}