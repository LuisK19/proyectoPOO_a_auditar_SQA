package Gestores;

import Modelo.EdificioInteligente;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Clase encargada de administrar un conjunto de edificios inteligentes.
 * Permite crear y registrar nuevos edificios, inicializar una lista de edificios predefinidos,
 * y consultar la lista de edificios registrados.
 */
public class GestorEdificios {

    /** Lista de edificios inteligentes registrados en el sistema. */
    private ArrayList<EdificioInteligente> edificios = new ArrayList<>();

    /**
     * Crea y registra un nuevo edificio en el sistema.
     * No se permite registrar más de 10 edificios.
     * No se permite registrar dos edificios con el mismo ID.
     */

    public EdificioInteligente crearEdificio(String id, String nombre, int calle, int avenida, int capacidad) {
        /** Validación: no permitir más de 10 edificios */
        if (edificios.size() >= 10) {
            System.out.println("No se pueden registrar más de 10 edificios.");
            return null;
        }
        /** Validación: no permitir IDs duplicados */
        for (EdificioInteligente existente : edificios) {
            if (existente.getId().equals(id)) {
                System.out.println("Ya existe un edificio con ese ID.");
                return null;
            }
        }
        /** Crear y registrar el edificio */
        EdificioInteligente e = new EdificioInteligente(avenida, calle, capacidad, id, nombre);
        edificios.add(e);
        return e;
    }


    /**
     * Inicializa un conjunto de edificios inteligentes con datos generados automáticamente.
     * La cantidad de edificios debe estar entre 3 y 10, de lo contrario se lanza una excepción.
     * Los edificios generados tendrán IDs, nombres, calles, avenidas y capacidades predefinidas.
     */
    public void inicializarEdificios(int cantidad) {
        /**Si la cantidad de edificios no esta entre 3 y 10, lanza una excepción*/
        if (cantidad < 3 || cantidad > 10) {
            throw new IllegalArgumentException("La cantidad de edificios debe estar entre 3 y 10.");
        }
        /**Sino, añade un contador para llevar un registro de los edificios creados y cre uno*/
        for (int i = 0; i < cantidad; i++) {
            String id = "Id: " + (i + 1);
            String nombre = "Edificio: " + (i + 1);
            int calle = (i + 1) * 10;
            int avenida = (i + 1);
            int capacidad = 5 + i;

            crearEdificio(id, nombre, calle, avenida, capacidad);
        }
    }

    /**Devuelve la lista de edificios registrados en el sistema.*/
    public ArrayList<EdificioInteligente> getEdificios() {
        return edificios;
    }
}
