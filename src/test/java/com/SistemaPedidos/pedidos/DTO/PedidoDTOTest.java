package com.SistemaPedidos.pedidos.DTO;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PedidoDTOTest {

    @Test
    void testSetCostoTotalEnPedidoDTO() {
        PedidoDTO pedido = new PedidoDTO();
        pedido.setCostoTotal(55.5);
        assertEquals(55.5, pedido.getCostoTotal());
    }
    @Test
    void testEstadoPedidoDTO() {
        PedidoDTO pedido = new PedidoDTO();
        pedido.setEstado("EN_CAMINO");
        assertEquals("EN_CAMINO", pedido.getEstado());
    }
}