package Gestores;

import Modelo.Dron;
import Modelo.EdificioInteligente;
import java.util.ArrayList;
import java.util.Random;

/**
 * Clase encargada de gestionar la creación y asignación de drones a una lista de edificios inteligentes.
 * Cada dron se genera con una cantidad de horas aleatoria y se asigna a un edificio
 */
public class GestorDrones {

    /** Lista de drones administrados por el gestor. */
    private ArrayList<Dron> drones = new ArrayList<>();

    /**
     * Genera y asigna drones a los edificios inteligentes proporcionados.
     * La cantidad máxima de drones generados es el doble del número de edificios.
     */
    public void generarDrones(ArrayList<EdificioInteligente> edificios) {
        if (edificios == null || edificios.isEmpty()) return;
        int maxDron = edificios.size() * 2;
        Random rand = new Random();
        /** Garantizar al menos un dron por edificio */
        for (int i = 0; i < maxDron; i++) {
            int horas = rand.nextInt(4) + 1; // Número de horas entre 1 y 4
            Dron d = new Dron(horas);
            /** Asignación del dron al edificio correspondiente */
            EdificioInteligente asignado = edificios.get(i % edificios.size());
            d.setEdificioAsignado(asignado);

            drones.add(d);
        }
    }

    /** Devuelve la lista de drones generados y administrados por el gestor. */
    public ArrayList<Dron> getDrones() {
        return drones;
    }
}

