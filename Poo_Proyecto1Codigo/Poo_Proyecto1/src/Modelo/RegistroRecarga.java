package Modelo;

import java.time.LocalDateTime;

/** Representa un registro de recarga para un dispositivo (Robot o Dron).*/
public class RegistroRecarga {
    private Dispositivos tipo;
    private String dispositivoId; /** procesador o id del dispositivo recargado */
    private LocalDateTime fechaHora;
    private int bateriaAntes;
    private int bateriaDespues;

    /** Crea un registro de recarga para un dispositivo.*/
    public RegistroRecarga(Dispositivos tipo, String dispositivoId, int bateriaAntes, int bateriaDespues) {
        this.tipo = tipo;
        this.dispositivoId = dispositivoId;
        this.bateriaAntes = bateriaAntes;
        this.bateriaDespues = bateriaDespues;
        this.fechaHora = LocalDateTime.now();
    }

    /** getters */
    public Dispositivos getTipo() { return tipo; }
    public String getDispositivoId() { return dispositivoId; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public int getBateriaAntes() { return bateriaAntes; }
    public int getBateriaDespues() { return bateriaDespues; }

    @Override
    public String toString() {
        return tipo + " " + dispositivoId + " recargado: " + bateriaAntes + "% -> " + bateriaDespues + "% at " + fechaHora;
    }
}

