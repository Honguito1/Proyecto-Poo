package com.SistemaPedidos.pedidos.ClasesUML;

import com.SistemaPedidos.pedidos.ClasesUML.Productos.Comida;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PedidoTest {

    @Test
    void testPedidoConClienteYProductos() {
        Cliente cliente = new Cliente("Luis", "luis@mail.com", "Av 1", "1111");
        Comida comida = new Comida("Pizza", 15.0, 2);

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setProductos(List.of(comida));
        pedido.setEstado(EstadoPedido.ELABORANDO);
        pedido.setFecha("2025-07-08");

        assertEquals("Luis", pedido.getCliente().getNombre());
        assertEquals(1, pedido.getProductos().size());
        assertEquals("Pizza", pedido.getProductos().get(0).getNombre());
        assertEquals(EstadoPedido.ELABORANDO, pedido.getEstado());
    }
    @Test
    void testFiltrarPedidosPendientes() {
        Pedido p1 = new Pedido();
        p1.setEstado(EstadoPedido.ENTREGADO);

        Pedido p2 = new Pedido();
        p2.setEstado(EstadoPedido.ELABORANDO);

        Pedido p3 = new Pedido();
        p3.setEstado(EstadoPedido.EN_CAMINO);

        List<Pedido> pedidos = List.of(p1, p2, p3);

        long pendientes = pedidos.stream()
                .filter(p -> p.getEstado() != EstadoPedido.ENTREGADO)
                .count();

        assertEquals(2, pendientes);
    }
}