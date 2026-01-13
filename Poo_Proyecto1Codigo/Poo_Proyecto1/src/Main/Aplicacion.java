package Main;

import Gestores.*;
import Modelo.Ciudadano;
import Modelo.ConsejoInteligencia;
import VistaInterfaces.PantallaPrincipal;

public class Aplicacion {
    private GestorCiudadanos gestorCiudadanos;
    private GestorDrones gestorDrones;
    private GestorEstacion gestorEstacion;
    private GestorRobots gestorRobots;
    private GestorEdificios gestorEdificios;
    private ConsejoInteligencia consejoInteligencia;

    public Aplicacion() {
        inicializarGestores();
        // Removemos la llamada automática a crearDatosPrueba()
    }

    /** Inicializar gestores */
    private void inicializarGestores() {
        gestorEdificios = new GestorEdificios();
        gestorEstacion = new GestorEstacion();
        gestorCiudadanos = new GestorCiudadanos();
        gestorRobots = new GestorRobots();
        gestorDrones = new GestorDrones();

        // Inicializar ConsejoInteligencia con listas vacías
        consejoInteligencia = new ConsejoInteligencia(
                gestorEdificios.getEdificios(),
                gestorEstacion.getEstaciones(),
                new java.util.ArrayList<>()
        );
    }

    public void crearDatosPrueba() {
        // Crear edificios
        gestorEdificios.crearEdificio("EDIF-001", "Torre Norte", 1, 1, 6);
        gestorEdificios.crearEdificio("EDIF-002", "Residencial Este", 1, 3, 6);
        gestorEdificios.crearEdificio("EDIF-003", "Condominio Sur", 3, 1, 6);
        gestorEdificios.crearEdificio("EDIF-004", "Complejo Oeste", 3, 3, 6);

        // Crear estaciones de recarga
        gestorEstacion.crearEstacion(1, 2, 8, Modelo.Estado.DISPONIBLE);
        gestorEstacion.crearEstacion(3, 2, 6, Modelo.Estado.DISPONIBLE);

        // Crear ciudadanos y asignarlos a edificios
        gestorCiudadanos.crearCiudadano("C-001", "Ana García", gestorEdificios.getEdificios().get(0));
        gestorCiudadanos.crearCiudadano("C-002", "Carlos López", gestorEdificios.getEdificios().get(0));
        gestorCiudadanos.crearCiudadano("C-003", "María Rodríguez", gestorEdificios.getEdificios().get(0));
        gestorCiudadanos.crearCiudadano("C-004", "José Martínez", gestorEdificios.getEdificios().get(0));
        gestorCiudadanos.crearCiudadano("C-005", "Laura Hernández", gestorEdificios.getEdificios().get(0));

        gestorCiudadanos.crearCiudadano("C-006", "Miguel Sánchez", gestorEdificios.getEdificios().get(1));
        gestorCiudadanos.crearCiudadano("C-007", "Elena González", gestorEdificios.getEdificios().get(1));
        gestorCiudadanos.crearCiudadano("C-008", "David Pérez", gestorEdificios.getEdificios().get(1));
        gestorCiudadanos.crearCiudadano("C-009", "Sofía Ramírez", gestorEdificios.getEdificios().get(1));
        gestorCiudadanos.crearCiudadano("C-010", "Jorge Torres", gestorEdificios.getEdificios().get(1));

        gestorCiudadanos.crearCiudadano("C-011", "Isabel Flores", gestorEdificios.getEdificios().get(2));
        gestorCiudadanos.crearCiudadano("C-012", "Roberto Vargas", gestorEdificios.getEdificios().get(2));
        gestorCiudadanos.crearCiudadano("C-013", "Carmen Ruiz", gestorEdificios.getEdificios().get(2));
        gestorCiudadanos.crearCiudadano("C-014", "Fernando Díaz", gestorEdificios.getEdificios().get(2));
        gestorCiudadanos.crearCiudadano("C-015", "Patricia Cruz", gestorEdificios.getEdificios().get(2));

        gestorCiudadanos.crearCiudadano("C-016", "Ricardo Morales", gestorEdificios.getEdificios().get(3));
        gestorCiudadanos.crearCiudadano("C-017", "Adriana Reyes", gestorEdificios.getEdificios().get(3));
        gestorCiudadanos.crearCiudadano("C-018", "Oscar Ortega", gestorEdificios.getEdificios().get(3));
        gestorCiudadanos.crearCiudadano("C-019", "Daniela Silva", gestorEdificios.getEdificios().get(3));
        gestorCiudadanos.crearCiudadano("C-020", "Andrés Castro", gestorEdificios.getEdificios().get(3));

        // Crear robots
        for (int i = 0; i < 15; i++) {
            int bateria = 20 + (int)(Math.random() * 80); // Batería entre 20% y 100%
            gestorRobots.crearRobot(true, bateria, null);
        }

        // Asignar robots a ciudadanos
        for (int i = 0; i < 15; i++) {
            String ciudadanoId = "C-" + String.format("%03d", (i + 1));
            gestorRobots.asignarRobot(gestorCiudadanos, ciudadanoId);
        }

        // Generar drones
        gestorDrones.generarDrones(gestorEdificios.getEdificios());

        System.out.println("Datos de prueba creados exitosamente:");
        System.out.println("- " + gestorEdificios.getEdificios().size() + " edificios");
        System.out.println("- " + gestorEstacion.getEstaciones().size() + " estaciones de recarga");
        System.out.println("- " + gestorCiudadanos.listarCiudadanos().size() + " ciudadanos");
        System.out.println("- " + gestorRobots.getRobots().size() + " robots");
        System.out.println("- " + gestorDrones.getDrones().size() + " drones");

        // Mostrar estadísticas de asignación de robots
        int ciudadanosConRobot = 0;
        for (Ciudadano ciudadano : gestorCiudadanos.listarCiudadanos()) {
            if (ciudadano.tieneRobotsAsignados()) {
                ciudadanosConRobot++;
            }
        }
        System.out.println("- " + ciudadanosConRobot + " ciudadanos con robot asignado");
        System.out.println("- " + (gestorCiudadanos.listarCiudadanos().size() - ciudadanosConRobot) + " ciudadanos sin robot");
    }

    // Getters para los gestores
    public GestorCiudadanos getGestorCiudadanos() { return gestorCiudadanos; }
    public GestorDrones getGestorDrones() { return gestorDrones; }
    public GestorEstacion getGestorEstacion() { return gestorEstacion; }
    public GestorRobots getGestorRobots() { return gestorRobots; }
    public GestorEdificios getGestorEdificios() { return gestorEdificios; }
    public ConsejoInteligencia getConsejoInteligencia() { return consejoInteligencia; }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            Aplicacion app = new Aplicacion();
            PantallaPrincipal pantalla = new PantallaPrincipal(app);
            pantalla.setVisible(true);
        });
    }
}