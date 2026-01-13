package Modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class Robot {

    private int bateria;
    private static final int bateriaMin = 15;
    private String procesador;
    private Collection<AccionRobot> tareas;
    private boolean activo;
    private ArrayList<RegistroTarea> historialTareas;
    private boolean asignado = false;
    private static int contadorRobots = 1;

    /** Constructor por defecto (procesador automático) */
    public Robot(){
        this.tareas = new ArrayList<>();
        this.historialTareas = new ArrayList<>();
        this.activo = true;
        this.procesador = "ROB-" + contadorRobots++;
        this.bateria = 100;
    }

    /** Constructor simple con activo y bateria */
    public Robot(boolean activo, int bateria) {
        this.activo = activo;
        this.bateria = bateria;
        this.procesador = "ROB-" + contadorRobots++;
        this.tareas = new ArrayList<>();
        this.historialTareas = new ArrayList<>();
    }

    /** Constructor que acepta lista de tareas */
    public Robot(boolean activo, int bateria, ArrayList<AccionRobot> tareas) {
        this.activo = activo;
        this.bateria = bateria;
        this.procesador = "ROB-" + contadorRobots++;
        this.tareas = (tareas != null) ? tareas : new ArrayList<>();
        this.historialTareas = new ArrayList<>();
    }

    /** Si alguna parte del código quiere crear con procesador explícito: */
    public Robot(boolean activo, int bateria, String procesador, ArrayList<AccionRobot> tareas) {
        this.activo = activo;
        this.bateria = bateria;
        this.procesador = (procesador == null || procesador.isBlank()) ? "ROB-" + contadorRobots++ : procesador;
        this.tareas = (tareas != null) ? tareas : new ArrayList<>();
        this.historialTareas = new ArrayList<>();
    }

    /** Obtiene el posible consumo de energia del robot al hacer alguna tarea en especifico*/
    public int obtenerConsumoEnergia(AccionRobot tarea) {
        switch (tarea) {
            case AGENDAR_CITA_MEDICA: return 5;
            case ASEAR_DORMITORIO: return 15;
            case ELABORAR_LISTA_ALIMENTOS_A_COMPRAR: return 10;
            case REGAR_PLANTAS: return 5;
            case DAR_PASEO_CON_CIUDADANO: return 20;
            case ASISTIR_CIUDADANO_REUNION_EN_OTRO_EDIFICIO_INTELIGENTE: return 25;
            default: return 0;
        }
    }

    /** Verifica si el robot cuenta con mas de la bateria minima requerida y la tarea tiene un consumo menor
    * de la bateria disponible es porque el robot puede realizar la tarea*/
    public boolean puedeRealizarTarea(AccionRobot tarea) {
        int consumo = obtenerConsumoEnergia(tarea);
        return consumo > 0 && bateria >= consumo && bateria > bateriaMin;
    }

    /** Verifica que el robot pueda hacer la tarea y si esto se cumple el robot realiza la respectiva tarea
    * disminuyendo su bateria y realizando un registro de esta misma*/
    public boolean hacerTarea(AccionRobot tarea) {
        if (puedeRealizarTarea(tarea)) {
            int consumo = obtenerConsumoEnergia(tarea);
            bateria -= consumo;
            historialTareas.add(new RegistroTarea(tarea, LocalDateTime.now()));
            activo = (bateria > bateriaMin);
            return true;
        }
        return false;
    }

    /** Recorre todas la tareas disponibles y añade aleatoriamente algunas de estas a las tareas del robot*/
    public void asignarTareasAleatorias(ArrayList<AccionRobot> todasLasTareas, int cantidad) {
        Collections.shuffle(todasLasTareas);
        tareas.clear();
        for (int i = 0; i < Math.min(cantidad, todasLasTareas.size()); i++) {
            tareas.add(todasLasTareas.get(i));
        }
    }

    /** Registra una tarea con su fecha*/
    public void registrarTarea(AccionRobot accion, LocalDateTime fechaHora) {
        historialTareas.add(new RegistroTarea(accion, fechaHora));
    }

    /** Recarga la bateria del robot y lo pone activo si esta es mayor al minimo*/
    public void recargarBateriaCompleta() {
        this.bateria = 100;
        activo = (bateria > bateriaMin);
    }


    public boolean necesitaRecarga() {
        return bateria <= bateriaMin;
    }

    /** getters y setters */
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public int getBateria() { return bateria; }
    public int getBateriaMin() { return bateriaMin; }
    public void setBateria(int bateria) { this.bateria = bateria; }

    public ArrayList<RegistroTarea> getHistorialTareas() { return new ArrayList<>(historialTareas); }
    public void setHistorialTareas(Collection<RegistroTarea> historialTareas) { this.historialTareas = new ArrayList<>(historialTareas); }

    public String getProcesador() { return procesador; }
    public void setProcesador(String procesador) { this.procesador = procesador; }

    public Collection<AccionRobot> getTareas() { return tareas; }
    public void setTareas(Collection<AccionRobot> tareas) { this.tareas = tareas; }

    public boolean isAsignado() { return asignado; }
    public void setAsignado(boolean asignado) { this.asignado = asignado; }

    public static int getContadorRobots() { return contadorRobots; }
    public static void reiniciarContador() { contadorRobots = 1; }
}

