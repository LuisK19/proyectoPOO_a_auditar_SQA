package Gestores;

import Modelo.EstacionRecarga;
import Modelo.Estado;

import java.util.ArrayList;

/**
 * Clase encargada de gestionar un conjunto de estaciones de recarga.
 * Permite crear nuevas estaciones, inicializar un conjunto predefinido de ellas y obtener
 * la lista de estaciones registradas. Cada estación cuenta con una ubicación (avenida y calle),
 * una capacidad y un estado de disponibilidad.
 */
public class GestorEstacion {
    /**Lista de estaciones de recarga registradas en el sistema.*/
    private ArrayList<EstacionRecarga> estaciones = new ArrayList<>();

    /**Crea y registra una nueva estación de recarga y no se permite registrar más de 8 estaciones*/
    public EstacionRecarga crearEstacion(int avenida, int calle, int capacidad, Estado estado) {
        /**No deja registrar mas estaciones si se llegó al limite de 8*/
        if (estaciones.size() >= 8) {
            System.out.println("No se pueden registrar más de 8 estaciones.");
            return null;
        }
        /**Sino se crea una nueva estacion*/
        EstacionRecarga estacion = new EstacionRecarga(avenida, calle, capacidad, estado);
        estaciones.add(estacion);
        return estacion;
    }
    /**
     * Inicializa un conjunto de estaciones de recarga con datos generados automáticamente.
     * La cantidad de estaciones debe estar entre 5 y 8, de lo contrario se lanza una excepción.
     */
    public void inicializarEstaciones(int cantidad) {
        /**No deja inicializar una estacion si la cantidad esta menor a 5 o mayor a 8*/
        if (cantidad < 5 || cantidad > 8) {
            throw new IllegalArgumentException("La cantidad de estaciones debe estar entre 5 y 8.");
        }
        /**Crea un contador para llevar el registro de estaciones y añade una nueva estacion*/
        for (int i = 0; i < cantidad; i++) {
            int avenida = i + 1;
            int calle = (i + 1) * 10;
            int capacidad = 3 + i;
            Estado estado = Estado.DISPONIBLE;
            crearEstacion(avenida, calle, capacidad, estado);
        }
    }
    /**Devuelve la lista de estaciones de recarga registradas*/
    public ArrayList<EstacionRecarga> getEstaciones() {
        return estaciones;
    }
}
