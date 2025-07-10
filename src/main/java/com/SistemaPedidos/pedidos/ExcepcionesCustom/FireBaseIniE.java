package com.SistemaPedidos.pedidos.ExcepcionesCustom;

public class FireBaseIniE extends Exception{
    public FireBaseIniE(){
    super("Error al cargar la base de datos de Firebase.");
}
}
