package com.SistemaPedidos.pedidos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Lector {
    public String leerArchivo(String ruta) {
        Path rutaArchivo = Paths.get(ruta);
        String contenido = "";
        try{
            contenido = new String(Files.readAllBytes(rutaArchivo));
        }catch (IOException e){
            System.out.println("El archivo no puede ser leido");
        }
        return  contenido;
    }
}
