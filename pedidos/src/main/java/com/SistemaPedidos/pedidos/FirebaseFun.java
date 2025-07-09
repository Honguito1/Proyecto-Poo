package com.SistemaPedidos.pedidos;

import com.SistemaPedidos.pedidos.ClasesUML.*;
import com.SistemaPedidos.pedidos.ClasesUML.Productos.*;
import com.SistemaPedidos.pedidos.DTO.*;
import com.google.firebase.database.*;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FirebaseFun {
    public int cantidadPedidos() {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger cantidad = new AtomicInteger(0);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("pedidos");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    cantidad.set((int) snapshot.getChildrenCount());}
                latch.countDown();}
            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Error al leer pedidos: " + error.getMessage());
                latch.countDown();}});

        try {latch.await();} catch (InterruptedException e) {e.printStackTrace();}

        return cantidad.get();
    }
    public int cantidadProductos() {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger cantidad = new AtomicInteger(0);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("productos");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    cantidad.set((int) snapshot.getChildrenCount());
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Error al leer productos: " + error.getMessage());
                latch.countDown();
            }
        });

        try { latch.await(); } catch (InterruptedException e) { e.printStackTrace(); }
        return cantidad.get();
    }
    public int cantidadClientes() {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger cantidad = new AtomicInteger(0);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("clientes");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    cantidad.set((int) snapshot.getChildrenCount());
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Error al leer clientes: " + error.getMessage());
                latch.countDown();
            }
        });

        try { latch.await(); } catch (InterruptedException e) { e.printStackTrace(); }
        return cantidad.get();
    }
    public int cantidadPendientes() {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger cantidad = new AtomicInteger(0);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("pedidos");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String estado = child.child("estado").getValue(String.class);

                    if (estado != null) {
                        try {
                            EstadoPedido estadoPedido = EstadoPedido.valueOf(estado.toUpperCase());
                            if (estadoPedido != EstadoPedido.ENTREGADO) {
                                cantidad.incrementAndGet();
                            }
                        } catch (IllegalArgumentException e) {
                            // Si el estado no es válido, lo consideramos pendiente por precaución
                            cantidad.incrementAndGet();
                        }
                    } else {
                        // Sin estado explícito también lo consideramos pendiente
                        cantidad.incrementAndGet();
                    }
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Error al contar pedidos no entregados: " + error.getMessage());
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return cantidad.get();
    }
    public List<Pedido> obtenerPedidos() {
        List<Pedido> lista = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("pedidos");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String key = child.getKey();
                        String fecha = child.child("fecha").getValue(String.class);
                        String estadoStr = child.child("estado").getValue(String.class);
                        EstadoPedido estado = EstadoPedido.valueOf(estadoStr);

                        Cliente cliente = child.child("cliente").getValue(Cliente.class);

                        List<Producto> productos = new ArrayList<>();
                        for (DataSnapshot prodSnap : child.child("productos").getChildren()) {
                            String nombre = prodSnap.child("nombre").getValue(String.class);
                            Double precio = prodSnap.child("precio").getValue(Double.class);
                            Integer cantidad = prodSnap.child("cantidad").getValue(Integer.class);
                            String tipo = prodSnap.child("tipo").getValue(String.class);

                            if (cantidad != null) {
                                productos.add(new Comida(nombre, precio, cantidad));
                            } else {
                                productos.add(new Bebida(nombre, precio, tipo));
                            }
                        }

                        double costoTotal = child.child("costoTotal").getValue(Double.class);

                        Pedido pedido = new Pedido();
                        pedido.setFirebaseKey(key);
                        pedido.setFecha(fecha);
                        pedido.setEstado(estado);
                        pedido.setCliente(cliente);
                        pedido.setProductos(productos);
                        pedido.setCostoTotal(costoTotal);

                        lista.add(pedido);
                    }
                } catch (Exception e) {
                    System.err.println("Error al mapear pedidos: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Firebase cancelado: " + error.getMessage());
                latch.countDown();
            }
        });

        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return lista;
    }
    public void guardarPedido(Pedido pedido) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("pedidos");
        String key = ref.push().getKey();
        pedido.setFirebaseKey(key);
        ref.child(key).setValueAsync(pedido);
    }
    public List<ProductoDTO> obtenerProductos() {
        List<ProductoDTO> productos = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("productos");
        CountDownLatch latch = new CountDownLatch(1);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot prodSnapshot : snapshot.getChildren()) {
                    String nombre = prodSnapshot.child("nombre").getValue(String.class);
                    Double precio = prodSnapshot.child("precio").getValue(Double.class);
                    String tipo = prodSnapshot.child("tipo").getValue(String.class);
                    Integer cantidad = prodSnapshot.child("cantidad").getValue(Integer.class);

                    if (nombre != null && precio != null) {
                        ProductoDTO dto = new ProductoDTO(nombre, precio, tipo, cantidad);
                        productos.add(dto);
                    }
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Error al leer productos: " + error.getMessage());
                latch.countDown();
            }
        });

        try {
            latch.await(); // Espera respuesta de Firebase
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return productos;
    }
    public List<Cliente> obtenerClientes() {
        List<Cliente> clientes = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("clientes");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Cliente cliente = child.getValue(Cliente.class);
                    if (cliente != null) {
                        cliente.setFirebaseKey(child.getKey());  // 🔑 Agrega el firebaseKey
                        clientes.add(cliente);
                    }
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Error al leer clientes: " + error.getMessage());
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return clientes;
    }

}
