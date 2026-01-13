package Modelo;

import java.util.ArrayList;

public class Ciudadano {
    private String idCiudadano;
    private String nombre;
    private ArrayList<Robot> robotsAsignados;
    private EdificioInteligente edificioAsignado;
    public Ciudadano() {
        this.robotsAsignados = new ArrayList<>();
    }

    public Ciudadano(EdificioInteligente edificioAsignado, String idCiudadano, String nombre, ArrayList<Robot> robotsAsignados) {
        this.edificioAsignado = edificioAsignado;
        this.idCiudadano = idCiudadano;
        this.nombre = nombre;
        this.robotsAsignados = (robotsAsignados != null) ? robotsAsignados : new ArrayList<>();
    }

    public Ciudadano(String id, String nombre, EdificioInteligente edificio) {
        this.idCiudadano = id;
        this.nombre = nombre;
        this.edificioAsignado = edificio;
        this.robotsAsignados = new ArrayList<>();
    }

    /**Verifica que el ciudadano tenga al menos un robot asignado y le solicta realizar una tarea*/
    public Robot solicitarTarea(AccionRobot tarea){
        if(robotsAsignados.size() > 0){
            for(Robot robot : robotsAsignados){
                if (robot.hacerTarea(tarea)){
                    return robot;
                }
            }
        }
        return null;
    }

    /**Verifica que la lista de de robots no contenga al robot por asignar, si es asi procede a asignar el nuevo robot */
    public boolean asignarRobot(Robot robot) {
        if (robot != null && !robotsAsignados.contains(robot)) {
            return robotsAsignados.add(robot);
        }
        return false;
    }

    /**Remueve el robot recibido por parametro de la lista*/
    public boolean removerRobot(Robot robot) {

        return robotsAsignados.remove(robot);
    }

    /**Verifica si el ciudadano tiene algun robot asignado*/
    public boolean tieneRobotsAsignados() {

        return !robotsAsignados.isEmpty();
    }

    /**Muestra la cantidad de robots asignados al ciudadano*/
    public int cantidadRobotsAsignados() {

        return robotsAsignados.size();
    }

    /**Verifica si los robots asignados al ciudadano estan por debajo de una bateria minima y los pone en alerta*/
    public boolean tieneRobotsEnAlerta(int bateriaMin) {
        for (Robot robot : robotsAsignados) {
            if (robot.getBateria() < bateriaMin) {
                return true;
            }
        }
        return false;
    }

    /**Obtiene la cantidad de robots asignados al ciudadano estan en alerta*/
    public int cantidadRobotsEnAlerta(int bateriaMin) {
        int count = 0;
        for (Robot robot : robotsAsignados) {
            if (robot.getBateria() < bateriaMin) {
                count++;
            }
        }
        return count;
    }

    /** getters y setters */
    public EdificioInteligente getEdificioAsignado() { return edificioAsignado; }
    public void setEdificioAsignado(EdificioInteligente edificioAsignado) { this.edificioAsignado = edificioAsignado; }
    public String getIdCiudadano() { return idCiudadano; }
    public void setIdCiudadano(String idCiudadano) { this.idCiudadano = idCiudadano; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public ArrayList<Robot> getRobotsAsignados() { return robotsAsignados; }
    public void setRobotsAsignados(ArrayList<Robot> robotsAsignados) { this.robotsAsignados = robotsAsignados; }
}
