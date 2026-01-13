package Modelo;

import java.util.UUID;

public class Dron {
    private String procesador;
    private int bateria;
    private Estado estado;
    private int horasVuelo;
    private EdificioInteligente edificioAsignado;
    private boolean enAlerta = false;
    private final int minimoBateria = 15;
    private static int contadorDrones = 1;

    /** Constructor principal: genera procesador automático */
    public Dron(int horasVuelo) {
        this.horasVuelo = horasVuelo;
        this.bateria = Math.min(100, horasVuelo * 25); // 1h=25%, etc.
        this.estado = Estado.DISPONIBLE;
        this.procesador = "DRON-" + contadorDrones++;
    }

    /** Constructor que recibe edificio asignado (sin procesador explícito) */
    public Dron(int bateria, Estado estado, int horasVuelo, EdificioInteligente edificioAsignado) {
        this.bateria = bateria;
        this.estado = estado;
        this.horasVuelo = horasVuelo;
        this.procesador = "DRON-" + contadorDrones++;
        this.edificioAsignado = edificioAsignado;
    }

    /** Constructor que permite pasar procesador */
    public Dron(int bateria, Estado estado, int horasVuelo, String procesador, EdificioInteligente edificioAsignado) {
        this.bateria = bateria;
        this.estado = estado;
        this.horasVuelo = horasVuelo;
        if (procesador == null || procesador.isBlank()) {
            this.procesador = "DRON-" + contadorDrones++;
        } else {
            this.procesador = procesador;
        }
        this.edificioAsignado = edificioAsignado;
    }

    /** Si el dron cuenta con una bateria igual o mayor a 25 y esta disponible puede patrullar*/
    public boolean puedePatrullar() {
        return bateria >= 25 && estado == Estado.DISPONIBLE;
    }

    /** El dron realiza un patrullaje siempre y cuando tenga mas del 25 de bateria, este disponible y cuente
    * con horas de vuelo, si el dron patrulla se le resta una hora de vuelo y 25 de bateria, si cuando esto pasa
    * la bateria del dron es menor a la minima el dron se pone en alerta*/
    public boolean patrullar() {
        if (bateria >= 25 && estado == Estado.DISPONIBLE && horasVuelo > 0) {
            bateria -= 25;
            horasVuelo--;
            if (bateria < minimoBateria) enAlerta = true;
            return true;
        }
        return false;
    }

    /** Si la bateria del dron es menor que la bateria minima requerida pone al dron en alerta
    * lo que significa que necesita recargarse*/
    public boolean necesitaRecarga() {
        enAlerta = (bateria < minimoBateria);
        return enAlerta;
    }

    /**Recarga la bateria del dron y reinicia las horas de vuelo disponibles ademas de ya no estar en alerta*/
    public void recargarBateria() {
        this.bateria = 100;
        this.horasVuelo = 4;
        this.enAlerta = false;
    }

    /** getters y setters */
    public int getBateria() { return bateria; }
    public void setBateria(int bateria) { this.bateria = bateria; }

    public int getHorasVuelo() { return horasVuelo; }
    public void setHorasVuelo(int horasVuelo) { this.horasVuelo = horasVuelo; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public String getProcesador() { return procesador; }
    public void setProcesador(String procesador) { this.procesador = procesador; }

    public EdificioInteligente getEdificioAsignado() { return edificioAsignado; }
    public void setEdificioAsignado(EdificioInteligente edificioAsignado) { this.edificioAsignado = edificioAsignado; }
}

