package Modelo;

import java.util.ArrayList;

public class EdificioInteligente {
    private String id;
    private String nombre;
    private int calle;
    private int avenida;
    private int capacidadMaxima;
    private int capacidadActual;
    private ArrayList<Ciudadano> listaResidentes;

    public EdificioInteligente() {
    }

    public EdificioInteligente(int avenida, int calle, int capacidadMaxima, String id, String nombre) {
        this.avenida = avenida;
        this.calle = calle;
        this.capacidadMaxima = capacidadMaxima;
        this.id = id;
        this.nombre = nombre;
        this.capacidadActual = 0;
        this.listaResidentes = new ArrayList<>();
    }

    /** Verifica si el edificio cuenta con espacios disponibles*/
    public boolean tieneCapacidadDisponible() {
        return capacidadActual < capacidadMaxima;
    }

    /** Si el edificio tiene espacio disponible agrega un nuevo ciudadano e incrementa la cantidad de ciudados
    * residentes en este edificio*/
    public boolean agregarCiudadano(Ciudadano ciudadano) {
        if (tieneCapacidadDisponible()) {
            listaResidentes.add(ciudadano);
            capacidadActual++;
            ciudadano.setEdificioAsignado(this);
            return true;
        }
        return false;
    }

    /** Cuenta la cantidad de ciudadanos en el edificio que tenga algun robot asignado*/
    public int contarCiudadanosConRobots() {
        int count = 0;
        for (Ciudadano ciudadano : listaResidentes) {
            if (ciudadano.tieneRobotsAsignados()) {
                count++;
            }
        }
        return count;
    }

    /** Cuenta la cantidad de robots aignados a los ciudadanos que cuenten con un robot en alerta,
    * esto cuando la bateria del robot es muy baja*/
    public int contarRobotsEnAlerta(int umbralMinimo) {
        int count = 0;
        for (Ciudadano ciudadano : listaResidentes) {
            for (Robot robot : ciudadano.getRobotsAsignados()) {
                if (robot.getBateria() < umbralMinimo) {
                    count++;
                }
            }
        }
        return count;
    }

    /** Obtiene la cantidad de ciudadanos residente es en el edifico en un porcentaje*/
    public double calcularPorcentajeOcupacion() {
        if (capacidadMaxima <= 0) return 0.0;
        return (double) capacidadActual / capacidadMaxima * 100;
    }

    /** Verifica si el edificio esta lleno*/
    public boolean estaLleno() {
        return capacidadActual >= capacidadMaxima;
    }

    /** Verifica si el nuevo edificio cuenta con espacio disponible si es asi añade el ciudadano y lo elimina
    * de la lista de de residentes del edificio anterior y libera ese espacio*/
    public boolean mudarse(EdificioInteligente nuevoEdificio, Ciudadano ciudadano) {
        if (nuevoEdificio != null && !nuevoEdificio.estaLleno()) {
            this.listaResidentes.remove(ciudadano);
            this.capacidadActual--;

            nuevoEdificio.getListaResidentes().add(ciudadano);
            nuevoEdificio.setCapacidadActual(nuevoEdificio.getCapacidadActual() + 1);

            ciudadano.setEdificioAsignado(nuevoEdificio);
            return true;
        }
        return false;
    }


    /** getters y setters */
    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public int getAvenida() {
        return avenida;
    }

    public void setAvenida(int avenida) {
        this.avenida = avenida;
    }

    public int getCalle() {
        return calle;
    }

    public void setCalle(int calle) {
        this.calle = calle;
    }

    public ArrayList<Ciudadano> getListaResidentes() {
        return listaResidentes;
    }

    public void setListaResidentes(ArrayList<Ciudadano> listaResidentes) {
        this.listaResidentes = listaResidentes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCapacidadActual() {
        return capacidadActual;
    }

    public void setCapacidadActual(int capacidadActual) {
        this.capacidadActual = capacidadActual;
    }

    public String getNombre() { return nombre; }
}
