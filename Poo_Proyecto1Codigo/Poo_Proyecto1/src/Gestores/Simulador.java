package Gestores;

import Modelo.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;
/**
 * Clase Simulador.
 * Se encarga de orquestar la simulación completa: asignación de robots,
 * ejecución de tareas de ciudadanos, patrullaje de drones, generación de eventos,
 * recargas y reporte del historial de tareas.
 */
public class Simulador {
    /** Gestores que administran ciudadanos, drones, estaciones, robots, edificios y el consejo*/
    private GestorCiudadanos gesCiudadano;
    private GestorDrones gesDrones;
    private GestorEstacion gesEstacion;
    private GestorRobots gesRobots;
    private GestorEdificios gesEdificios;
    private ConsejoInteligencia consejoInteligencia;
    private Random rand = new Random();

    /** Formato de fecha y hora para registros*/
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**Constructor que recibe todos los gestores y el consejo de inteligencia*/
    public Simulador(GestorCiudadanos gesCiudadano, GestorDrones gesDrones, GestorEstacion gesEstacion,
                     GestorRobots gesRobots, GestorEdificios gesEdificios, ConsejoInteligencia consejoInteligencia) {
        this.gesCiudadano = gesCiudadano;
        this.gesDrones = gesDrones;
        this.gesEstacion = gesEstacion;
        this.gesRobots = gesRobots;
        this.gesEdificios = gesEdificios;
        this.consejoInteligencia = consejoInteligencia;
    }

    /**
     * Asigna robots de manera aleatoria a los ciudadanos.
     * Solo se asignan robots libres a ciudadanos existentes.
     */
    public void asignarRobotsAleatoriamente() {
        ArrayList<Ciudadano> ciudadanos = gesCiudadano.getCiudadanos();
        ArrayList<Robot> robotsDisponibles = gesRobots.getRobotsDisponibles();

        /** Validación: verificar que existan ciudadanos y robots*/
        if (ciudadanos.isEmpty() || robotsDisponibles.isEmpty()) {
            System.out.println("No hay suficientes ciudadanos o robots para asignar.");
            return;
        }
        /** Recorre ciudadanos y asigna robots aleatoriamente*/
        Random rand = new Random();
        for (Ciudadano c : ciudadanos) {
            if (rand.nextBoolean() && !robotsDisponibles.isEmpty()) {
                Robot robot = robotsDisponibles.get(rand.nextInt(robotsDisponibles.size()));
                c.asignarRobot(robot);
                robot.setAsignado(true);
                robotsDisponibles.remove(robot);
                System.out.println("Robot " + robot.getProcesador() + " asignado a " + c.getNombre());
            }
        }
    }

    /**
     * Hace que los ciudadanos ejecuten tareas aleatorias
     * utilizando sus robots asignados.
     */
    public void tareasCiudadanos() {
        for (Ciudadano c : gesCiudadano.listarCiudadanos()) {
            // Verifica que el ciudadano tenga al menos un robot
            if (c.tieneRobotsAsignados()) {
                AccionRobot[] acciones = AccionRobot.values();
                AccionRobot tarea = acciones[rand.nextInt(acciones.length)];

                // Solicita que un robot ejecute la tarea
                Robot ejecutor = c.solicitarTarea(tarea);

                if (ejecutor != null) {
                    // Registra la tarea con fecha y hora
                    ejecutor.registrarTarea(tarea, LocalDateTime.now());

                    System.out.println("[TAREA] Ciudadano " + c.getNombre() + " realizó tarea " + tarea +
                            " con el robot " + ejecutor.getProcesador() +
                            " a las " + LocalDateTime.now().format(FORMATTER));

                } else {
                    System.out.println("[TAREA FALLIDA] " + c.getNombre() + " intentó " + tarea +
                            " pero los robots no tenían batería suficiente.");
                }
            }
        }
    }

    /**
     * Realiza el patrullaje de los drones.
     * Si necesitan recarga, intentan hacerlo en una estación disponible.
     */
    public void patrullaDrones() {
        ArrayList<Dron> drones = new ArrayList<>(gesDrones.getDrones());
        for (Dron d : drones) {
            // Si puede patrullar, realiza la acción
            if (d.puedePatrullar()) {
                boolean ok = d.patrullar();
                System.out.println("[PATRULLA] Dron " + d.getProcesador() + " patrulló. Batería restante: " + d.getBateria() + "%");
            }
            // Si necesita recarga, busca una estación disponible
            else if (d.necesitaRecarga()) {
                boolean recargado = false;
                for (EstacionRecarga est : gesEstacion.getEstaciones()) {
                    if (est.puedeAtender()) {
                        est.atenderDron(d);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        System.out.println("[RECARGA] Dron " + d.getProcesador() +
                                " recargado en estación " + est.getId() + " a las " + LocalDateTime.now().format(formatter));
                        recargado = true;
                        break;
                    }
                }
                if (!recargado) {
                    System.out.println("[RECARGA] No hay estaciones disponibles para " + d.getProcesador());
                }
            }
            // Caso en que no patrulla por otra razón
            else {
                System.out.println("[INFO] Dron " + d.getProcesador() + " no patrulló (batería: " + d.getBateria() + "%).");
            }
        }
    }

    /**
     * Genera un evento aleatorio con un dron seleccionado
     * y lo procesa mediante el consejo de inteligencia.
     */
    public void eventoAleatorio() {
        ArrayList<Dron> drones = new ArrayList<>(gesDrones.getDrones());
        if (!drones.isEmpty()) {
            // Selecciona un dron aleatorio para generar un evento
            Dron dronEvento = drones.get(rand.nextInt(drones.size()));
            Evento e = new Evento().generarEvento(gesEdificios.getEdificios(), dronEvento);

            // Consejo de inteligencia procesa el evento
            consejoInteligencia.procesarEvento(e);
            System.out.println("[EVENTO] Dron " + dronEvento.getProcesador() +
                    " reportó evento " + e.getTipo() + " a las " + e.getFechaHora());
        }
    }

    /**
     * Intenta recargar los robots en alerta de los ciudadanos
     * en estaciones de recarga disponibles.
     */
    public void recargarRobotsAlerta() {
        for (Ciudadano c : gesCiudadano.listarCiudadanos()) {
            for (Robot r : c.getRobotsAsignados()) {
                if (r.necesitaRecarga()) {
                    boolean recargado = false;
                    // Busca una estación disponible para recargar
                    for (EstacionRecarga est : gesEstacion.getEstaciones()) {
                        if (est.puedeAtender()) {
                            est.atenderRobot(r);
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            System.out.println("[RECARGA] Robot " + r.getProcesador() +
                                    " del ciudadano " + c.getNombre() +
                                    " recargado en estación " + est.getId() +
                                    " a las " + LocalDateTime.now().format(formatter));
                            recargado = true;
                            break;
                        }
                    }
                    if (!recargado) {
                        System.out.println("[RECARGA] No hay estaciones disponibles para robot " + r.getProcesador());
                    }
                }
            }
        }
    }

    /**
     * Muestra por consola el historial de tareas realizadas por
     * cada robot de los ciudadanos.
     */
    public void historialTareas() {
        for (Ciudadano c : gesCiudadano.listarCiudadanos()) {
            for (Robot r : c.getRobotsAsignados()) {
                System.out.println("Robot " + r.getProcesador() + " del ciudadano " + c.getNombre());
                for (RegistroTarea rt : r.getHistorialTareas()) {
                    System.out.println("   " + rt);
                }
            }
        }
    }

    /**Ejecuta el flujo prinicpal de la simulacion*/
    public void ejecutarSimulacion() {
        System.out.println("\n========== INICIANDO SIMULACIÓN ==========\n");

        // 1. Asignar robots a ciudadanos que no tengan (solo robots libres)
        asignarRobotsAleatoriamente();
        // 2. Ciudadanos ejecutan tareas aleatorias
        tareasCiudadanos();
        // 3. Drones patrullan (y recargan si es necesario)
        patrullaDrones();
        // 4. Generar evento aleatorio con un dron
        eventoAleatorio();
        // 5. Recargar robots que estén en alerta en estaciones
        recargarRobotsAlerta();

        System.out.println("\n--- HISTORIAL DE TAREAS ---");
        // 6. Historial de tareas realizadas
        historialTareas();
        System.out.println("\n========== FIN DE SIMULACIÓN ==========\n");
    }
}

