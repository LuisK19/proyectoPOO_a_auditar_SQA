package Gestores;

import Modelo.AccionRobot;
import Modelo.Ciudadano;
import Modelo.Robot;

import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;


/**
 * Clase GestorRobots:
 * Se encarga de gestionar un conjunto de robots, permitiendo crearlos,
 * asignarlos a ciudadanos, buscarlos, eliminarlos y verificar disponibilidad.
 */
public class GestorRobots {
    private ArrayList<Robot> robots = new ArrayList<Robot>();

    /**Función crearRobot: toma como parametros activo, bateria y tareas para poder crear un robot*/
    public Robot crearRobot(boolean activo, int bateria, ArrayList<AccionRobot> tareas){
        /**Crea un nuevo robot con las caracteristicas necesarias*/
        Robot r = new Robot(activo, bateria, tareas);
        /**Crea un ArrayList de acciones que puede tener el robot*/
        ArrayList<AccionRobot> todas = new ArrayList<>();
        /**Crea una cantidad aleatoria de tareas para asignarle al  robot*/
        for (AccionRobot a : AccionRobot.values()) todas.add(a);
        int cant = new Random().nextInt(todas.size()) + 1;
        r.asignarTareasAleatorias(todas, cant);

        robots.add(r);
        return r;
    }


    /**Esta función se encarga de eliminar un robot desde la informacion de su procesador*/
    public boolean eliminarRobot(String procesador) {
        Robot robot = buscarRobot(procesador);
        if (robot != null && !robot.isAsignado()) {
            robots.remove(robot);
            return true;
        }
        return false;
    }
    /**Esta función se encarga de buscar a un robot desde su numero de procesador*/
    public ArrayList<Robot> buscarRobotsPorProcesador(String procesador) {
        ArrayList<Robot> resultados = new ArrayList<>();
        for (Robot r : robots) {
            if (r.getProcesador().toLowerCase().contains(procesador.toLowerCase())) {
                resultados.add(r);
            }
        }
        return resultados;
    }


    /**Función getRobotsDisponibles: crea un ArrayList de robots disponibles y retorna los robots del array*/
    public ArrayList<Robot> getRobotsDisponibles() {
        /**Se crea un array de robots libres*/
        ArrayList<Robot> libres = new ArrayList<>();
        /**Si el robot no está asignado, significa que está disponible y se agrgega al array*/
        for (Robot r : robots) {
            if (!r.isAsignado()) {
                libres.add(r);
            }
        }
        /**Retorna el array*/
        return libres;
    }
    /**Función marcarComoAsignado: cambia el estado de asignado de false(disponible) a true(asignado)*/
    public void marcarComoAsignado(Robot r) {
        r.setAsignado(true);
    }

    /**Función asignarRobot: Esta función se encarga de que buscar asdignar un robot al ciudadano*/
    public void asignarRobot(GestorCiudadanos gCiu, String ciudadanoId){
        /**Busca el id del ciudadano*/
        Ciudadano c = gCiu.buscarCiudadanoId(ciudadanoId);
        /**Si el el ciudadano no existe(null) hace un print a consola de que no se ha encontrado el ciudadano*/
        if (c == null){
            System.out.println("[ERROR] Ciudadano no encontrado: " + ciudadanoId);
            return;
        }
        /**Crea un arrayList para saber si el robot esta disponible, y si lo está se lo asigna a un ciudadano*/
        ArrayList<Robot> disponibles = new ArrayList<>();
        for (Robot r : robots) {
            if (!r.isAsignado()) disponibles.add(r);
        }
        /**Si no hay robots disponibles, printea en consola que no los hay*/
        if (disponibles.isEmpty()) {
            System.out.println("[ERROR] No hay robots disponibles para asignar a " + c.getNombre());
            return;
        }
        Robot r = disponibles.get(new Random().nextInt(disponibles.size()));
        boolean ok = c.asignarRobot(r);
        if (ok) {
            r.setAsignado(true);
            System.out.println("[ASIGNACIÓN] Robot " + r.getProcesador() + " asignado a " + c.getNombre());
        } else {
            System.out.println("[ERROR] No se pudo asignar robot " + r.getProcesador() + " a " + c.getNombre());
        }
    }

    /**retorna los robots*/
    public ArrayList<Robot> getRobots() {
        return robots;
    }
    /**
    * Función buscarRobot: Se encarga de buscar un robot con su proesador, y si el procesador
    * ingresado coincide con un robot de la lista, lo retorna
     */
    public Robot buscarRobot(String procesador){
        for (Robot r : robots){
            if (r.getProcesador().equals(procesador)){
                return r;
            }
        }
        return null;
    }
}

