package Gestores;

import Modelo.Ciudadano;
import Modelo.EdificioInteligente;
import Modelo.Robot;

import java.util.Random;
import java.util.ArrayList;

/**
 * Clase encargada de gestionar el registro de ciudadanos en edificios inteligentes y la asignación de robots
 * Permite crear ciudadanos, listarlos, buscarlos por ID y asignarles robots de forma aleatoria
 */
public class GestorCiudadanos {
    private ArrayList<Ciudadano> ciudadanos = new ArrayList<>();
    private GestorRobots gestorRobots;

    /**
     * Función crearCiudadano: Se encarga de revisar si el edicifio tiene capacidad, y si la tiene
     * asigna un ciudadano a un edificio
     */
    public Ciudadano crearCiudadano(String id, String nombre, EdificioInteligente edificio) {
        /** Si el edificio no tiene capacidad disponible, detiene la función */
        if (edificio == null || !edificio.tieneCapacidadDisponible()){
            System.out.println("Edificio no tiene capacidad disponible");
            return null;
        }
        /** Crea un nuevo ciudadano si hay espacio y lo asigna a un edificio */
        Ciudadano c = new Ciudadano(id, nombre, edificio);
        ciudadanos.add(c);
        edificio.agregarCiudadano(c);
        return c;
    }

    /** Funcion listarCiudadano: Se encarga de listas los ciudadanos que hay */
    public ArrayList<Ciudadano> listarCiudadanos() {
        return ciudadanos;
    }

    /**
     * Funcion buscarCiudadano: Se encarga de buscar un ciudadano con un id, si lo encuentra dentro de
     * los ciudadanos, lo retorna
     */
    public Ciudadano buscarCiudadanoId(String id) {
        for (Ciudadano c : ciudadanos) {
            if (c.getIdCiudadano().equals(id)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Función asignarRobotAleatorio: Se encarga de verificar que el ciudadano exista y añade un
     * robot al ciudadano
     */
    public boolean asignarRobotAleatorio(String idCiudadano) {
        /** Si el ciudadano no existe retorna falso y detiene la función */
        Ciudadano ciudadano = buscarCiudadanoId(idCiudadano);
        if (ciudadano == null) {
            System.out.println("No se encontró el ciudadano con ID: " + idCiudadano);
            return false;
        }
        /** Llama al array de robots disponibles para verificar si hay robots disponibles */
        ArrayList<Robot> robotsDisponibles = gestorRobots.getRobotsDisponibles();
        if (robotsDisponibles.isEmpty()) {
            System.out.println("No hay robots disponibles para asignar.");
            return false;
        }
        /** Si hay robots disponibles asigna mediante random un robot de la lista de disponibles */
        Random rand = new Random();
        Robot seleccionado = robotsDisponibles.get(rand.nextInt(robotsDisponibles.size()));
        ciudadano.asignarRobot(seleccionado);
        /** Marca el robot disponible como asignado */
        gestorRobots.marcarComoAsignado(seleccionado);
        System.out.println("Robot " + seleccionado.getProcesador() + " asignado a " + ciudadano.getNombre());
        return true;
    }

    /** Esta función se encarga de eliminar un ciudadano por medio del id */
    public boolean eliminarCiudadano(String id) {
        Ciudadano ciudadano = buscarCiudadanoId(id);
        if (ciudadano != null) {
            for (Robot robot : ciudadano.getRobotsAsignados()) {
                robot.setAsignado(false);
            }
            ciudadano.getEdificioAsignado().getListaResidentes().remove(ciudadano);
            ciudadanos.remove(ciudadano);
            return true;
        }
        return false;
    }

    public ArrayList<Ciudadano> getCiudadanos() {
        return ciudadanos;
    }
}