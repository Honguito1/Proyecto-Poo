package com.SistemaPedidos.pedidos.ClasesUML;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstadoPedidoTest {

    @Test
    void testEstadoPedidoEnum() {
        EstadoPedido estado = EstadoPedido.valueOf("ENTREGADO");
        assertEquals(EstadoPedido.ENTREGADO, estado);
    }
}