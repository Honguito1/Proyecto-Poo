package com.SistemaPedidos.pedidos.ClasesUML;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    @Test
    void testClienteCreacionYGetters() {
        Cliente cliente = new Cliente("Ana", "ana@mail.com", "Calle Falsa 123", "123456");
        cliente.setFirebaseKey("abc123");

        assertEquals("Ana", cliente.getNombre());
        assertEquals("ana@mail.com", cliente.getEmail());
        assertEquals("Calle Falsa 123", cliente.getDireccion());
        assertEquals("123456", cliente.getTelefono());
        assertEquals("abc123", cliente.getFirebaseKey());
    }

}