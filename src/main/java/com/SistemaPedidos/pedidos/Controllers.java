package com.SistemaPedidos.pedidos;

import com.SistemaPedidos.pedidos.ClasesUML.*;
import com.SistemaPedidos.pedidos.ClasesUML.Productos.*;
import com.SistemaPedidos.pedidos.DTO.PedidoDTO;
import com.SistemaPedidos.pedidos.DTO.ProductoDTO;
import com.google.firebase.database.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@Controller
public class Controllers {

    //todo:Index
    @GetMapping("/")
    public String index(Model model) {
        System.out.println("Cargando datos para el index...");
        model.addAttribute("cantidadPedidos", new FirebaseFun().cantidadPedidos());
        model.addAttribute("cantidadPendientes", new FirebaseFun().cantidadPendientes());
        model.addAttribute("cantidadProductos", new FirebaseFun().cantidadProductos());
        model.addAttribute("cantidadClientes", new FirebaseFun().cantidadClientes());
        return "index";}


    //todo:Pedidos
    @GetMapping("/pedidos")
    public String verPedidos(Model model) {
        List<Pedido> pedidos = new FirebaseFun().obtenerPedidos();
        List<PedidoDTO> pedidosDTO = new ArrayList<>();

        for (Pedido pedido : pedidos) {
            List<ProductoDTO> productosDTO = new ArrayList<>();
            double costoTotal = 0;

            if (pedido.getProductos() != null) {
                for (Producto producto : pedido.getProductos()) {
                    ProductoDTO dto = new ProductoDTO();
                    dto.setNombre(producto.getNombre());
                    dto.setPrecio(producto.getPrecio());

                    try {
                        if (producto instanceof Comida comida) {
                            dto.setCantidad(comida.getCantidad());
                            dto.setTipo("Comida");
                            costoTotal += comida.calcularCosto();
                        } else if (producto instanceof Bebida bebida) {
                            dto.setTipo(bebida.getTipo());
                            dto.setCantidad(1); // Default para bebida
                            costoTotal += bebida.calcularCosto();
                        } else {
                            // fallback por si no es Comida ni Bebida
                            dto.setTipo("Producto");
                            dto.setCantidad(1);
                            costoTotal += producto.getPrecio();
                        }
                    } catch (Exception e) {
                        System.err.println("Error al procesar producto: " + e.getMessage());
                    }

                    productosDTO.add(dto);
                }
            }

            PedidoDTO dto = new PedidoDTO();
            dto.setFirebaseKey(pedido.getFirebaseKey());
            dto.setFecha(pedido.getFecha());
            dto.setEstado(pedido.getEstado() != null ? pedido.getEstado().toString() : "DESCONOCIDO");
            dto.setProductos(productosDTO);
            dto.setCliente(pedido.getCliente());
            dto.setCostoTotal(costoTotal);

            pedidosDTO.add(dto);
        }

        List<ProductoDTO> productosDisponibles = new FirebaseFun().obtenerProductos();
        List<Cliente> clientes = new FirebaseFun().obtenerClientes();

        model.addAttribute("pedidos", pedidosDTO);
        model.addAttribute("productosDisponibles", productosDisponibles);
        model.addAttribute("clientesDisponibles", clientes);

        return "pedidos";
    }
    @PostMapping("/guardarPedido")
    public String guardarPedido(
            @RequestParam("productosSeleccionados") List<String> productosSeleccionados,
            @RequestParam("clienteEmail") String clienteEmail) {
        if (productosSeleccionados == null || productosSeleccionados.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar al menos un producto.");
        }

        if (clienteEmail == null || clienteEmail.isBlank()) {
            throw new IllegalArgumentException("Debe especificarse un cliente.");
        }

        List<Producto> productos = new ArrayList<>();
        double costoTotal = 0;

        for (String sel : productosSeleccionados) {
            String[] partes = sel.split(";");
            if (partes.length >= 2) {
                String nombre = partes[0];
                double precio = Double.parseDouble(partes[1]);
                String cantidadStr = partes.length >= 3 ? partes[2] : "";
                String tipo = partes.length >= 4 ? partes[3] : null;

                if (tipo == null || tipo.equalsIgnoreCase("Comida")) {
                    int cantidad = (!cantidadStr.isBlank()) ? Integer.parseInt(cantidadStr) : 1;
                    Comida comida = new Comida(nombre, precio, cantidad);
                    productos.add(comida);
                    costoTotal += comida.calcularCosto();
                } else {
                    Bebida bebida = new Bebida(nombre, precio, tipo);
                    productos.add(bebida);
                    costoTotal += bebida.calcularCosto();
                }
            }
        }

        Cliente clienteAsignado = new FirebaseFun().obtenerClientes().stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(clienteEmail))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con email: " + clienteEmail));

        Cliente clienteClonado = new Cliente(
                clienteAsignado.getNombre(),
                clienteAsignado.getEmail(),
                clienteAsignado.getDireccion(),
                clienteAsignado.getTelefono()
        );
        clienteClonado.setFirebaseKey(clienteAsignado.getFirebaseKey());

        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setFecha(LocalDate.now().toString());
        nuevoPedido.setEstado(EstadoPedido.ELABORANDO);
        nuevoPedido.setProductos(productos);
        nuevoPedido.setCliente(clienteClonado); // con key
        nuevoPedido.setCostoTotal(costoTotal);

        new FirebaseFun().guardarPedido(nuevoPedido);
        return "redirect:/pedidos";
    }
    @PostMapping("/actualizarEstado")
    public String actualizarEstado(@RequestParam String firebaseKey, @RequestParam String estado) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("pedidos").child(firebaseKey).child("estado");
        ref.setValueAsync(estado);
        return "redirect:/pedidos";
    }
    @PostMapping("/eliminarPedido")
    public String eliminarPedido(@RequestParam String firebaseKey) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("pedidos").child(firebaseKey);
        ref.removeValueAsync();  // Elimina el nodo del pedido
        return "redirect:/pedidos";
    }


    //todo:Productos
    @GetMapping("/productos")
    public String mapearProductos(Model model) {
        model.addAttribute("producto", new ProductoDTO());

        List<ProductoDTO> productos = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("productos");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    ProductoDTO p = child.getValue(ProductoDTO.class);
                    if (p != null) {
                        p.setFirebaseKey(child.getKey()); // 👉 asigna la key aquí
                        productos.add(p);
                    }
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Error al obtener productos: " + error.getMessage());
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        model.addAttribute("productosList", productos);
        return "productos";
    }
    @PostMapping("/guardarProducto")
    public String guardarProducto(@ModelAttribute ProductoDTO productoDTO, @RequestParam("tipoProducto") String tipoProducto) {
        if ("comida".equals(tipoProducto)) {
            productoDTO.setTipo(null); // no se guarda tipo en comida
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("productos");
        ref.push().setValueAsync(productoDTO);
        return "redirect:/productos";
    }
    @PostMapping("/eliminarProducto")
    public String eliminarProducto(@RequestParam("firebaseKey") String firebaseKey) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("productos").child(firebaseKey);
        ref.removeValueAsync(); // elimina directamente usando la clave
        return "redirect:/productos";
    }

    //todo:Clientes
    @GetMapping("/clientes")
    public String mostrarFormularioClientes(Model model) {
        model.addAttribute("cliente", new Cliente());

        // Obtener lista de clientes desde Firebase
        List<Cliente> clientes = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("clientes");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Cliente cliente = child.getValue(Cliente.class);
                    clientes.add(cliente);
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

        model.addAttribute("clientesList", clientes);
        return "clientes";
    }
    @PostMapping("/guardarCliente")
    public String guardarCliente(@ModelAttribute Cliente cliente) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("clientes");

        if (cliente.getFirebaseKey() != null && !cliente.getFirebaseKey().isBlank()) {
            // 🔁 Actualiza cliente existente
            ref.child(cliente.getFirebaseKey()).setValueAsync(cliente);
        } else {
            // ➕ Agrega nuevo cliente
            String key = ref.push().getKey();
            cliente.setFirebaseKey(key);
            ref.child(key).setValueAsync(cliente);
        }

        return "redirect:/clientes";
    }
    @PostMapping("/eliminarCliente")
    public String eliminarCliente(@RequestParam String firebaseKey) {
        DatabaseReference clientesRef = FirebaseDatabase.getInstance().getReference("clientes").child(firebaseKey);
        DatabaseReference pedidosRef = FirebaseDatabase.getInstance().getReference("pedidos");

        // Eliminar cliente
        clientesRef.removeValueAsync();

        // Buscar y eliminar pedidos relacionados a ese cliente
        pedidosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Pedido pedido = child.getValue(Pedido.class);

                    if (pedido.getCliente() != null && firebaseKey.equals(pedido.getCliente().getFirebaseKey())) {
                        pedidosRef.child(child.getKey()).removeValueAsync();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Error al eliminar pedidos del cliente: " + error.getMessage());
            }
        });

        return "redirect:/clientes";
    }
}
