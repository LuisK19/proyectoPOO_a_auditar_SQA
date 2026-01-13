package Modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

public class Evento {
    private int id;
    private tipoEvento tipo;
    private int calle;
    private int avenida;
    private Dron dronReportador;
    private ArrayList<String> accionesEjecutadas;
    private LocalDateTime fechaHora;
    private ArrayList<Evento> eventosRegistrados;

    public Evento() {
        this.accionesEjecutadas = new ArrayList<>();
        this.eventosRegistrados = new ArrayList<>();
    }

    public Evento(int avenida, int calle, tipoEvento tipo, int id) {
        this.avenida = avenida;
        this.calle = calle;
        this.tipo = tipo;
        this.id = id;
        this.accionesEjecutadas = new ArrayList<>();
        this.eventosRegistrados = new ArrayList<>();
        this.fechaHora = LocalDateTime.now();
    }

    /** Genera un ecvento aleatorio en la ciudad con la direccion del edificio donde esta sucediendo el evento
    * el cual es registrado y reportado por un dron el cual registra la fecha y hora*/
    public Evento generarEvento(ArrayList<EdificioInteligente> edificios, Dron dron){
        Random rand = new Random();
        tipoEvento[] tipos = tipoEvento.values();
        tipoEvento tipoRandom = tipos[rand.nextInt(tipos.length)];
        EdificioInteligente edificio = edificios.get(rand.nextInt(edificios.size()));

        int newId = eventosRegistrados.size() + 1;
        Evento nuevoEvento = new Evento(edificio.getAvenida(), edificio.getCalle(), tipoRandom, newId);
        nuevoEvento.setDronReportador(dron);
        nuevoEvento.fechaHora = LocalDateTime.now();
        eventosRegistrados.add(nuevoEvento);
        return nuevoEvento;
    }

    public void agregarAccionEjecutada(String accion) {
        accionesEjecutadas.add(accion);
    }

    /** getters y setters */
    public void setDronReportador(Dron dronReportador) { this.dronReportador = dronReportador; }
    public int getAvenida() { return avenida; }
    public void setAvenida(int avenida) { this.avenida = avenida; }
    public int getCalle() { return calle; }
    public void setCalle(int calle) { this.calle = calle; }
    public tipoEvento getTipo() { return tipo; }
    public void setTipo(tipoEvento tipo) { this.tipo = tipo; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getFechaHora(){ return fechaHora; }


}

