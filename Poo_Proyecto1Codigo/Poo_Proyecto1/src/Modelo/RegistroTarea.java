package Modelo;

import java.time.LocalDateTime;

/** Representa una tarea ejecutada por un Robot con su fecha y hora.*/
public class RegistroTarea {
    private AccionRobot tarea;
    private LocalDateTime fechaHora;

    /** Crea un registro de tarea realizada por el robot.*/
    public RegistroTarea(AccionRobot tarea, LocalDateTime fechaHora) {
        this.tarea = tarea;
        this.fechaHora = fechaHora;
    }

    public AccionRobot getTarea() {
        return tarea; }
    public LocalDateTime getFechaHora() {
        return fechaHora; }

    @Override
    public String toString() {
        return tarea + " @ " + fechaHora.toString();
    }
}
